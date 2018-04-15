/*
 * Created by Liam Logan
 * Copyright (c) 2018. All Rights reserved
 *
 *
 */

package liam.dissertationproject.Positioning;

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
