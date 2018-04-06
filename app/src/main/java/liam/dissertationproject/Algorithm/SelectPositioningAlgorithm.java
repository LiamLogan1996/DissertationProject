/*
 * Created by Liam Logan on 05/04/18 18:08
 * Copyright (c) 2018. All rights reserved.
 *
 * Last modified 03/04/18 21:11
 */

package liam.dissertationproject.Algorithm;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import liam.dissertationproject.Tracker.LocateMe;
import liam.dissertationproject.Tracker.R;


public class SelectPositioningAlgorithm extends PreferenceActivity {

	/**
	 * Called when the activity is first created.
	 * */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		getPreferenceManager().setSharedPreferencesName(LocateMe.SHARED_PREFS_INDOOR);

		addPreferencesFromResource(R.xml.choose_algorithm);
	}
}