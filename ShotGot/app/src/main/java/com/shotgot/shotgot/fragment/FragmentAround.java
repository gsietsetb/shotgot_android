package com.shotgot.shotgot.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shotgot.shotgot.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class FragmentAround extends ImmersiveModeFragment implements LocationListener {

    /**
     * TODO checking http://stackoverflow.com/questions/34582370/how-can-i-show-current-location-on-a-google-map-on-android-marshmallow
     *
     * @param point
     * @param radius
     * @return protected synchronized void buildGoogleApiClient() {
     * mGoogleApiClient = new GoogleApiClient.Builder(getContext())
     * .addConnectionCallbacks(getContext().)
     * .addOnConnectionFailedListener(getContext())
     * .addApi(LocationServices.API)
     * .build();
     * mGoogleApiClient.connect();
     * }
     */

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    MapView mMapView;
    Location mLastLocation;
    LatLng myLatLng;
    LatLng arg = new LatLng(34, 58.38);

    GoogleApiClient mGoogleApiClient;
    List<Marker> availItems;
    private GoogleMap mGoogleMap;

    public FragmentAround() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentAround.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentAround newInstance(String param1, String param2) {
        FragmentAround fragment = new FragmentAround();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_around, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mGoogleMap = mMap;
                mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                //Initialize Google Play Services
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        //Location Permission already granted
//                        buildGoogleApiClient();
                        mGoogleMap.setMyLocationEnabled(true);
                    } else {
                        //Request Location Permission
                        checkLocationPermission();
                    }
                } else {
//                    buildGoogleApiClient();
                    mGoogleMap.setMyLocationEnabled(true);
                    Log.e("MAPS", "Properly located");
                }
                //Update own loc
                /*onLocationChanged(mGoogleMap.getMyLocation());*/
                mLastLocation = mGoogleMap.getMyLocation();
                if(mLastLocation!= null){
                    myLatLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                    updateFromDB();
                }

                /**
                 * TODO Aggregate for-statement
                 * to recurively update from DB
                 */

                LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();

                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                if (location != null) {
                    myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 13));
//                     For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(arg).zoom(11).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    updateFromDB();
                }

            }
        });

        return rootView;
    }

    private void updateFromDB() {
        //First create N random fake values
        final int nFakeItems = 5;
        for (int i = 0; i < nFakeItems; i++) {
            addMarker(1);
            Log.d("MAPS", "Adding " + i + "-nth new fake marker");
        }
    }

    public void addMarker(int num) {
        mGoogleMap.addMarker(new MarkerOptions()
//                .icon(getRandomIcon())//R.drawable.airport))
                .position(getRandomLocation(myLatLng, 5000, num))
                .flat(true)
                .title("Camera Reflex")
                .snippet("Illawong, Australia"));
    }

    public BitmapDescriptor getRandomIcon() {
        final Class drawableClass = android.R.drawable.class;
//      final Field[] fields = drawableClass.getFields();
        final Random rand = new Random();
        /**Needed to create aN.png files where N is an integer in
         * order to obtain random icons from sequencial numbers*/
        int resId = getResources().getIdentifier("a" + rand.nextInt(29), "drawable", getActivity().getPackageName());
        return BitmapDescriptorFactory.fromResource(resId);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(getContext())
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    public LatLng getRandomLocation(LatLng point, int radius, int num) {
        List<LatLng> randomPoints = new ArrayList<>();
        List<Float> randomDistances = new ArrayList<>();
        Location myLocation = new Location("");
        myLocation.setLatitude(point.latitude);
        myLocation.setLongitude(point.longitude);

        //This is to generate num random points
        for (int i = 0; i < num; i++) {

            double x0 = point.latitude;
            double y0 = point.longitude;

            Random random = new Random();

            // Convert radius from meters to degrees
            double radiusInDegrees = radius / 111000f;

            double u = random.nextDouble();
            double v = random.nextDouble();
            double w = radiusInDegrees * Math.sqrt(u);
            double t = 2 * Math.PI * v;
            double x = w * Math.cos(t);
            double y = w * Math.sin(t);

            // Adjust the x-coordinate for the shrinking of the east-west distances
            double new_x = x / Math.cos(y0);

            double foundLatitude = new_x + x0;
            double foundLongitude = y + y0;
            LatLng randomLatLng = new LatLng(foundLatitude, foundLongitude);
            randomPoints.add(randomLatLng);
            Location l1 = new Location("");
            l1.setLatitude(randomLatLng.latitude);
            l1.setLongitude(randomLatLng.longitude);
            randomDistances.add(l1.distanceTo(myLocation));
        }
        //Get nearest point to the centre
        int indexOfNearestPointToCentre = randomDistances.indexOf(Collections.min(randomDistances));
        return randomPoints.get(indexOfNearestPointToCentre);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        //Place current location marker
        myLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        //move map camera to user's last known location
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        /**TODO check if need API google
         * optionally, stop location updates if only current location is needed
         *
         if (mGoogleApiClient != null) {
         LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
         }*/
//        updateFromDB();
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

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
