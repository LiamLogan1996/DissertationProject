
package liam.dissertationproject.zoom;

import android.content.Context;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Listener for controlling zoom state through touch events
 */
public class LongPressZoomListener implements View.OnTouchListener {

	/**
	 * Enum defining listener modes. Before the view is touched the listener is
	 * in the UNDEFINED mode. Once touch starts it can enter either one of the
	 * other two modes: If the user scrolls over the view the listener will
	 * enter PAN mode, if the user lets his finger rest and makes a longpress
	 * the listener will enter ZOOM mode.
	 */
	private enum Mode {
		UNDEFINED, PAN, ZOOM
	}

	/** Time of tactile feedback vibration when entering zoom mode */
	private static final long VIBRATE_TIME = 50;

	/** Current listener mode */
	private Mode mMode = Mode.UNDEFINED;

	/** Zoom control to manipulate */
	private DynamicZoomControl mZoomControl;

	/** X-coordinate of previously handled touch event */
	private float mX;

	/** Y-coordinate of previously handled touch event */
	private float mY;

	/** X-coordinate of latest down event */
	private float mDownX;

	/** Y-coordinate of latest down event */
	private float mDownY;

	/** Velocity tracker for touch events */
	private VelocityTracker mVelocityTracker;

	/** Distance touch can wander before we think it's scrolling */
	private final int mScaledTouchSlop;

	/** Duration in ms before a press turns into a long press */
	private final int mLongPressTimeout;

	/** Vibrator for tactile feedback */
	private final Vibrator mVibrator;

	/** Maximum velocity for fling */
	private final int mScaledMaximumFlingVelocity;

	private final BitmapCoordinates pointClicked = new BitmapCoordinates(0, 0);

	/**
	 * Creates a new instance
	 * 
	 * @param context
	 *            Application context
	 */
	public LongPressZoomListener(Context context) {
		mLongPressTimeout = ViewConfiguration.getLongPressTimeout();
		mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mScaledMaximumFlingVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
		mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
	}

	/**
	 * Sets the zoom control to manipulate
	 * 
	 * @param control
	 *            Zoom control
	 */
	public void setZoomControl(DynamicZoomControl control) {
		mZoomControl = control;
	}


    public BitmapCoordinates getClickPoint() { return pointClicked; }

    /**
	 * Runnable that enters zoom mode
	 */
	private final Runnable mLongPressRunnable = new Runnable() {
		public void run() {
			mMode = Mode.ZOOM;
			mVibrator.vibrate(VIBRATE_TIME);
		}
	};

	// implements View.OnTouchListener
	public boolean onTouch(View v, MotionEvent event) {
		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mZoomControl.stopFling();
			v.postDelayed(mLongPressRunnable, mLongPressTimeout);
			mDownX = x;
			mDownY = y;
			mX = x;
			mY = y;
			break;

		case MotionEvent.ACTION_MOVE: {
			final float dx = (x - mX) / v.getWidth();
			final float dy = (y - mY) / v.getHeight();
			
			if (mMode == Mode.ZOOM) {
				mZoomControl.zoom((float) Math.pow(20, -dy), mDownX / v.getWidth(), mDownY / v.getHeight());
			} else if (mMode == Mode.PAN) {
				mZoomControl.pan(-dx, -dy);
			} else {
				final float scrollX = mDownX - x;
				final float scrollY = mDownY - y;

				final float dist = (float) Math.sqrt(scrollX * scrollX + scrollY * scrollY);

				if (dist >= mScaledTouchSlop) {
					v.removeCallbacks(mLongPressRunnable);
					mMode = Mode.PAN;
				}
			}

			mX = x;
			mY = y;
			break;
		}

		case MotionEvent.ACTION_UP:
			if (mMode == Mode.PAN) {
				mVelocityTracker.computeCurrentVelocity(1000, mScaledMaximumFlingVelocity);
				mZoomControl.startFling(-mVelocityTracker.getXVelocity() / v.getWidth(), -mVelocityTracker.getYVelocity() / v.getHeight());
			} else {
				mZoomControl.startFling(0, 0);
			}
			mVelocityTracker.recycle();
			mVelocityTracker = null;
			v.removeCallbacks(mLongPressRunnable);
			mMode = Mode.UNDEFINED;

			int xDiff = (int) Math.abs(x-mDownX);
			int yDiff = (int) Math.abs(y-mDownY);
			if(xDiff < 8 && yDiff <8){
				pointClicked.setPositionCoordinates(x,y);
				pointClicked.notifyObservers();
			}
			break;


		case MotionEvent.ACTION_POINTER_DOWN:
			mMode = Mode.ZOOM;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mMode = Mode.UNDEFINED;
			break;

		default:
			mVelocityTracker.recycle();
			mVelocityTracker = null;
			v.removeCallbacks(mLongPressRunnable);
			mMode = Mode.UNDEFINED;
			break;

		}

		return true;
	}


}
