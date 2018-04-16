/*
 * Created by Liam Logan
 * Copyright (c) 2018. All Rights reserved
 *
 */

package liam.dissertationproject.Algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import liam.dissertationproject.Positioning.AccessPointRecords;
import liam.dissertationproject.Positioning.LocationDistance;
import liam.dissertationproject.Positioning.RadioMap;


public class PositioningAlgorithms {

    /**
     * @param latestScanList the current scan list of APs
     * @param RM             the constructed Radio Map
     * @param algorithm      choice of several algorithms
     * @return the location of user
     */
    public static String ProcessAlgorithm(ArrayList<AccessPointRecords> latestScanList, RadioMap RM, int algorithm) {

        int i, j;

        ArrayList<String> MACAddressList = RM.getMACAddress();
        ArrayList<String> ObservedRSS = new ArrayList<String>();
        AccessPointRecords accessPointsTempList;
        int notFoundCounter = 0;

        // Read parameter of algorithm
        String NaNValue = readParameter(RM.getRadioMapFile(), 0);

        // Check which MAC addresses of radio map, we are currently listening.
        for (i = 0; i < MACAddressList.size(); ++i) {

            for (j = 0; j < latestScanList.size(); ++j) {
                accessPointsTempList = latestScanList.get(j);
                // MAC Address Matched
                if (MACAddressList.get(i).compareTo(accessPointsTempList.getBSSID()) == 0) {
                    ObservedRSS.add(String.valueOf(accessPointsTempList.getRSS()));
                    break;
                }
            }
            // A MAC Address is missing so we replace with NaN from algorithm parameters
            if (j == latestScanList.size()) {
                ObservedRSS.add(String.valueOf(NaNValue));
                ++notFoundCounter;
            }
        }

        if (notFoundCounter == MACAddressList.size())
            return null;

        // Read parameter from parameter file associated with chosen algorithm.
        String parameter = readParameter(RM.getRadioMapFile(), algorithm);

        if (parameter == null)
            return null;

        // Switch Function has been used to allow other algorithms to be potentially
        // implemented at a later data
        switch (algorithm) {
            case 1:
                return KNNAlgorithm(RM, ObservedRSS, parameter);
        }
        return null;

    }

    /**
     * Calculates user location based on K Nearest Neighbour (KNN) Algorithm
     *
     * @param RM          radio map
     * @param ObservedRSS RSS values currently observed
     * @param parameter   Algorithm Specific Parameters are applied if necessary
     * @return The estimated user location
     */
    private static String KNNAlgorithm(RadioMap RM, ArrayList<String> ObservedRSS, String parameter) {

        ArrayList<String> RSSValues;
        float distance = 0;
        ArrayList<LocationDistance> EuclideanDistanceLocationResult = new ArrayList<LocationDistance>();
        String position = null;
        int K;

        try {
            K = Integer.parseInt(parameter);
        } catch (Exception e) {
            return null;
        }

        // Construct a list with locations-distances pairs for currently
        // observed RSS values
        for (String location : RM.getLocationRSSPairs().keySet()) {
            RSSValues = RM.getLocationRSSPairs().get(location);
            distance = EuclideanDistance(RSSValues, ObservedRSS);

            if (distance == Float.NEGATIVE_INFINITY)
                return null;

            // New ArrayList to assign the euclidean to the location-distance pairs.
            EuclideanDistanceLocationResult.add(0, new LocationDistance(distance, location));
        }

        // Sort locations-distances pairs based on minimum distances
        Collections.sort(EuclideanDistanceLocationResult, new Comparator<LocationDistance>() {

            // The compare function allows two different locationDistances to be compared. Collection.sort
            // utilises the insertion sort to sort the elements.
            public int compare(LocationDistance distanceResult1, LocationDistance distanceResult2) {
                return (Double.compare(distanceResult1.getDistance(), distanceResult2.getDistance()));
            }
        });


        // The position is the two coordinates which are passed from the averageKCalculated method
        position = averageKCalculatedPosition(EuclideanDistanceLocationResult, K);

        return position;

    }

