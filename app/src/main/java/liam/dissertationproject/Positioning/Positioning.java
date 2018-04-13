/*
 * Created by Liam Logan
 * Copyright (c) 2018. All Rights reserved
 *
 * Last Modified .12/04/18 13:50
 */

package liam.dissertationproject.Positioning;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.util.Observable;
import java.util.Observer;

import liam.dissertationproject.ZoomFunction.BitmapCoordinates;
import liam.dissertationproject.ZoomFunction.DynamicZoomControl;
import liam.dissertationproject.ZoomFunction.ImageZoomView;
import liam.dissertationproject.ZoomFunction.LongPressZoomListener;


public class Positioning implements Observer {

    private final BitmapCoordinates curPosition = new BitmapCoordinates(-1, -1);
    /**
     * Image zoom view
     */
    public ImageZoomView zoomView;
    private InputStream imagePath;
    private String floorPlanWidth;
    private String floorPlanHeight;
    /**
     * Zoom control
     */
    private DynamicZoomControl zoomControl;

    /**
     * Decoded bitmap image
     */
    private Bitmap bitmap;

    /**
     * On touch listener for zoom view
     */
    private LongPressZoomListener zoomListener;

    private PositionObservable positionMe;

    public Positioning(MainActivity fm) {

        // Set Zooming and Panning Settings
        zoomControl = new DynamicZoomControl();
        zoomListener = new LongPressZoomListener(fm.getApplicationContext());
        zoomListener.setZoomControl(zoomControl);
        zoomView = (ImageZoomView) fm.findViewById(R.id.zoomview);
        zoomView.setZoomState(zoomControl.getZoomState());
        zoomView.setOnTouchListener(zoomListener);
        zoomView.setCurrentPoint(curPosition);

        zoomControl.setAspectQuotient(zoomView.getAspectQuotient());


    }

    public void setPositionMe(PositionObservable positionMe) {
        zoomView.setPosition(positionMe);

        if (this.positionMe != null) {
            this.positionMe.deleteObserver(this);
        }
        this.positionMe = positionMe;
        this.positionMe.addObserver(this);
    }

    public boolean setFloorPlan(InputStream imagePath, String building_width, String building_height) {


        if (imagePath == null || imagePath.equals(""))
            return false;

        if (this.imagePath != null && this.imagePath.equals(imagePath))
            return true;

        Bitmap tempBitmap = BitmapFactory.decodeStream(imagePath);

        if (tempBitmap != null) {

            if (bitmap != null)
                bitmap.recycle();

            System.gc();

            this.imagePath = imagePath;
            this.bitmap = tempBitmap;
            this.floorPlanWidth = building_width;
            this.floorPlanHeight = building_height;
            zoomView.setImage(this.bitmap);
            resetZoomState();
            resetLocation();
        } else
            return false;

        return true;
    }

    public boolean positionOnMap(String Geolocation) {

        if (Geolocation == null || bitmap == null || floorPlanWidth == null || floorPlanHeight == null)
            return false;

        String coordinates[] = Geolocation.replace(", ", " ").split(" ");
        float x, y;
        float bitmapWidth;
        float bitmapHeight;

        try {
            x = Float.parseFloat(coordinates[0]);
            y = Float.parseFloat(coordinates[1]);
            bitmapWidth = Float.parseFloat(floorPlanWidth);
            bitmapHeight = Float.parseFloat(floorPlanHeight);
        } catch (Exception e) {
            return false;
        }

        // Clear all overlays if it is not tracking
        if (!positionMe.get())
            zoomView.clearRecordedPoints();

        int xPixels = (int) ((x * bitmap.getWidth()) / bitmapWidth);
        int yPixels = (int) ((y * bitmap.getHeight()) / bitmapHeight);
        curPosition.setPositionCoordinates(xPixels, yPixels);
        curPosition.notifyObservers();


        return true;
    }

    protected void onDestroy() {

        if (bitmap != null)
            bitmap.recycle();
        if (zoomView != null)
            zoomView.setOnTouchListener(null);
        if (zoomControl != null)
            zoomControl.getZoomState().deleteObservers();
    }

    /**
     * Reset zoom state and notify observers
     */
    private void resetZoomState() {
        zoomControl.getZoomState().setPanX(0.5f);
        zoomControl.getZoomState().setPanY(0.5f);
        zoomControl.getZoomState().setZoom(2f);
        zoomControl.getZoomState().notifyObservers();
        zoomView.clearRecordedPoints();
    }


    private void resetLocation() {
        curPosition.setPositionCoordinates(-1, -1);
        curPosition.notifyObservers();
    }

    @Override
    public void update(Observable observable, Object data) {
        // Clear all overlays if it is not tracking
        if (!positionMe.get()) {
            zoomView.clearRecordedPoints();
        }

    }

    public boolean okBuildingSettings() {
        return this.bitmap != null && this.floorPlanHeight != null && this.floorPlanWidth != null;
    }

}
