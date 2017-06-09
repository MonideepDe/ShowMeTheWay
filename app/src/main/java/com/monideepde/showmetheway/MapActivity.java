package com.monideepde.showmetheway;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


public class MapActivity extends ActionBarActivity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG_MAP_ACTIVITY = "MapActivity";
    private GoogleMap googleMap;

    GoogleApiClient mGoogleApiClient;
    Boolean mRequestingLocationUpdates;
    Location mCurrentLocation;
    Boolean mIsGoogleApiClientConnected;
    int mGPSIntervalMillis;
    int mGPSFastestIntervalMillis;
    LocationRequest mLocationRequest;

    String mDestinationName;
    String mDestinationCoordinates;
    LatLng mDestinationCoordinatesLatLngObj;

    Polyline mLastPolyLine; //used for removing old polyline

    public MapActivity() {
        mRequestingLocationUpdates = false;
        mIsGoogleApiClientConnected=false;

        mGPSIntervalMillis = 1000;
        mGPSFastestIntervalMillis = 1000;
        mLastPolyLine = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG_MAP_ACTIVITY, "Entering onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        mDestinationName = intent.getStringExtra(HomeActivity.DESTINATION_NAME);
        getDestinationCoordinates();


        //setup google api client for getting current location
        buildGoogleApiClient();
        createLocationRequest();


        try{
            if(googleMap == null) {
                googleMap = ((MapFragment) (getFragmentManager().findFragmentById(R.id.map))).getMap();
            }

            //Set all the google map features
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setMyLocationEnabled(true);
            googleMap.setIndoorEnabled(true);
            googleMap.setBuildingsEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setAllGesturesEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.setMyLocationEnabled(true);
            googleMap.addMarker(new MarkerOptions().position(mDestinationCoordinatesLatLngObj).title(mDestinationName));
            googleMap.getUiSettings().setCompassEnabled(true);
            googleMap.getUiSettings().setIndoorLevelPickerEnabled(true);


        } catch (Exception e) {
            Log.d(TAG_MAP_ACTIVITY, "Could not create google map object", e);
        }
        //Show message to user indicating how to enable directions
        Toast.makeText(getApplicationContext(),"Use two (sets of) fingers to orientation of map on screen, to enable compass", Toast.LENGTH_LONG).show();

        Log.d(TAG_MAP_ACTIVITY, "Exiting onCreate");
    }


    public void getDestinationCoordinates() {
        Log.d(TAG_MAP_ACTIVITY, "Entering getDestinationCoordinates");
        //Get DBTools instance
        DBTools dbTools = DBTools.getInstance(getApplicationContext());

        mDestinationCoordinates = dbTools.getGPSCoordFromName(mDestinationName);

        //Create LatLng Obj
        int indexOfComma = mDestinationCoordinates.indexOf(',');
        String latitude = mDestinationCoordinates.substring(0, indexOfComma);
        String longitude = mDestinationCoordinates.substring(indexOfComma+1);

        Log.d(TAG_MAP_ACTIVITY, "Latitude=" + latitude + "; Longitude=" + longitude);
        Log.d(TAG_MAP_ACTIVITY, "Exiting getDestinationCoordinates");

        mDestinationCoordinatesLatLngObj = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
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

    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG_MAP_ACTIVITY, "Entering buildGoogleApiClient");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        Log.d(TAG_MAP_ACTIVITY, "Exiting buildGoogleApiClient");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mIsGoogleApiClientConnected == true) {
            // Google API will not be connected when the activity starts up for the first time.
            //It takes some time for it to be connected
            startLocationUpdates();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG_MAP_ACTIVITY, "Entering onConnected");

        mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        //Now I can set flag to indicate that Google API client is connected
        mIsGoogleApiClientConnected = true;

        startLocationUpdates();

        Log.d(TAG_MAP_ACTIVITY, "Exiting onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG_MAP_ACTIVITY, "Entering onPause");

        super.onPause();
        if(mRequestingLocationUpdates) {
            stopLocationUpdates();
            Log.d(TAG_MAP_ACTIVITY, "onPause: Stopped location updates");
        }

        Log.d(TAG_MAP_ACTIVITY, "Exiting onPause");
    }

    protected void createLocationRequest(){
        Log.d(TAG_MAP_ACTIVITY, "Entering createLocationRequest");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(mGPSIntervalMillis);
        mLocationRequest.setFastestInterval(mGPSFastestIntervalMillis);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.d(TAG_MAP_ACTIVITY, "createLocationRequest: mGPSIntervalMillis=" + mGPSIntervalMillis + "; mGPSFastestIntervalMillis=" + mGPSFastestIntervalMillis);
        Log.d(TAG_MAP_ACTIVITY, "Exiting createLocationRequest");
    }

    protected void startLocationUpdates() {
        Log.d(TAG_MAP_ACTIVITY, "Entering startLocationUpdates");

        Log.d(TAG_MAP_ACTIVITY, "startLocationUpdates: mGPSIntervalMillis=" + mGPSIntervalMillis + "; mGPSFastestIntervalMillis=" + mGPSFastestIntervalMillis);
        mLocationRequest.setInterval(mGPSIntervalMillis);
        mLocationRequest.setFastestInterval(mGPSFastestIntervalMillis);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest, this);

        //Let the user know that updates have started
        Toast.makeText(this.getApplicationContext(), "Regular Location Updates Started", Toast.LENGTH_SHORT).show();

        //Location uodates started
        mRequestingLocationUpdates=true;

        Log.d(TAG_MAP_ACTIVITY, "Exiting startLocationUpdates");
    }

    protected void stopLocationUpdates() {
        Log.d(TAG_MAP_ACTIVITY, "Entering stopLocationUpdates");

        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

        //Let the user know that the location updates have stopped
        Toast.makeText(this.getApplicationContext(), "Regular Location Updates Stopped", Toast.LENGTH_SHORT).show();

        Log.d(TAG_MAP_ACTIVITY, "Exiting stopLocationUpdates");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG_MAP_ACTIVITY, "Entering onLocationChanged");

        mCurrentLocation = location;

        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()), 20);
        googleMap.animateCamera(cameraUpdate);

        //Add polyline
        if(mLastPolyLine != null) {
            Log.d(TAG_MAP_ACTIVITY, "onLocationChanged: Removing polyline");
            mLastPolyLine.remove();
        } else {
            Log.d(TAG_MAP_ACTIVITY, "onLocationChanged: polyline detected null");
        }
        LatLng currLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        mLastPolyLine = googleMap.addPolyline(new PolylineOptions().add(currLatLng, mDestinationCoordinatesLatLngObj).width(5).color(Color.GREEN));


        Log.d(TAG_MAP_ACTIVITY, "Exiting onLocationChanged");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
