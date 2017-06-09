package com.monideepde.showmetheway;

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.LocationListener;

import java.util.HashMap;
import java.util.SimpleTimeZone;


public class LocationAddActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG_LOCATION_ADD_ACTIVITY = LocationAddActivity.class.getSimpleName();
    MapView mapView;
    GoogleMap googleMap;

    GoogleApiClient mGoogleApiClient;
    Boolean mRequestingLocationUpdates;
    Location mCurrentLocation;
    Boolean mIsGoogleApiClientConnected;
    int mGPSIntervalMillis;
    int mGPSFastestIntervalMillis;
    LocationRequest mLocationRequest;

    public LocationAddActivity() {
        mRequestingLocationUpdates = false;
        mIsGoogleApiClientConnected=false;

        mGPSIntervalMillis = 1000;
        mGPSFastestIntervalMillis = 1000;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_add);

        //setup google api client for getting current location
        buildGoogleApiClient();
        createLocationRequest();

        //setup mapView
        setUpMapView(savedInstanceState);

    }

    private void setUpMapView(Bundle savedInstanceState) {

        Log.d(TAG_LOCATION_ADD_ACTIVITY, "Entering setUpMapView");
        //Setup mapview
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        googleMap = mapView.getMap();

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setMyLocationEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setIndoorLevelPickerEnabled(true);


        //some more MapView Stuff
        try {
            MapsInitializer.initialize(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Show message to user indicating how to enable directions
        Toast.makeText(getApplicationContext(),"Use two (sets of) fingers to orientation of map on screen, to enable compass", Toast.LENGTH_LONG).show();

//
//        Location location = googleMap.getMyLocation();
//        if(location == null) {
//            Log.d(TAG_LOCATION_ADD_ACTIVITY, "Location is null");
//            return;
//        }
//        Double lat = location.getLatitude();
//        Double longi = location.getLongitude();
//
//        if(lat == null || longi == null) {
//            Log.d(TAG_LOCATION_ADD_ACTIVITY, "Latitude or Longitude is null");
//            return;
//
//        }
//
//        String locationString = lat.toString() + "," + longi.toString();
//        Log.d(TAG_LOCATION_ADD_ACTIVITY, "Location String: " + locationString);

    }


    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG_LOCATION_ADD_ACTIVITY, "Entering buildGoogleApiClient");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        Log.d(TAG_LOCATION_ADD_ACTIVITY, "Exiting buildGoogleApiClient");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
        if(mIsGoogleApiClientConnected == true) {
            // Google API will not be connected when the activity starts up for the first time.
            //It takes some time for it to be connected
            startLocationUpdates();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG_LOCATION_ADD_ACTIVITY, "Entering onConnected");

        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        //Now I can set flag to indicate that Google API client is connected
        mIsGoogleApiClientConnected = true;

        startLocationUpdates();

        Log.d(TAG_LOCATION_ADD_ACTIVITY, "Exiting onConnected");
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();

        Log.d(TAG_LOCATION_ADD_ACTIVITY, "Entering onPause");

        super.onPause();
        if(mRequestingLocationUpdates) {
            stopLocationUpdates();
            Log.d(TAG_LOCATION_ADD_ACTIVITY, "onPause: Stopped location updates");
        }

        Log.d(TAG_LOCATION_ADD_ACTIVITY, "Exiting onPause");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void createLocationRequest(){
        Log.d(TAG_LOCATION_ADD_ACTIVITY, "Entering createLocationRequest");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(mGPSIntervalMillis);
        mLocationRequest.setFastestInterval(mGPSFastestIntervalMillis);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.d(TAG_LOCATION_ADD_ACTIVITY, "createLocationRequest: mGPSIntervalMillis=" + mGPSIntervalMillis + "; mGPSFastestIntervalMillis=" + mGPSFastestIntervalMillis);
        Log.d(TAG_LOCATION_ADD_ACTIVITY, "Exiting createLocationRequest");
    }

    protected void startLocationUpdates() {
        Log.d(TAG_LOCATION_ADD_ACTIVITY, "Entering startLocationUpdates");

        Log.d(TAG_LOCATION_ADD_ACTIVITY, "startLocationUpdates: mGPSIntervalMillis=" + mGPSIntervalMillis + "; mGPSFastestIntervalMillis=" + mGPSFastestIntervalMillis);
        mLocationRequest.setInterval(mGPSIntervalMillis);
        mLocationRequest.setFastestInterval(mGPSFastestIntervalMillis);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, this);

        //Let the user know that updates have started
        Toast.makeText(this.getApplicationContext(), "Regular Location Updates Started", Toast.LENGTH_SHORT).show();

        //Location uodates started
        mRequestingLocationUpdates=true;

        Log.d(TAG_LOCATION_ADD_ACTIVITY, "Exiting startLocationUpdates");
    }

    protected void stopLocationUpdates() {
        Log.d(TAG_LOCATION_ADD_ACTIVITY, "Entering stopLocationUpdates");

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        //Let the user know that the location updates have stopped
        Toast.makeText(this.getApplicationContext(), "Regular Location Updates Stopped", Toast.LENGTH_SHORT).show();

        Log.d(TAG_LOCATION_ADD_ACTIVITY, "Exiting stopLocationUpdates");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG_LOCATION_ADD_ACTIVITY, "Entering onLocationChanged");

        mCurrentLocation = location;

        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 20);
        googleMap.animateCamera(cameraUpdate);


        Log.d(TAG_LOCATION_ADD_ACTIVITY, "Exiting onLocationChanged");
    }

    /*
    This will be called when SAVE button is clicked.
    It will save the current location in database.
     */
    public void saveCurrentLocation(View v) {

        //Get DBTools instance
        DBTools dbTools = DBTools.getInstance(getApplicationContext());

        //Get Location name
        String locName = (((EditText) findViewById(R.id.locationName_editText)).getText()).toString();

        //check if we have a valid location name - no nulls and whitespaces
        if(locName == null) {
            Toast.makeText(getApplicationContext(), "Specify a location name!", Toast.LENGTH_LONG).show();
            return;
        } else {
            locName.trim();
            if(locName.equals("")) {
                Toast.makeText(getApplicationContext(), "Specify a non-whitespace location name!", Toast.LENGTH_LONG).show();
                return;
            }
        }

        //Get the GPS Coords
        String gpsCoords = ((Double) mCurrentLocation.getLatitude()).toString() + "," + ((Double) mCurrentLocation.getLongitude()).toString();

        HashMap<String, String> values = new HashMap<>();
        values.put(DBTools.destination_name, locName);
        values.put(DBTools.destination_GPSCoord, gpsCoords);


        dbTools.insertDestination(values);
        Toast.makeText(getApplicationContext(), "Location Added!", Toast.LENGTH_LONG).show();

        //Finish the activity
        finish();
    }

}
