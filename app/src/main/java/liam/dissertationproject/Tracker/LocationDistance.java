/*
 * Created by Liam Logan on 05/04/18 18:08
 * Copyright (c) 2018. All rights reserved.
 *
 * Last modified 22/03/18 21:56
 */

package liam.dissertationproject.Tracker;

public class LocationDistance {
	private double distance;
	private String location;

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