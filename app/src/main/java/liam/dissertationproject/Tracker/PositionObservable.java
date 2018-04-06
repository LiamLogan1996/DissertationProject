/*
 * Created by Liam Logan on 05/04/18 18:08
 * Copyright (c) 2018. All rights reserved.
 *
 * Last modified 22/03/18 21:56
 */

package liam.dissertationproject.Tracker;

import java.util.Observable;

public class PositionObservable extends Observable {

    private boolean isTracking;

    public boolean get() {
        return isTracking;
    }

    public void setBoolean(boolean track) {

        isTracking = track;
        setChanged();
    }
}
