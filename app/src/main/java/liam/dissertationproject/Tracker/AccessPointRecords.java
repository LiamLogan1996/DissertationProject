/*
 * Created by Liam Logan on 05/04/18 18:08
 * Copyright (c) 2018. All rights reserved.
 *
 * Last modified 30/03/18 16:16
 */

package liam.dissertationproject.Tracker;

public class AccessPointRecords {

	private String bssid;
	private int rss;

	public AccessPointRecords(String bssid, int rss) {
		super();
		this.bssid = bssid;
		this.rss = rss;
	}

	public String getBssid() {
		return bssid;
	}

	public int getRss() {
		return rss;
	}
	
	public String toString() {
		String str = new String();
		str = String.valueOf(bssid) + " " + String.valueOf(rss) + "\n";
		return str;
	}

}
