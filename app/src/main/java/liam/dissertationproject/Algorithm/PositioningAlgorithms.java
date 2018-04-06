
/*
 * Created by Liam Logan on 05/04/18 18:08
 * Copyright (c) 2018. All rights reserved.
 *
 * Last modified 04/04/18 16:10
 */

package liam.dissertationproject.Algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import liam.dissertationproject.Tracker.AccessPointRecords;
import liam.dissertationproject.Tracker.LocationDistance;
import liam.dissertationproject.Tracker.RadioMap;


public class PositioningAlgorithms {

	/**
	 * 
	 * @param latestScanList
	 *            the current scan list of APs
	 * @param RM
	 *            the constructed Radio Map
	 * 
	 * @param algorithm
	 *            choice of several algorithms
	 * 
	 * @return the location of user
	 */
	public static String AlgorithmProcessor(ArrayList<AccessPointRecords> latestScanList, RadioMap RM, int algorithm) {

		int i, j;

		ArrayList<String> MACAddressList = RM.getMacAdressList();
		ArrayList<String> ObservedRSS = new ArrayList<String>();
		AccessPointRecords accessPointsTempList;
		int notFoundCounter = 0;
		// Read parameter of algorithm
		String NaNValue = readParameter(RM.getRadioMapFile(), 0);
		// Check which mac addresses of radio map, we are currently listening.
		for (i = 0; i < MACAddressList.size(); ++i) {

			for (j = 0; j < latestScanList.size(); ++j) {
				accessPointsTempList = latestScanList.get(j);
				// MAC Address Matched
				if (MACAddressList.get(i).compareTo(accessPointsTempList.getBssid()) == 0) {
					ObservedRSS.add(String.valueOf(accessPointsTempList.getRss()));
					break;
				}
			}
			// A MAC Address is missing so we place a small value, NaN value
			if (j == latestScanList.size()){
				ObservedRSS.add(String.valueOf(NaNValue));
				++notFoundCounter;
			}
		}
		if (notFoundCounter == MACAddressList.size())
			return null;
		// Read parameter of algorithm
		String parameter = readParameter(RM.getRadioMapFile(), algorithm);

		if (parameter == null)
			return null;
		switch (algorithm) {
		case 1:
			return KNNAlgorithm(RM, ObservedRSS, parameter);
		}
		return null;

	}

	/**
	 * Calculates user location based on Weighted/Not Weighted K Nearest
	 * Neighbor (KNN) Algorithm
	 * 
	 * @param RM
	 *            The radio map structure
	 * 
	 * @param CurrentlyObservedRSS
	 *            RSS values currently observed
	 * @param parameter
	 *
	 * 
	 * @return The estimated user location
	 */
	private static String KNNAlgorithm(RadioMap RM, ArrayList<String> CurrentlyObservedRSS, String parameter) {

		ArrayList<String> RSSValues;
		float curResult = 0;
		ArrayList<LocationDistance> LocDistanceResult = new ArrayList<LocationDistance>();
		String myLocation = null;
		int K;

		try {
			K = Integer.parseInt(parameter);
		} catch (Exception e) {
			return null;
		}

		// Construct a list with locations-distances pairs for currently
		// observed RSS values
		for (String location : RM.getLocationRSS().keySet()) {
			RSSValues = RM.getLocationRSS().get(location);
			curResult = EuclideanDistance(RSSValues, CurrentlyObservedRSS);

			if (curResult == Float.NEGATIVE_INFINITY)
				return null;

			LocDistanceResult.add(0, new LocationDistance(curResult, location));
		}

		// Sort locations-distances pairs based on minimum distances
		Collections.sort(LocDistanceResult, new Comparator<LocationDistance>() {

			public int compare(LocationDistance distanceResult1, LocationDistance distanceResult2) {
				return (distanceResult1.getDistance() > distanceResult2.getDistance() ? 1 :
						(distanceResult1.getDistance() == distanceResult2.getDistance() ? 0 : -1));

				//Potential more efficient code
				//return (Double.compare(distanceResult1.getDistance(), distanceResult2.getDistance()));
			}
		});

			myLocation = averageKCalculatedPosition(LocDistanceResult, K);

		return myLocation;

	}
	/**
	 * Calculates the Euclidean distance between the currently observed RSS
	 * values and the RSS values for a specific location.
	 * 
	 * @param l1
	 *            RSS values of a location in radiomap
	 * @param l2
	 *            RSS values currently observed
	 * 
	 * @return The Euclidean distance, or MIN_VALUE for error
	 */
	private static float EuclideanDistance(ArrayList<String> l1, ArrayList<String> l2) {

		float finalResult = 0;
		float v1;
		float v2;
		float temp;
		String str;

		for (int i = 0; i < l1.size(); ++i) {

			try {
				str = l1.get(i);
				v1 = Float.valueOf(str.trim()).floatValue();
				str = l2.get(i);
				v2 = Float.valueOf(str.trim()).floatValue();
			} catch (Exception e) {
				return Float.NEGATIVE_INFINITY;
			}

			// do the procedure
			temp = v1 - v2;
			temp *= temp;

			// do the procedure
			finalResult += temp;
		}
		return ((float) Math.sqrt(finalResult));
	}

	/**
	 * Calculates the Average of the K locations that have the shortest
	 * distances D
	 * 
	 * @param LocDistance_Results_List
	 *            Locations-Distances pairs sorted by distance
	 * @param K
	 *            The number of locations used
	 * @return The estimated user location, or null for error
	 */
	private static String averageKCalculatedPosition(ArrayList<LocationDistance> LocDistance_Results_List, int K) {

		float sumX = 0.0f;
		float sumY = 0.0f;

		String[] LocationArray = new String[2];
		float x, y;

		int K_Min = K < LocDistance_Results_List.size() ? K : LocDistance_Results_List.size();

		// Calculate the sum of X and Y
		for (int i = 0; i < K_Min; ++i) {
			LocationArray = LocDistance_Results_List.get(i).getLocation().split(" ");

			try {
				x = Float.valueOf(LocationArray[0].trim()).floatValue();
				y = Float.valueOf(LocationArray[1].trim()).floatValue();
			} catch (Exception e) {
				return null;
			}

			sumX += x;
			sumY += y;
		}

		// Calculate the average
		sumX /= K_Min;
		sumY /= K_Min;

		return sumX + " " + sumY;

	}


	/**
	 * Reads the parameters from the file
	 * 
	 * @param file
	 *            the file of radiomap, to read parameters
	 * 
	 * @param algorithm_choice
	 *            choice of several algorithms
	 * 
	 * @return The parameter for the algorithm
	 * 
	 */
	private static String readParameter(File file, int algorithm_choice) {

		String line;
		BufferedReader reader = null;
		FileReader fr = null;

		String parameter = null;

		try {
			fr = new FileReader(file.getAbsolutePath() + "-parameters");
			reader = new BufferedReader(fr);

			while ((line = reader.readLine()) != null) {

				/* Ignore the labels */
				if (line.startsWith("#") || line.trim().equals("")) {
					continue;
				}

				/* Split fields */
				String[] temp = line.split(":");

				/* The file may be corrupted so ignore reading it */
				if (temp.length != 2) {
					return null;
				}

				if (algorithm_choice == 0 && temp[0].equals("NaN")) {
					parameter = temp[1];
					break;
				} else if (algorithm_choice == 1 && temp[0].equals("KNN")) {
					parameter = temp[1];
					break;
				}

			}
			fr.close();
			reader.close();
		} catch (Exception e) {
			return null;
		}

		return parameter;
	}

}
