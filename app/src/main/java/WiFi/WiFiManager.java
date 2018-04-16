/*
 * Created by Liam Logan
 * Copyright (c) 2018. All Rights reserved
 *
 *
 */

package WiFi;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WiFiManager {

    /**
     * WiFi manager used to scan and get scan results
     */
    private final WifiManager WiFi;

    /**
     * Intent with the SCAN_RESULTS_AVAILABLE_ACTION which will announce scan is complete
     * and results are available
     *
     */
    private final IntentFilter intentFilter;

    /**
     * Timer for Scanning
     */
    private final Timer timer;
    /**
     * Application context
     */
    private final Context mContext;
    /**
     *  The WiFiTasks can be scheduled for one-time by a timer
     */
    private TimerTask WifiTask;

    /**
     * If Scanning, true or false
     */
    private Boolean isScanning;

    /**
     *
     *
     * @param context Application context
     */
    public WiFiManager(Context context) {
        mContext = context;
        isScanning = Boolean.valueOf(false);

        // This instantiates the use of the Android WI-FI manager
        WiFi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

        //Intent Filter to specify when an AP scan has completed and results are available
        intentFilter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        timer = new Timer();
    }


    /**
     * @return the results of the scan
     */
    public List<ScanResult> getScanResults() {
        return WiFi.getScanResults();
    }

    /**
     * Starts the Access Points Scanning
     *
     * @param receiverWifi     Receiver to register with WiFi manager
     * @param samplesInterval  Interval used to perform a new scan
     */
    public void startScan(Receiver receiverWifi, String samplesInterval) {

        synchronized (isScanning) {
            if (isScanning) {
                return;
            }
            isScanning = true;
        }

        toastPrint("Start scanning for near Access Points...", Toast.LENGTH_SHORT);

        enableWifi();

        mContext.registerReceiver(receiverWifi, intentFilter);

        if (WifiTask != null) {
            WifiTask.cancel();
        }

        if (timer != null) {
            timer.purge();
        }

        WifiTask = new TimerTask() {

            @Override
            public void run() {
                WiFi.startScan();
            }
        };

        timer.schedule(WifiTask, 0, Long.parseLong(samplesInterval));

    }

    /**
     * Stop the Access Points Scanning
     *
     * @param receiverWifi Receiver to unregister
     */
    public void stopScan(Receiver receiverWifi) {

        synchronized (isScanning) {
            if (!isScanning) {
                return;
            }
            isScanning = false;
        }

        disableWifi();

        mContext.unregisterReceiver(receiverWifi);
        toastPrint("Scanning Stopped", Toast.LENGTH_SHORT);

    }

    /**
     * Enables WiFi
     */
    private void enableWifi() {
        if (!WiFi.isWifiEnabled())
            // Call of Android Wifi manager. WiFi is currently being enabled.
            if (WiFi.getWifiState() != WifiManager.WIFI_STATE_ENABLING)
                WiFi.setWifiEnabled(true);
    }

    /**
     * Disables WiFi
     */
    public void disableWifi() {
        if (WiFi.isWifiEnabled())
            // Call of Android Wifi manager. WiFi is currently being disabled.
            if (WiFi.getWifiState() != WifiManager.WIFI_STATE_DISABLING)
                WiFi.setWifiEnabled(false);
    }

    /**
     *  Toast Message Constructor
     */
    protected void toastPrint(String textMSG, int duration) {
        Toast.makeText(this.mContext, textMSG, duration).show();
    }

}
