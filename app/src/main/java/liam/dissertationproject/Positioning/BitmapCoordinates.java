
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