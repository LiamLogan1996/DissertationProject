

package liam.dissertationproject.zoom;

import android.graphics.PointF;

import java.util.Observable;

public class BitmapCoordinates extends Observable {

	private PointF bitmapCoordPoints;

	public BitmapCoordinates(float x, float y) {
		bitmapCoordPoints = new PointF(x, y);
	}

	public PointF get() {
		return bitmapCoordPoints;
	}

	public void setPositionCoordinates(float x, float y) {

		if (x != bitmapCoordPoints.x || y != bitmapCoordPoints.y) {
			bitmapCoordPoints.x = x;
			bitmapCoordPoints.y = y;
			setChanged();
		}
	}
}