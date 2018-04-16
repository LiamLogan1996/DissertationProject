/*
 * Created by Liam Logan
 * Copyright (c) 2018. All Rights reserved
 *
 */

package liam.dissertationproject.Positioning;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

import java.util.Objects;

import liam.dissertationproject.FileBrowser.AndroidFileBrowser;


public class Preferences extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int FILE = 1;

    /**
     * Called when the activity is first created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Load the appropriate preferences
        getPreferenceManager().setSharedPreferencesName(MainActivity.sharedPreferences);

        addPreferencesFromResource(R.xml.preferences);

        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        // Custom button to choose radio map file to useD for positioning
        Objects.requireNonNull(getPreferenceManager().findPreference("radiomap")).setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            /**
             *  The preferenceClick uses bundles which is used to pass data between activities
             *  In this instance, we are communicating with the AndroidFileBrowser class as
             *  the application has to obtain the radio map file
             */
            public boolean onPreferenceClick(Preference preference) {

                Intent intent = new Intent(getBaseContext(), AndroidFileBrowser.class);

                Bundle dataBundle = new Bundle();
                dataBundle.putInt("Browse", 1);
                intent.putExtras(dataBundle);

                startActivityForResult(intent, FILE);
                return true;
            }
        });
    }

    @Override
    /**
     *  The intent returned by the file provides a content URI that identified
     *  the file which has been selected by the user
     *
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SharedPreferences customSharedPreference;

        customSharedPreference = getSharedPreferences(MainActivity.sharedPreferences, MODE_PRIVATE);

                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedFile = data.getData();
                    assert selectedFile != null;
                    String file = selectedFile.toString();
                    SharedPreferences.Editor editor = customSharedPreference.edit();
                    editor.putString("radiomap", file);
                    editor.apply();
                }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the listener whenever a key changes
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
    }

}