/*
 * Created by Liam Logan
 * Copyright (c) 2018. All Rights reserved
 *
 *
 */

package liam.dissertationproject.Positioning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class RadioMap {

    private File RadioMapFile = null;
    private ArrayList<String> MacAdressList = null;
    private HashMap<String, ArrayList<String>> LocationRSS = null;
    private ArrayList<String> OrderList = null;

    public RadioMap() {
        super();
        MacAdressList = new ArrayList<String>();
        LocationRSS = new HashMap<String, ArrayList<String>>();
        OrderList = new ArrayList<String>();
    }

    /**
     * Getter of MAC Address list in file order
     *
     * @return the list of MAC Addresses
     */
    public ArrayList<String> getMacAdressList() {
        return MacAdressList;
    }

    /**
     * Getter of HashMap Location-RSS Values list in no particular order
     *
     * @return the HashMap Location-RSS Values
     */
    public HashMap<String, ArrayList<String>> getLocationRSS() {
        return LocationRSS;
    }

    /**
     * Getter of Location list in file order
     *
     * @return the Location list
     */
    public ArrayList<String> getOrderList() {
        return OrderList;
    }

    /**
     * Getter of radio map mean filename
     *
     * @return the filename of radiomap mean used
     */
    public File getRadioMapFile() {
        return this.RadioMapFile;
    }

    /**
     * Construct a radio map
     *
     * @param inFile the radio map file to read
     * @return true if radio map constructed successfully, otherwise false
     */
    public boolean GenerateRadioMap(File inFile) {

        if (!inFile.exists() || !inFile.canRead()) {
            return false;
        }

        this.RadioMapFile = inFile;

        this.OrderList.clear();
        this.MacAdressList.clear();
        this.LocationRSS.clear();

        ArrayList<String> RSS_Values = null;
        BufferedReader reader = null;
        String line = null;
        String[] temp = null;
        String key = null;

        try {

            reader = new BufferedReader(new FileReader(inFile));

            // Read the first line
            line = reader.readLine();

            // Must exists
            if (line == null)
                return false;

            line = line.replace(", ", " ");
            temp = line.split(" ");

            // Must have more than 4 fields
            if (temp.length < 4)
                return false;

            // Store all Mac Addresses
            for (int i = 3; i < temp.length; ++i)
                this.MacAdressList.add(temp[i]);

            while ((line = reader.readLine()) != null) {

                if (line.trim().equals(""))
                    continue;

                line = line.replace(", ", " ");
                temp = line.split(" ");

                if (temp.length < 3)
                    return false;

                key = temp[0] + " " + temp[1];

                RSS_Values = new ArrayList<String>();

                for (int i = 2; i < temp.length; ++i)
                    RSS_Values.add(temp[i]);

                // Equal number of MAC address and RSS Values
                if (this.MacAdressList.size() != RSS_Values.size())
                    return false;

                this.LocationRSS.put(key, RSS_Values);

                this.OrderList.add(key);
            }
            reader.close();
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    public String toString() {
        String str = "MAC Adresses: ";
        ArrayList<String> temp;
        for (int i = 0; i < MacAdressList.size(); ++i)
            str += MacAdressList.get(i) + " ";

        str += "\nLocations\n";
        for (String location : LocationRSS.keySet()) {
            str += location + " ";
            temp = LocationRSS.get(location);
            for (int i = 0; i < temp.size(); ++i)
                str += temp.get(i) + " ";
            str += "\n";
        }

        return str;
    }
}
