/*
 * Created by Liam Logan
 * Copyright (c) 2018. All Rights reserved
 *
 *
 */

package liam.dissertationproject.Positioning;

import android.graphics.PointF;

import java.util.Observable;

public class BitmapCoordinates extends Observable {

    // PointF Object to hold position Coordinates
    private PointF bitmapCoordPoints;

    // Public constructor for the use of PointF
    public BitmapCoordinates(float x, float y) {
        bitmapCoordPoints = new PointF(x, y);
    }

    public PointF get() {
        return bitmapCoordPoints;
    }

    // This method allows the coordinates from the positioning class to be passed. The coordinates
    // can then be passed to the imageZoomView to be drawn.
    public void setPositionCoordinates(float x, float y) {

        if (x != bitmapCoordPoints.x || y != bitmapCoordPoints.y) {
            bitmapCoordPoints.x = x;
            bitmapCoordPoints.y = y;
            setChanged();
        }
    }
}