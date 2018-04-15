/*
 * Created by Liam Logan
 * Copyright (c) 2018. All Rights reserved
 *
 *
 */

package liam.dissertationproject.Positioning;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.AssetManager;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import WiFi.Receiver;
import WiFi.WiFiManager;
import liam.dissertationproject.Algorithm.PositioningAlgorithms;


public class MainActivity extends AppCompatActivity implements OnClickListener, PopupMenu.OnMenuItemClickListener, OnSharedPreferenceChangeListener, Observer {

    // Preferences name for indoor and outdoor
    public static final String sharedPreferences = "IndoorPreferences";
    private final PositionObservable positionMe = new PositionObservable();
    // The latest scan list of APs
    ArrayList<AccessPointRecords> scanList;
    // Text views to show results
    private TextView title;
    // TextView showing the current scan results
    private TextView scanResults;
    private TextView PositionX;
    private TextView PositionY;
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    private DecimalFormat myFormatter = new DecimalFormat("###0.00");
    // Button for positioning
    private Button LocateMe;
    // Button for isPositioning
    private ToggleButton isPositioning;
    // Flag to show if there is an ongoing progress
    private Boolean inProgress;
    // The radioMap read
    private RadioMap radioMap;
    // WiFi manager
    private WiFiManager wifi;
    // WiFi Receiver
    private Receiver receiverWifi;
    private SharedPreferences Preferences;
    private InputStream imagePath;
    // Image width and height in meters
    private String floorPlanWidth;
    private String floorPlanHeight;
    private Positioning position;

    private ArrayList<Destination> destinationList = new ArrayList<>();


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // This sets the layout for the application
        setContentView(R.layout.application);

        // This creates an object of Positioning, which is used for the
        // actual positioning of the user.
        this.position = new Positioning(this);
        this.position.setPositionMe(this.positionMe);

        // Store results from scanList into an ArrayList
        scanList = new ArrayList<AccessPointRecords>();

        // This section sets the textViews used to display the values
        // onto the screen

        latitudeTextView = (TextView) findViewById(R.id.latitude);
        longitudeTextView = (TextView) findViewById(R.id.longitude);


        PositionX = (TextView) findViewById(R.id.LatTitle);
        PositionY = (TextView) findViewById(R.id.LonTitle);

        inProgress = Boolean.FALSE;

        // Create the Radio map
        radioMap = new RadioMap();




        // Toogle Button to initiate starting of positioning
        isPositioning = (ToggleButton) findViewById(R.id.positioning);
        isPositioning.setOnClickListener(this);

        /// / WiFi manager to manage scans
        wifi = new WiFiManager(getApplicationContext());
        wifi.setScanResultsTextView(scanResults);

        // Create new receiver to get broadcasts
        receiverWifi = new WifiManager();

        // Configure preferences
        Preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // This creates the instance of preferences which are required to access radio map
        PreferenceManager.setDefaultValues(this, sharedPreferences, MODE_PRIVATE, R.xml.preferences, true);
        Preferences = MainActivity.this.getSharedPreferences(sharedPreferences, MODE_PRIVATE);

        PositionX.setText("X:");
        PositionY.setText("Y:");

        Preferences.registerOnSharedPreferenceChangeListener(this);

        // This will write the files to the Download Folder when the application is loaded
        copyAssets();


        onSharedPreferenceChanged(Preferences, "image");


