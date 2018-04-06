/*
 * Created by Liam Logan on 05/04/18 18:08
 * Copyright (c) 2018. All rights reserved.
 *
 * Last modified 30/03/18 16:17
 */

package liam.dissertationproject.Tracker;

/**
 * Created by liaml on 31/01/2018.
 */


public class Destination {

    private String room;
    private float locationY;
    private float locationX;

    public Destination(String room, float locationX, float locationY) {

        this.room = room;
        this.locationX = locationX;
        this.locationY = locationY;
    }


    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public float getLocationY() {
        return locationY;
    }

    public void setLocationY(float locationY) {
        this.locationY = locationY;
    }

    public float getLocationX() {
        return locationX;
    }

    // Set the X Location in the onDraw( I think)
    public void setLocationX(float locationX) {
        this.locationX = locationX;
    }

    public String toString(){
        return "The Room is: " + room + "The Y is:" + locationY +
        "The X is: " + locationX;
    }
}
