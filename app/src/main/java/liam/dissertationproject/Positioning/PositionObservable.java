/*
 * Created by Liam Logan
 * Copyright (c) 2018. All Rights reserved
 *
 * Last Modified .12/04/18 13:50
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
