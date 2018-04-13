

/*
 * Created by Liam Logan
 * Copyright (c) 2018. All Rights reserved
 *
 * Last Modified .12/04/18 13:31
 */

package liam.dissertationproject.ZoomFunction;

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