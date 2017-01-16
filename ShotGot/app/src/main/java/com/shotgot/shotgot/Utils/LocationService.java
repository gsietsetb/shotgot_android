package com.shotgot.shotgot.Utils;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * Created by gsierra on 16/01/17.
 */

public class LocationService implements LocationListener {
    //The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

    //The minimum time beetwen updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0;//1000 * 60 * 1; // 1 minute

    private final static boolean forceNetwork = false;

    private static LocationService instance = null;
    public Location location;
    public double longitude;
    public double latitude;
    private LocationManager locationManager;
    private boolean isGPSEnabled;
    private boolean isNetworkEnabled;
    private boolean locationServiceAvailable;


    /**
     * Singleton implementation
     * @return public static LocationService getLocationManager(Context context)     {
    if (instance == null) {
    instance = new LocationService(context);
    }
    return instance;
    }*/

    /**
     * Local constructor

     private LocationService( Context context )     {

     initLocationService(context);
     Log.d("MAPS","LocationService created");
     }*/

    /**
     * Sets up location service after permissions is granted
     *
     * @TargetApi(23) private void initLocationService(Context context) {
     * if ( Build.VERSION.SDK_INT >= 23 &&
     * ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
     * ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
     * return  ;
     * }
     * <p>
     * try   {
     * this.longitude = 0.0;
     * this.latitude = 0.0;
     * this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
     * <p>
     * // Get GPS and network status
     * this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
     * this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
     * <p>
     * if (forceNetwork) isGPSEnabled = false;
     * <p>
     * if (!isNetworkEnabled && !isGPSEnabled)    {
     * // cannot get location
     * this.locationServiceAvailable = false;
     * }
     * //else
     * {
     * this.locationServiceAvailable = true;
     * <p>
     * if (isNetworkEnabled) {
     * locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
     * MIN_TIME_BW_UPDATES,
     * MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
     * if (locationManager != null)   {
     * location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
     * updateCoordinates();
     * }
     * }//end if
     * <p>
     * if (isGPSEnabled)  {
     * locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
     * MIN_TIME_BW_UPDATES,
     * MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
     * <p>
     * if (locationManager != null)  {
     * location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
     * updateCoordinates();
     * }
     * }
     * }
     * } catch (Exception ex)  {
     * LogService.log( "Error creating location service: " + ex.getMessage() );
     * <p>
     * }
     * }
     */

    @Override
    public void onLocationChanged(Location location) {
        // do stuff here with location object
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
