/*
 * Created by Liam Logan
 * Copyright (c) 2018. All Rights reserved
 *
 *
 */

package liam.dissertationproject.Positioning;


/**
 * Constructor to store the values of the location-distance pairs which are used in the KNN Algorithm
 */
public class LocationDistance {
    private double distance;
    private String location;

    /**
     * @param distance double distance - Distance of user from estimated position
     * @param location String location, This will be the coordinates of the locations
     */
    public LocationDistance(double distance, String location) {
        this.distance = distance;
        this.location = location;
    }

    public double getDistance() {
        return distance;
    }

    public String getLocation() {
        return location;
    }
}