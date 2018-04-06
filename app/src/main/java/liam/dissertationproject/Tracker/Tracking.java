/*
 * Created by Liam Logan on 05/04/18 18:08
 * Copyright (c) 2018. All rights reserved.
 *
 * Last modified 04/04/18 14:24
 */

package liam.dissertationproject.Tracker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;

import liam.dissertationproject.zoom.BitmapCoordinates;
import liam.dissertationproject.zoom.DynamicZoomControl;
import liam.dissertationproject.zoom.ImageZoomView;
import liam.dissertationproject.zoom.LongPressZoomListener;


public class Tracking implements Observer {

	private final BitmapCoordinates curPosition = new BitmapCoordinates(-1, -1);

	private InputStream imagePath;
	private String floorPlanWidth;
	private String floorPlanHeight;

	/** Image zoom view */
	public ImageZoomView zoomView;

	/** Zoom control */
	private DynamicZoomControl zoomControl;

	/** Decoded bitmap image */
	private Bitmap bitmap;

	/** On touch listener for zoom view */
	private LongPressZoomListener zoomListener;

	private PositionObservable trackMe;

	public Tracking(LocateMe fm) {

		// Set Zooming and Panning Settings
		zoomControl = new DynamicZoomControl();
		zoomListener = new LongPressZoomListener(fm.getApplicationContext());
		zoomListener.setZoomControl(zoomControl);
		zoomView = (ImageZoomView) fm.findViewById(R.id.zoomview);
		zoomView.setZoomState(zoomControl.getZoomState());
		zoomView.setOnTouchListener(zoomListener);
		zoomView.setCurrentPoint(curPosition);

		zoomControl.setAspectQuotient(zoomView.getAspectQuotient());



	}

	public void setTrackMe(PositionObservable trackMe) {
		zoomView.setTrackingPosition(trackMe);
		
		if (this.trackMe != null) {
			this.trackMe.deleteObserver(this);
		}
		this.trackMe = trackMe;
		this.trackMe.addObserver(this);
	}

	public boolean setFloorPlan(InputStream imagePath, String building_width, String building_height) {



		if (imagePath == null || imagePath.equals(""))
			return false;

		if (this.imagePath != null && this.imagePath.equals(imagePath))
			return true;

		Bitmap tempBitmap = BitmapFactory.decodeStream(imagePath);

		if (tempBitmap != null) {

			if (bitmap != null)
				bitmap.recycle();

			System.gc();

			this.imagePath = imagePath;
			this.bitmap = tempBitmap;
			this.floorPlanWidth = building_width;
			this.floorPlanHeight = building_height;
			zoomView.setImage(this.bitmap);
			resetZoomState();
			resetLocation();
		} else
			return false;

        return true;
	}

	public boolean positionOnMap(String Geolocation) {

		if (Geolocation == null || bitmap == null || floorPlanWidth == null || floorPlanHeight == null)
			return false;

		String coordinates[] = Geolocation.replace(", ", " ").split(" ");
		float x, y;
		float bitmapWidth;
		float bitmapHeight;

		try {
			x = Float.parseFloat(coordinates[0]);
			y = Float.parseFloat(coordinates[1]);
			bitmapWidth = Float.parseFloat(floorPlanWidth);
			bitmapHeight = Float.parseFloat(floorPlanHeight);
		} catch (Exception e) {
			return false;
		}

		// Clear all overlays if it is not tracking
		if (!trackMe.get())
			zoomView.clearRecordedPoints();

		int xPixels = (int) ((x * bitmap.getWidth()) / bitmapWidth);
		int yPixels = (int) ((y * bitmap.getHeight()) / bitmapHeight);
		curPosition.setPositionCoordinates(xPixels, yPixels);
		curPosition.notifyObservers();


		return true;
	}

	protected void onDestroy() {

		if (bitmap != null)
			bitmap.recycle();
		if (zoomView != null)
			zoomView.setOnTouchListener(null);
		if (zoomControl != null)
			zoomControl.getZoomState().deleteObservers();
	}

	/**
	 * Reset zoom state and notify observers
	 */
	private void resetZoomState() {
        zoomControl.getZoomState().setPanX(0.5f);
        zoomControl.getZoomState().setPanY(0.5f);
        zoomControl.getZoomState().setZoom(2f);
        zoomControl.getZoomState().notifyObservers();
		zoomView.clearRecordedPoints();
	}


	private void resetLocation() {
		curPosition.setPositionCoordinates(-1, -1);
		curPosition.notifyObservers();
	}

	@Override
	public void update(Observable observable, Object data) {
		// Clear all overlays if it is not tracking
		if (!trackMe.get()) {
			zoomView.clearRecordedPoints();
		}

	}

	public boolean okBuildingSettings() {
		return this.bitmap != null && this.floorPlanHeight != null && this.floorPlanWidth != null;
	}

}