    /**
     * Calculates the Euclidean distance between the currently observed RSS
     * values and the RSS values for a specific location.
     *
     * @param RSSValueRM       RSS values of a location in radiomap
     * @param RSSValueObserved RSS values currently observed
     * @return The Euclidean distance, or MIN_VALUE for error
     */
    private static float EuclideanDistance(ArrayList<String> RSSValueRM, ArrayList<String> RSSValueObserved) {

        float finalDistance = 0;
        float RSS1;
        float RSS2;
        float temp;
        String str;

        // For loop to loop through all the RSSValues from the radioMap. Each value can be from the
        // RM can then be compared with the currently observed.
        for (int i = 0; i < RSSValueRM.size(); ++i) {

            try {
                str = RSSValueRM.get(i);
                RSS1 = Float.valueOf(str.trim());
                str = RSSValueObserved.get(i);
                RSS2 = Float.valueOf(str.trim());
            } catch (Exception e) {
                return Float.NEGATIVE_INFINITY;
            }

            // store temporary value of latest result. The need for a temp value is because there
            // are many APs for which the Euclidean distance need to be calculated. Therefore each
            // time a new value better value is determined, it is able to replace the previous

            temp = RSS1 - RSS2;
            temp *= temp;

            // add temporary result to the finalDistance value.
            finalDistance += temp;
        }
        return ((float) Math.sqrt(finalDistance));
    }

    /**
     * Calculates the Average of the K locations that have the shortest
     * distances D
     *
     * @param LocationDistanceResults Locations-Distances pairs sorted by distance
     * @param K                       The number of locations used
     * @return The estimated user location, or null for error
     */
    private static String averageKCalculatedPosition(ArrayList<LocationDistance> LocationDistanceResults, int K) {

        float averageXCoordinate = 0.0f;
        float averageYCoordinate = 0.0f;

        String[] LocationArray = new String[2];
        float xCoordinate, yCoordinate;

        // The minimum K value to be used within the algorithm
        int minimumK;
        if (K < LocationDistanceResults.size()) minimumK = K;
        else minimumK = LocationDistanceResults.size();

        // Calculate the sum of X and Y
        for (int i = 0; i < minimumK; ++i) {
            LocationArray = LocationDistanceResults.get(i).getLocation().split(" ");

            // Store Coordinate Elements into the location
            try {
                xCoordinate = Float.valueOf(LocationArray[0].trim());
                yCoordinate = Float.valueOf(LocationArray[1].trim());
            } catch (Exception e) {
                return null;
            }

            // Cast arrayList values to averageCoordinate Variable
            averageXCoordinate += xCoordinate;
            averageYCoordinate += yCoordinate;
        }

        // Calculate the average of coordinates based on locations used(K Value)
        averageXCoordinate /= minimumK;
        averageYCoordinate /= minimumK;

        return averageXCoordinate + " " + averageYCoordinate;

    }


    /**
     * Reads the parameters from the file
     *
     * @param file      the file of radiomap
     * @param algorithm choice of algorithm
     * @return The parameter for the algorithm
     */
    private static String readParameter(File file, int algorithm) {

        String line;
        BufferedReader reader = null;
        FileReader fr = null;

        String parameter = null;

        try {
            fr = new FileReader(file.getAbsolutePath() + "Parameters");
            reader = new BufferedReader(fr);

            while ((line = reader.readLine()) != null) {

                // Remove labels from file
                if (line.startsWith("#") || line.trim().equals("")) {
                    continue;
                }

                //Split the fields based on the :
                String[] temp = line.split(":");

                if (temp.length != 2) {
                    return null;
                }

                // This fills the algorithms chosen with appropriate values from algorithm
                // parameters
                if (algorithm == 0 && temp[0].equals("NaN")) {
                    parameter = temp[1];
                    break;
                } else if (algorithm == 1 && temp[0].equals("KNN")) {
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
