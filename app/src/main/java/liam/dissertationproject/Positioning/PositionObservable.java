/*
 * Created by Liam Logan
 * Copyright (c) 2018. All Rights reserved
 *
 *
 */

package liam.dissertationproject.Positioning;

import java.util.Observable;

public class PositionObservable extends Observable {

    /**
     *  Boolean to determine if application should position the user or not
     */
    private boolean isPositioning;

    public boolean get() {
        return isPositioning;
    }

    /**
     *
     * @param position Allocates the boolean object to represent the value position
     */
    public void setBoolean(boolean position) {

        isPositioning = position;
        setChanged();
    }
}
