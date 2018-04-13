/*
 * Created by Liam Logan
 * Copyright (c) 2018. All Rights reserved
 *
 * Last Modified .12/04/18 14:16
 */

package liam.dissertationproject.Algorithm;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import liam.dissertationproject.Positioning.MainActivity;
import liam.dissertationproject.Positioning.R;


public class SelectPositioningAlgorithm extends PreferenceActivity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getPreferenceManager().setSharedPreferencesName(MainActivity.sharedPreferences);

        addPreferencesFromResource(R.xml.choose_algorithm);
    }
}