        // The elements which are required for the destinations are added here, as from the destination class
        // the constructor is filled with the appropriate values. Each room within the 2A corridor has been implemented
        Destination LTA1 = new Destination("LTA1", 628, 582);
        Destination LTA3 = new Destination("LTA3", 1192, 582);
        Destination LTA4 = new Destination("LTA4", 1340, 486);
        Destination LTA5 = new Destination("LTA5", 2034, 483);
        Destination LTA6 = new Destination("LTA6", 2215, 310);
        Destination LTB3 = new Destination("LTB3", 1238, 887);
        Destination LTB4 = new Destination("LTB4", 1387, 737);
        Destination LTW1 = new Destination("LTW1", 1464, 618);
        Destination Room1 = new Destination("Room 1", 514, 468);
        Destination Room2 = new Destination("Room 2", 460, 455);
        Destination Room3 = new Destination("Room 3", 479, 475);
        Destination Room4 = new Destination("Room 4", 490, 490);
        Destination Room5 = new Destination("Room 5", 605, 515);
        Destination Room9 = new Destination("Room 9", 690, 580);
        Destination Room11 = new Destination("Room 11", 732, 580);
        Destination Room13 = new Destination("Room 13", 799, 585);
        Destination Room14 = new Destination("Room 14", 806, 620);
        Destination Room15 = new Destination("Room 15", 881, 618);
        Destination Room16 = new Destination("Room 16", 930, 610);
        Destination Room17 = new Destination("Room 17", 960, 595);
        Destination Room18 = new Destination("Room 18", 996, 610);
        Destination Room19 = new Destination("Room 19", 1032, 575);
        Destination Room21 = new Destination("Room 21", 1094, 578);
        Destination Room29 = new Destination("Room 29", 1411, 460);
        Destination Room31 = new Destination("Room 31", 1411, 450);
        Destination Room33 = new Destination("Room 33", 1411, 440);
        Destination Room35 = new Destination("Room 35", 1430, 425);
        Destination Room37 = new Destination("Room 37", 1474, 425);
        Destination Room39 = new Destination("Room 39", 1470, 435);
        Destination Room41 = new Destination("Room 41", 1485, 435);
        Destination Room43 = new Destination("Room 43", 1523, 440);
        Destination Room45 = new Destination("Room 45", 1520, 455);
        Destination Room47 = new Destination("Room 47", 1555, 465);
        Destination Room49 = new Destination("Room 49", 1561, 450);
        Destination Room51 = new Destination("Room 51", 1573, 410);
        Destination Room53 = new Destination("Room 53", 1606, 410);
        Destination Room55 = new Destination("Room 55", 1637, 410);
        Destination Room56 = new Destination("Room 56", 1690, 470);
        Destination Room57 = new Destination("Room 57", 1655, 400);
        Destination Room58 = new Destination("Room 58", 1712, 470);
        Destination Room59 = new Destination("Room 59", 1697, 400);
        Destination Room60 = new Destination("Room 60", 1725, 470);
        Destination Room61 = new Destination("Room 61", 1697, 415);
        Destination Room62 = new Destination("Room 62", 1740, 470);
        Destination Room63 = new Destination("Room 63", 1697, 430);
        Destination Room64 = new Destination("Room 64", 1755, 470);
        Destination Room65 = new Destination("Room 65", 1697, 445);
        Destination Room66 = new Destination("Room 66", 1770, 470);
        Destination Room68 = new Destination("Room 68", 1785, 470);
        Destination Room70 = new Destination("Room 70", 1800, 470);
        Destination Room72 = new Destination("Room 72", 1822, 440);
        Destination Room73 = new Destination("Room 73", 1780, 450);
        Destination Room74 = new Destination("Room 74", 1842, 440);
        Destination Room75 = new Destination("Room 75", 1855, 436);
        Destination Room77 = new Destination("Room 77", 1888, 445);
        Destination Room78 = new Destination("Room 78", 1905, 490);
        Destination Room79 = new Destination("Room 79", 1910, 445);
        Destination Room81 = new Destination("Room 81", 1970, 450);
        Destination Room87 = new Destination("Room 87", 2052, 410);
        Destination Room91 = new Destination("Room 91", 2148, 345);


