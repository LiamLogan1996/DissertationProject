/*
 * Created by Liam Logan
 * Copyright (c) 2018. All Rights reserved
 *
 *
 */

package liam.dissertationproject.Positioning;

//Constructor Class to store information from the nearby APs
public class AccessPointRecords {

    // MACAddress
    private String BSSID;

    //RSS Values
    private int RSS;

    AccessPointRecords(String BSSID, int RSS) {
        super();
        this.BSSID = BSSID;
        this.RSS = RSS;
    }

    public String getBSSID() {
        return BSSID;
    }

    public int getRSS() {
        return RSS;
    }

    // toString used to collect the information in a readable format
    public String toString() {
        String str = new String();
        str = String.valueOf(BSSID) + " " + String.valueOf(RSS) + "\n";
        return str;
    }

}
