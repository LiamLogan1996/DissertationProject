/*
 * Created by Liam Logan
 * Copyright (c) 2018. All Rights reserved
 *
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

import liam.dissertationproject.FileBrowser.AndroidFileBrowser;


public class Preferences extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int SELECT_FILE = 9;

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

        // Custom button to choose folder

        // Custom button to choose radio map file to use for positioning
        getPreferenceManager().findPreference("radiomap").setOnPreferenceClickListener(new OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {

                Intent i = new Intent(getBaseContext(), AndroidFileBrowser.class);

                Bundle extras = new Bundle();
                // Send flag to browse for folder false, it is a file selection.
                extras.putInt("to_Browse", 2);

                i.putExtras(extras);

                startActivityForResult(i, SELECT_FILE);
                return true;
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        SharedPreferences customSharedPreference;

        customSharedPreference = getSharedPreferences(MainActivity.sharedPreferences, MODE_PRIVATE);

        switch (requestCode) {

            case SELECT_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedFile = data.getData();
                    String file = selectedFile.toString();
                    SharedPreferences.Editor editor = customSharedPreference.edit();
                    editor.putString("radiomap", file);
                    editor.commit();
                }
                break;
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