        // This adds the elements into the arrayList
        destinationList.add(LTA1);
        destinationList.add(LTA3);
        destinationList.add(LTA4);
        destinationList.add(LTA5);
        destinationList.add(LTA6);
        destinationList.add(LTB3);
        destinationList.add(LTB4);
        destinationList.add(LTW1);
        destinationList.add(Room1);
        destinationList.add(Room2);
        destinationList.add(Room3);
        destinationList.add(Room4);
        destinationList.add(Room5);
        destinationList.add(Room9);
        destinationList.add(Room11);
        destinationList.add(Room13);
        destinationList.add(Room14);
        destinationList.add(Room15);
        destinationList.add(Room16);
        destinationList.add(Room17);
        destinationList.add(Room18);
        destinationList.add(Room19);
        destinationList.add(Room21);
        destinationList.add(Room29);
        destinationList.add(Room31);
        destinationList.add(Room33);
        destinationList.add(Room35);
        destinationList.add(Room37);
        destinationList.add(Room39);
        destinationList.add(Room41);
        destinationList.add(Room43);
        destinationList.add(Room45);
        destinationList.add(Room47);
        destinationList.add(Room49);
        destinationList.add(Room51);
        destinationList.add(Room53);
        destinationList.add(Room55);
        destinationList.add(Room56);
        destinationList.add(Room57);
        destinationList.add(Room58);
        destinationList.add(Room59);
        destinationList.add(Room60);
        destinationList.add(Room61);
        destinationList.add(Room62);
        destinationList.add(Room63);
        destinationList.add(Room64);
        destinationList.add(Room65);
        destinationList.add(Room66);
        destinationList.add(Room68);
        destinationList.add(Room70);
        destinationList.add(Room72);
        destinationList.add(Room73);
        destinationList.add(Room74);
        destinationList.add(Room75);
        destinationList.add(Room77);
        destinationList.add(Room78);
        destinationList.add(Room79);
        destinationList.add(Room81);
        destinationList.add(Room87);
        destinationList.add(Room91);


        latitudeTextView.setVisibility(View.VISIBLE);
        longitudeTextView.setVisibility(View.VISIBLE);
        PositionX.setVisibility(View.VISIBLE);
        PositionY.setVisibility(View.VISIBLE);


        isPositioning.setVisibility(View.VISIBLE);

