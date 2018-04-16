/*
 * Created by Liam Logan
 * Copyright (c) 2018. All Rights reserved
 *
 */

package liam.dissertationproject.Positioning;

// Constructor class used for the positioning of the destination marker
public class Destination {

    // Three Variables used for placement of marker
    private String room;
    private float locationY;
    private float locationX;

    // Constructor
    public Destination(String room, float locationX, float locationY) {

        this.room = room;
        this.locationX = locationX;
        this.locationY = locationY;
    }

    // Get Y for the Y position on bitmap
    public float getLocationY() {
        return locationY;
    }

    // Get X for the X position on bitmap
    public float getLocationX() {
        return locationX;
    }

    // toString method to test if correct values are being passed
    public String toString() {
        return "The Room is: " + room + "The Y is:" + locationY +
                "The X is: " + locationX;
    }
}
