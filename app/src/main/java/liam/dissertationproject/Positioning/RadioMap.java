/*
 * Created by Liam Logan
 * Copyright (c) 2018. All Rights reserved
 *
 */

package liam.dissertationproject.Positioning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RadioMap {

    private File RadioMapFile = null;
    private ArrayList<String> MACAddress = null;
    private HashMap<String, ArrayList<String>> LocationRSSPairs = null;
    private ArrayList<String> locationOrderList = null;

    public RadioMap() {
        super();
        MACAddress = new ArrayList<String>();
        LocationRSSPairs = new HashMap<String, ArrayList<String>>();
        locationOrderList = new ArrayList<String>();
    }

    /**
     * Getter of MAC Address
     *
     * @return the list of MAC Addresses
     */
    public ArrayList<String> getMACAddress() {
        return MACAddress;
    }


    /**
     * Getter of HashMap Location-RSS Values list
     *
     * @return the HashMap Location-RSS Values
     */
    public HashMap<String, ArrayList<String>> getLocationRSSPairs() {
        return LocationRSSPairs;
    }


    /**
     * Gets radio map from the androidFileBrowser
     *
     * @return the filename of file used
     */
    public File getRadioMapFile() {
        return this.RadioMapFile;
    }

    /**
     * Creates the physical radio map
     *
     * @param file the radio map file to read
     * @return true if map is successfully generated
     */
    public boolean GenerateRadioMap(File file) {

        if (!file.exists()) {
            return false;
        } else if (!file.canRead()) {
            return false;
        }

        this.RadioMapFile = file;

        this.locationOrderList.clear();
        this.MACAddress.clear();
        this.LocationRSSPairs.clear();

        ArrayList<String> RSSValues = null;
        BufferedReader reader = null;
        String line = null;
        String[] temp = null;
        String key = null;

        try {

            reader = new BufferedReader(new FileReader(file));

            // Read the first line
            line = reader.readLine();

            if (line == null)
                return false;

            // Split the , based lines and replace with a whole sentence with no comma
            line = line.replace(", ", " ");
            temp = line.split(" ");

            // Length must be greater than 4 because only the MAC Address contains a string of longer
            // than floor values when you remove the commas. The X and Y has a . which is not in the
            // reading of the MAC address
            if (temp.length < 4)
                return false;

            // Store all Mac Addresses
            this.MACAddress.addAll(Arrays.asList(temp).subList(3, temp.length));

            // This is to test if MAC Addresses are being printed
            System.out.println("MAC Addresses" + this.getMACAddress());

            while ((line = reader.readLine()) != null) {

                if (line.trim().equals(""))
                    continue;

                line = line.replace(", ", " ");
                temp = line.split(" ");

                // This reads the X and Y coordinates from the list
                if (temp.length < 3)
                    return false;

                // Once split, each coord value is added to a temp arrayList
                key = temp[0] + " " + temp[1];

                System.out.println("key" + key);

                // Array to Hold the RSS Values from Radio Map

                // Add the remaining values associated with the coords. These values will be the
                // RSS Values
                RSSValues = new ArrayList<String>(Arrays.asList(temp).subList(2, temp.length));

                // Ensure Equal number of MAC address and RSS Values
                if (this.MACAddress.size() != RSSValues.size())
                    return false;

                // Add key of coordinates(location) + RSSValues
                this.LocationRSSPairs.put(key, RSSValues);

                this.locationOrderList.add(key);

                // Yet again to test values have been correctly read.
                System.out.println("RSSValues" + RSSValues);
                System.out.println("RSSValues" + LocationRSSPairs);
            }
            reader.close();
        } catch (Exception ex) {
            return false;
        }
        return true;


    }

    // Coverts the data into readable formats for the application to read.
    public String toString() {
        String str = "MAC Adresses: ";
        ArrayList<String> temp;
        for (int i = 0; i < MACAddress.size(); ++i)
            str += MACAddress.get(i) + " ";

        str += "\nLocations\n";
        for (String location : LocationRSSPairs.keySet()) {
            str += location + " ";
            temp = LocationRSSPairs.get(location);
            for (int i = 0; i < temp.size(); ++i)
                str += temp.get(i) + " ";
            str += "\n";
        }

        return str;
    }
}