        // Enables the WiFi if is in Online Mode
        wifi.startScan(receiverWifi, "2000");


    }


    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }

        assert files != null;
        for(String filename : files) {

            System.out.println("Files=>" + filename);
            InputStream in = null;
            OutputStream out = null;

            if (filename.contains("radiomap")) try {
                in = assetManager.open(filename);

                File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                File file = new File(dir, filename);
                Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

                if (!isSDPresent) {
                    popupMsg("External Memory is not available", "Memory", Toast.LENGTH_LONG);
                }

                out = new FileOutputStream(file);
                copyFile(in, out);

                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;

            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }

        }


    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }




    /**
     * This is the menuOption "Destinations" which is present on the application
     * Each Button in the menu has a clickable option. For each option we assign it a value
     * from the destination. This makes calls to the imageZoomView class where the marker is drawn
     * regarding the coordinates to the element from the arrayList
     */

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.LTA1:
                position.zoomView.clearDestinationPoints();
                position.zoomView.setPoint(destinationList.get(0).getLocationX(),
                        destinationList.get(0).getLocationY());
                Toast.makeText(getBaseContext(), "Lecture Theatre A1 Selected,", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.LTA3:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Lecture Theatre A3 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(1).getLocationX(),
                        destinationList.get(1).getLocationY());
                return true;
            case R.id.LTA4:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Lecture Theatre A4 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(2).getLocationX(),
                        destinationList.get(2).getLocationY());
                return true;
            case R.id.LTA5:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Lecture Theatre A5 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(3).getLocationX(),
                        destinationList.get(3).getLocationY());
                return true;
            case R.id.LTA6:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Lecture Theatre A6 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(4).getLocationX(),
                        destinationList.get(4).getLocationY());
                return true;
            case R.id.LTB3:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Lecture Theatre B3 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(5).getLocationX(),
                        destinationList.get(5).getLocationY());
                return true;
            case R.id.LTB4:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Lecture Theatre B4 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(6).getLocationX(),
                        destinationList.get(6).getLocationY());
                return true;
            case R.id.LTW1:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Lecture Theatre W1 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(7).getLocationX(),
                        destinationList.get(7).getLocationY());
                return true;
            case R.id.Room1:
                position.zoomView.clearDestinationPoints();
                position.zoomView.setPoint(destinationList.get(8).getLocationX(),
                        destinationList.get(8).getLocationY());
                Toast.makeText(getBaseContext(), "Room 1 Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Room2:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 2 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(9).getLocationX(),
                        destinationList.get(9).getLocationY());
                return true;
            case R.id.Room3:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 3 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(10).getLocationX(),
                        destinationList.get(10).getLocationY());
                return true;
            case R.id.Room4:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 4 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(11).getLocationX(),
                        destinationList.get(11).getLocationY());
                return true;
            case R.id.Room5:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 5 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(12).getLocationX(),
                        destinationList.get(12).getLocationY());
                return true;
            case R.id.Room9:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 9 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(13).getLocationX(),
                        destinationList.get(13).getLocationY());
                return true;
            case R.id.Room11:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 11 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(14).getLocationX(),
                        destinationList.get(14).getLocationY());
                return true;
            case R.id.Room13:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 13 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(15).getLocationX(),
                        destinationList.get(15).getLocationY());
                return true;
            case R.id.Room14:
                position.zoomView.clearDestinationPoints();
                position.zoomView.setPoint(destinationList.get(16).getLocationX(),
                        destinationList.get(16).getLocationY());
                Toast.makeText(getBaseContext(), "Room 14 Selected ", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Room15:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 15 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(17).getLocationX(),
                        destinationList.get(17).getLocationY());
                return true;
            case R.id.Room16:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 16 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(18).getLocationX(),
                        destinationList.get(18).getLocationY());
                return true;
            case R.id.Room17:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 17 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(19).getLocationX(),
                        destinationList.get(19).getLocationY());
                return true;
            case R.id.Room18:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 18 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(20).getLocationX(),
                        destinationList.get(20).getLocationY());
                return true;
            case R.id.Room19:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 19 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(21).getLocationX(),
                        destinationList.get(21).getLocationY());
                return true;
            case R.id.Room21:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 21 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(22).getLocationX(),
                        destinationList.get(22).getLocationY());
                return true;
            case R.id.Room29:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 29 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(23).getLocationX(),
                        destinationList.get(23).getLocationY());
                return true;
            case R.id.Room31:
                position.zoomView.clearDestinationPoints();
                position.zoomView.setPoint(destinationList.get(24).getLocationX(),
                        destinationList.get(24).getLocationY());
                Toast.makeText(getBaseContext(), "Room 31 Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Room33:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 33 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(25).getLocationX(),
                        destinationList.get(25).getLocationY());
                return true;
            case R.id.Room35:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 35 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(26).getLocationX(),
                        destinationList.get(26).getLocationY());
                return true;
            case R.id.Room37:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 37 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(27).getLocationX(),
                        destinationList.get(27).getLocationY());
                return true;
            case R.id.Room39:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 39 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(28).getLocationX(),
                        destinationList.get(28).getLocationY());
                return true;
            case R.id.Room41:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 41 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(29).getLocationX(),
                        destinationList.get(29).getLocationY());
                return true;
            case R.id.Room43:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 43 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(30).getLocationX(),
                        destinationList.get(30).getLocationY());
                return true;
            case R.id.Room45:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 45 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(31).getLocationX(),
                        destinationList.get(31).getLocationY());
                return true;
            case R.id.Room47:
                position.zoomView.clearDestinationPoints();
                position.zoomView.setPoint(destinationList.get(32).getLocationX(),
                        destinationList.get(32).getLocationY());
                Toast.makeText(getBaseContext(), "Room 47 Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Room49:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 49 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(33).getLocationX(),
                        destinationList.get(33).getLocationY());
                return true;
            case R.id.Room51:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 51 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(34).getLocationX(),
                        destinationList.get(34).getLocationY());
                return true;
            case R.id.Room53:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 53 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(35).getLocationX(),
                        destinationList.get(35).getLocationY());
                return true;
            case R.id.Room55:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 55 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(36).getLocationX(),
                        destinationList.get(36).getLocationY());
                return true;
            case R.id.Room56:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 56 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(37).getLocationX(),
                        destinationList.get(37).getLocationY());
                return true;
            case R.id.Room57:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 57 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(38).getLocationX(),
                        destinationList.get(38).getLocationY());
                return true;
            case R.id.Room58:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 58 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(39).getLocationX(),
                        destinationList.get(39).getLocationY());
                return true;
            case R.id.Room59:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 59 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(40).getLocationX(),
                        destinationList.get(40).getLocationY());
                return true;
            case R.id.Room60:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 60 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(41).getLocationX(),
                        destinationList.get(41).getLocationY());
                return true;
            case R.id.Room61:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 61 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(42).getLocationX(),
                        destinationList.get(42).getLocationY());
                return true;
            case R.id.Room62:
                position.zoomView.clearDestinationPoints();
                position.zoomView.setPoint(destinationList.get(43).getLocationX(),
                        destinationList.get(43).getLocationY());
                Toast.makeText(getBaseContext(), "Room 62 Selected",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.Room63:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 63 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(44).getLocationX(),
                        destinationList.get(44).getLocationY());
                return true;
            case R.id.Room64:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 64 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(45).getLocationX(),
                        destinationList.get(45).getLocationY());
                return true;
            case R.id.Room65:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 65 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(46).getLocationX(),
                        destinationList.get(46).getLocationY());
                return true;
            case R.id.Room66:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 66 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(47).getLocationX(),
                        destinationList.get(47).getLocationY());
                return true;
            case R.id.Room68:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 68 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(48).getLocationX(),
                        destinationList.get(48).getLocationY());
                return true;
            case R.id.Room70:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 70 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(49).getLocationX(),
                        destinationList.get(49).getLocationY());
                return true;
            case R.id.Room72:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 72 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(50).getLocationX(),
                        destinationList.get(50).getLocationY());
                return true;
            case R.id.Room73:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 73 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(51).getLocationX(),
                        destinationList.get(51).getLocationY());
                return true;
            case R.id.Room74:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 74 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(52).getLocationX(),
                        destinationList.get(52).getLocationY());
                return true;
            case R.id.Room75:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 75 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(53).getLocationX(),
                        destinationList.get(53).getLocationY());
                return true;
            case R.id.Room77:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 77 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(54).getLocationX(),
                        destinationList.get(54).getLocationY());
                return true;
            case R.id.Room78:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 78 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(55).getLocationX(),
                        destinationList.get(55).getLocationY());
                return true;
            case R.id.Room79:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 79 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(56).getLocationX(),
                        destinationList.get(56).getLocationY());
                return true;
            case R.id.Room81:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 81 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(57).getLocationX(),
                        destinationList.get(57).getLocationY());
                return true;
            case R.id.Room87:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 87 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(58).getLocationX(),
                        destinationList.get(58).getLocationY());
                return true;
            case R.id.Room91:
                position.zoomView.clearDestinationPoints();
                Toast.makeText(getBaseContext(), "Room 91 Selected",
                        Toast.LENGTH_SHORT).show();
                position.zoomView.setPoint(destinationList.get(59).getLocationX(),
                        destinationList.get(59).getLocationY());
                return true;

            default:
                return false;
        }
    }

    /**
     * Draw the menu
     *
     * @param menu This generates the menu
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    /**
     * @param item Handles the menu click options
     */
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Launch Preferences
            case R.id.Preferences:
                Intent prefs = new Intent(this, liam.dissertationproject.Positioning.Preferences.class);
                startActivity(prefs);
                return true;
            // Launch Preferences to choose one of the algorithms implemented
        }
        return false;
    }

    /**
     * This is the method which deals with callback to be invoked when a shared preference is changed
     *
     * @param prefs The sharedPreferences that recieved the change
     * @param key   String: The key of the preference that was changed, added or removed
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {

        if (key == null)
            return;

        if (key.equals("image")) {

            try {
                imagePath = getAssets().open("CottrellBuilding.jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //	imagePath = The imagePath should be the path to the file from the assest, which is then
            // called into FMOB and used a the image.

            if (imagePath.equals("")) {
                return;
            }

            floorPlanWidth = null;
            floorPlanHeight = null;

            if (!ReadWidthHeigthFromFile()) {
                popupMsg("Corrupted image configuration file.\nPlease set a different floor plan or previous floor plan will be used if available.",
                        "Error", Toast.LENGTH_LONG);
                imagePath = null;
                floorPlanWidth = null;
                floorPlanHeight = null;
                return;
            }

            if (!position.setFloorPlan(imagePath, floorPlanWidth, floorPlanHeight)) {
                imagePath = null;
                floorPlanWidth = null;
                floorPlanHeight = null;
            } else {
                latitudeTextView.setText("000.00");
                longitudeTextView.setText("00.00");
            }
        }
    }

    /**
     * Control the clicks on buttons
     *
     * @param v the view clicked
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            // Positioning
            case R.id.positioning:
                if (isPositioning.isChecked()) {
                    this.positionMe.setBoolean(true);
                    this.positionMe.notifyObservers();
                } else {
                    this.positionMe.setBoolean(false);
                    this.positionMe.notifyObservers();
                }
                break;
        }

    }

    public void showPopUp(View v) {

        PopupMenu routeMenu = new PopupMenu(this, v);
        routeMenu.setOnMenuItemClickListener(MainActivity.this);
        MenuInflater inflater = routeMenu.getMenuInflater();
        inflater.inflate(R.menu.route_menu, routeMenu.getMenu());
        routeMenu.show();
    }

    /**
     * Initiates positioning algorithm
     */
    private boolean startAlgorithm() {

        // This states the algorithm for which the algorithm will use which is the KNN Algorithm
        // This is important as the app need to knows that since it has been provided with this, it can
        // initiate process
        String algorithm = Preferences.getString("PositioningAlgorithms", "1").trim();

        // This makes sure that radiomap is retrieved otherwise not calculations will be made as we cant
        // locate the user
        String radiomapFile = Preferences.getString("radiomap", "").trim();


        // Error reading Radio Map
        if (!radioMap.GenerateRadioMap(new File(radiomapFile))) {
            popupMsg("Error Reading Radio Map", "User Error", Toast.LENGTH_LONG);

            // Unset in progress (false)
            synchronized (inProgress) {
                inProgress = false;
            }

            return false;
        }

        if (!calculatePosition(Integer.parseInt(algorithm))) {
            popupMsg("Can't find location", "Application Error", Toast.LENGTH_LONG);

            // Unset in progress (false)
            synchronized (inProgress) {
                inProgress = false;
            }

            return false;
        }

        // Unset in progress (false)
        synchronized (inProgress) {
            inProgress = false;
        }

        return true;

    }

    /**
     * @return return the height and width from config file for map
     *
     * This information will be returned to the isPositioning class
     */
    private boolean ReadWidthHeigthFromFile() {


        //AssetManager allows files to be retrieved from asset folder in Android Studio
        AssetManager assetManager = getAssets();


        // Get the config file from assets
        InputStream input = null;
        try {
            input = assetManager.open("mapDimensions.config");
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Store the heigh and width into one value then split to get two values
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = null;
        int i = 0;


        try {

            while ((line = reader.readLine()) != null) {

                /* Ignore the labels */
                if (line.startsWith("#") || line.trim().equals("")) {
                    continue;
                }

                line = line.replace(": ", " ");
                /* Split fields */
                String[] temp = line.split(" ");

                if (temp.length != 2) {
                    return false;
                }

                if (i == 0)
                    floorPlanWidth = temp[1];
                else
                    floorPlanHeight = temp[1];

                ++i;

            }
        } catch (Exception e) {
            return false;
        }

        return buildingDimensions(floorPlanWidth, floorPlanHeight);
    }

    /**
     * @param floorPlanWidth  Width of floor plan
     * @param floorPlanHeight Height of floor plan
     * @return
     */
    private boolean buildingDimensions(String floorPlanWidth, String floorPlanHeight) {

        if (floorPlanHeight.equals("")) {
            popupMsg("Corrupted image configuration file", "User Error", Toast.LENGTH_LONG);
            return false;
        }

        try {
            Float.parseFloat(floorPlanHeight);
        } catch (Exception e) {
            popupMsg("Error Floor Plan Height: " + e.getMessage(), "User Error", Toast.LENGTH_LONG);
            return false;
        }

        try {
            Float.parseFloat(floorPlanWidth);
        } catch (Exception e) {
            popupMsg("Error Building Width: " + e.getMessage(), "User Error", Toast.LENGTH_LONG);
            return false;
        }

        return true;
    }

    /**
     * @param algorithm The algorithm is called from positioningAlgorithms and process of
     *                  the specific algorithm is initiated.
     * @return Return if coordinates are valid or not
     */
    private boolean calculatePosition(int algorithm) {

        String calculatedLocation = PositioningAlgorithms.ProcessAlgorithm(scanList, radioMap, algorithm);

        if (calculatedLocation == null) {
            return false;
        }

        /**
         * Split the the whole number into coordinates
         */
        String[] coordinates = calculatedLocation.split(" ");
        if (coordinates.length != 2) {
            return false;
        }

        // Convert the coordinates into decimal format
        try {
            if (coordinates[0].equals("NaN") || coordinates[1].equals("NaN"))
                return false;
            latitudeTextView.setText(new StringBuilder().append(myFormatter.format(Float.parseFloat(coordinates[0]))).append("").toString());
            longitudeTextView.setText(new StringBuilder().append(myFormatter.format(Float.parseFloat(coordinates[1]))).append("").toString());
        } catch (Exception e) {
            return false;
        }


        //Return coordinates and call them from position class
        position.positionOnMap(calculatedLocation);

        return true;

    }

    /**
     * Display pop up messages on screen
     *
     * @param msg     Message to be displayed
     * @param title   Title of Message
     * @param imageID Image to be displayed with message
     */
    private void popupMsg(String msg, String title, int imageID) {

        AlertDialog.Builder alertBox = new AlertDialog.Builder(this);
        alertBox.setTitle(title);
        alertBox.setMessage(msg);
        alertBox.setIcon(imageID);

        alertBox.setNeutralButton("Hide", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alert = alertBox.create();
        alert.show();
    }

    /**
     * Back button pressed to exit program
     */
    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure you want to go to the main menu?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MainActivity.this.finish();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        super.onBackPressed();
        this.finish();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {

        wifi.stopScan(receiverWifi);

        super.onDestroy();
    }

    @Override
    public void update(Observable observable, Object data) {

    }

    /**
     * The Wifi manager is responsible for the dealing of the Access Points. Such as storing information.
     */
    public class WifiManager extends Receiver {

        public void onReceive(Context context, Intent intent) {

            try {
                if (intent == null || context == null || intent.getAction() == null)
                    return;

                String action = intent.getAction();

                // An access point has completed and results are available. A call to getScanResults is
                // used to obtain the results.

                if (!action.equals(android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
                    return;

                // This is the call to get the results. The results are shown on screen of the number
                // of access points which have been detected.
                List<ScanResult> AccessPointList = wifi.getScanResults();

                // Set in progress (true)
                synchronized (inProgress) {
                    if (inProgress == true)
                        return;
                    inProgress = true;
                }
                scanList.clear();
                AccessPointRecords APR = null;

                // If results are retrieved add the information from the scan to AccessPointRecord list
                // This stores all information regarding each access point.
                if (AccessPointList != null && !AccessPointList.isEmpty()) {
                    for (int i = 0; i < AccessPointList.size(); i++) {
                        APR = new AccessPointRecords(AccessPointList.get(i).BSSID, AccessPointList.get(i).level);
                        scanList.add(APR);
                    }
                }
                // Unset in progress (false)
                synchronized (inProgress) {
                    inProgress = false;
                }

                if (positionMe.get()) {
                    if (!startAlgorithm()) {
                        isPositioning.setChecked(false);
                        positionMe.setBoolean(false);
                        positionMe.notifyObservers();
                    }
                }

            } catch (RuntimeException e) {
                return;
            }

        }

    }
}