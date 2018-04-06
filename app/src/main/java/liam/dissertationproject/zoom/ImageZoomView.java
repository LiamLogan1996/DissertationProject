/*
 * Created by Liam Logan on 05/04/18 18:08
 * Copyright (c) 2018. All rights reserved.
 *
 * Last modified 05/04/18 15:12
 */

package liam.dissertationproject.zoom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import liam.dissertationproject.Tracker.PositionObservable;
import liam.dissertationproject.Tracker.R;


/**
 * View capable of drawing an image at different zoom state levels
 */
public class ImageZoomView extends View implements Observer {

	/** Paint object used when drawing bitmap. */
	private final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);

	/** Rectangle used (and re-used) for cropping source image. */
	private final Rect rectSrc = new Rect();

	/** Rectangle used (and re-used) for specifying drawing area on canvas. */
	private final Rect rectDst = new Rect();

	/** Object holding aspect quotient */
	private final AspectQuotient aspectQuotient = new AspectQuotient();

	/** The bitmap that we're zooming in, and drawing on the screen. */
	private Bitmap bitmap;

	/** State of the zoom. */
	private ZoomState state;

	// Public methods
	private BitmapCoordinates currentPoint;
	private ArrayList<PointF> recordedPoints = new ArrayList<PointF>();

	private final Paint p = new Paint();

	public ArrayList<PointF> destinationPoints = new ArrayList<PointF>();

	private PositionObservable positionMe;

	/**
	 * Constructor
	 */
	public ImageZoomView(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

    /**
     *
     * @param click the parameter passes the x and y which have been set from the Tracking class
     */
	public void setCurrentPoint(BitmapCoordinates click) {

		if (currentPoint != null) {
			currentPoint.deleteObserver(this);
		}
		currentPoint = click;
		currentPoint.addObserver(this);

		invalidate();
	}

    /**
	 * Set image bitmap
	 * 
	 * @param bitmap
	 *            The bitmap to view and zoom into
	 */
	public void setImage(Bitmap bitmap) {
		this.bitmap = bitmap;

		aspectQuotient.updateAspectQuotient(getWidth(), getHeight(), this.bitmap.getWidth(), this.bitmap.getHeight());
		aspectQuotient.notifyObservers();
		recordedPoints.clear();
		invalidate();
	}

	/**
	 * Set object holding the zoom state that should be used
	 * 
	 * @param state
	 *            The zoom state
	 */
	public void setZoomState(ZoomState state) {
		if (this.state != null) {
			this.state.deleteObserver(this);
		}

		this.state = state;
		this.state.addObserver(this);

		invalidate();
	}

	/**
	 * Gets reference to object holding aspect quotient
	 * 
	 * @return Object holding aspect quotient
	 */
	public AspectQuotient getAspectQuotient() {
		return aspectQuotient;
	}

	// Superclass overrides
	@Override
	protected void onDraw(Canvas canvas) {
		if (bitmap != null && state != null) {
			final float aspectQuotient = this.aspectQuotient.get();

			final int viewWidth = getWidth();
			final int viewHeight = getHeight();
			final int bitmapWidth = bitmap.getWidth();
			final int bitmapHeight = bitmap.getHeight();

			final float panX = state.getPanX();
			final float panY = state.getPanY();
			final float zoomX = state.getZoomX(aspectQuotient) * viewWidth / bitmapWidth;
			final float zoomY = state.getZoomY(aspectQuotient) * viewHeight / bitmapHeight;

			// Setup source and destination rectangles
			rectSrc.left = (int) (panX * bitmapWidth - viewWidth / (zoomX * 2));
			rectSrc.top = (int) (panY * bitmapHeight - viewHeight / (zoomY * 2));
			rectSrc.right = (int) (rectSrc.left + viewWidth / zoomX);
			rectSrc.bottom = (int) (rectSrc.top + viewHeight / zoomY);

			rectDst.left = getLeft();
			rectDst.top = getTop();
			rectDst.right = getRight();
			rectDst.bottom = getBottom();

			// Adjust source rectangle so that it fits within the source image.
			if (rectSrc.left < 0) {
				rectDst.left += -rectSrc.left * zoomX;
				rectSrc.left = 0;
			}
			if (rectSrc.right > bitmapWidth) {
				rectDst.right -= (rectSrc.right - bitmapWidth) * zoomX;
				rectSrc.right = bitmapWidth;
			}
			if (rectSrc.top < 0) {
				rectDst.top += -rectSrc.top * zoomY;
				rectSrc.top = 0;
			}
			if (rectSrc.bottom > bitmapHeight) {
				rectDst.bottom -= (rectSrc.bottom - bitmapHeight) * zoomY;
				rectSrc.bottom = bitmapHeight;
			}

			//From this point, This is my own work. The previous 186 line have been obtained
            // from the open source code which has been discussed in my dissertation project.

			float XDimensions = (float) (rectSrc.right - rectSrc.left) / (float) (rectDst.right - rectDst.left);
			float YDimensions = (float) (rectSrc.bottom - rectSrc.top) / (float) (rectDst.bottom - rectDst.top);

            // This draws the floor plan within the specified region
			canvas.drawBitmap(bitmap, rectSrc, rectDst, paint);


			// Used to draw the destination marker onto the bitmap
			for (int i = 0; i < destinationPoints.size(); i++) {

				Bitmap marker = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
				canvas.drawBitmap(marker, (destinationPoints.get(i).x - rectSrc.left) / XDimensions + rectDst.left, ((destinationPoints.get(i).y - rectSrc.top) / YDimensions)
						+ rectDst.top, null);

			}

            // If the coordinates are within the bitmap dimensions then carry out drawing of positioning marker
			if (currentPoint.get().x >= 0 && currentPoint.get().x <= bitmapWidth && currentPoint.get().y >= 0 && currentPoint.get().y <= bitmapHeight) {

				boolean previouslyMarked = false;
				PointF point = null;

				//This for loop checks if a point has previously been drawn
				for (int i = 0; i < recordedPoints.size(); ++i) {
					point = recordedPoints.get(i);

					if (currentPoint.get().equals(point.x, point.y)) {
						previouslyMarked = true;

						PointF pointLast = recordedPoints.get(recordedPoints.size() - 1);

						if (pointLast != point) {
							point = recordedPoints.set(i, pointLast);
							recordedPoints.set(recordedPoints.size() - 1, point);
						}
						break;
					}
				}

				// Add new point to arrayList if the current point has not been previously added
				if (!previouslyMarked)
					recordedPoints.add(new PointF(currentPoint.get().x, currentPoint.get().y));
			}

            // When the boolean is set to PositionMe, draw the trackerMarker
			for (int i = 0; i < recordedPoints.size(); ++i) {

				if (i == recordedPoints.size() - 1) {
                    if (positionMe.get()) {
                        Bitmap trackerMarker = BitmapFactory.decodeResource(getResources(), R.drawable.tracker_marker);

                        PointF temp = recordedPoints.get(i);

                        // This ensures thats bitmap which is drawn sits within the dimensions of the map and also the source square.
                        if (temp.x != -1 && temp.y != -1 && temp.x >= rectSrc.left - 5 && temp.x <= rectSrc.right + 5 && temp.y >= rectSrc.top - 5
                                && temp.y <= rectSrc.bottom + 5) {
                                //canvas.drawCircle((temp.x - rectSrc.left) / XDimensions + rectDst.left, (temp.y - rectSrc.top) / YDimensions + rectDst.top, radius, this.p);
                                canvas.drawBitmap(trackerMarker, (temp.x - rectSrc.left) / XDimensions + rectDst.left, (temp.y - rectSrc.top) / YDimensions + rectDst.top, p);
                        }
                    }
                }
			}
		}
	}


	// This adds the coordinates which have been supplied from the locate me method used to draw destination marker
    public void setPoint(float x, float y) {

	    destinationPoints.add(new PointF(x,y));
	}

   @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (bitmap != null) {
			aspectQuotient.updateAspectQuotient(right - left, bottom - top, bitmap.getWidth(), bitmap.getHeight());
			aspectQuotient.notifyObservers();
		}
	}

	// implements Observer
	public void update(Observable observable, Object data) {
	    invalidate();
	}

	// Clear the previous marked points
	public void clearRecordedPoints() {
		recordedPoints.clear();
		invalidate();
	}

	// Clear previous destination points
	public void clearDestinationPoints(){
		destinationPoints.clear();
		invalidate();
	}

	//If boolean is set to null dont continue to draw on bitmap, if true enable drawing and notify observers from
    // the other classes needed such as LocateMe and Tracking.
	public void setTrackingPosition(PositionObservable trackMe) {
		if (this.positionMe != null) {
			this.positionMe.deleteObserver(this);
		}
		this.positionMe = trackMe;
		this.positionMe.addObserver(this);
	}

}