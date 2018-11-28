package com.bytes.fightr.client.service.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.bytes.fightr.client.service.Persistable;


import com.bytes.fightr.client.service.logger.Logger;
import com.bytes.fightr.client.service.logger.LoggerFactory;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Kent on 10/21/2015.
 * Its purpose is to provide GPS coordinates
 */
public class LocationService implements
        Persistable, ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private Logger logger = LoggerFactory.getLogger(LocationService.class);
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    private final static String UPDATE_ENABLED_KEY = "requesting-location-updates-key";
    private final static String LOCATION_KEY = "location-key";
    private final static String LAST_UPDATED_TIME_KEY = "last-updated-time-string-key";

    /** Provides the entry point to Google Play services. */
    private GoogleApiClient googleApiClient;

    /** Stores parameters for requests to the FusedLocationProviderApi. */
    private LocationRequest locationRequest;

    /** Represents a geographical location. */
    private Location currentLocation;

    /** Tracks the status of the location updates request. */
    private Boolean updateEnabled;

    /** Time when the location was updated represented as a String. */
    private String lastUpdateTime;

    private Context context;
    private LocationListener listener;

    /**
     * Creates a new location service adapter for the specified activity
     * @param context the activity
     */
    public LocationService(Context context, LocationListener listener) {
        this.context = context;
        this.updateEnabled = true;  // enabled by default for testing
        this.lastUpdateTime = "";
        this.listener = listener;

        buildGoogleApiClient();
        createLocationRequest();
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    private synchronized void buildGoogleApiClient() {
        logger.info("Building GoogleApiClient");
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

    }


    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast addUser
     * interval (5 seconds), the Fused Track Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private void createLocationRequest() {
        locationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Connects to GoogleAPI
     */
    public void start() {
        googleApiClient.connect();
    }

    /**
     * Disconnect from GoogleAPI
     */
    public void stop() {
        googleApiClient.disconnect();
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    private void startLocationUpdates(LocationListener listener) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient,
                locationRequest,
                listener);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    public void stopLocationUpdates(LocationListener listener) {

        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, listener);
    }

    /**
     * Within {@code onPause()}, we pause location updates, but leave the
     * connection to GoogleApiClient intact.  Here, we resume receiving
     * location updates if the user has requested them.
     */
    public void resume() {
        if (googleApiClient.isConnected() && updateEnabled) {
            startLocationUpdates(listener);
        }
    }

    /**
     * Stops location updates to save battery,
     * but don't disconnect the GoogleApiClient object.
     */
    public void pause() {
        if (googleApiClient.isConnected()) {
            stopLocationUpdates(listener);
        }
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        logger.info("Connected to GoogleApiClient");

        if (currentLocation == null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            lastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // updateEnabled to true (see startUpdatesButtonHandler()). Here, we check
        // the value of updateEnabled and if it is true, we start location updates.
        if (updateEnabled) {
            startLocationUpdates(listener);
        }
    }


    /**
     * Callback that fires when the location changes.
     *
     * NOTE: This is currently not used.
     * The listener is not set to this class. This will be used in the future
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        lastUpdateTime = DateFormat.getTimeInstance().format(new Date());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        logger.info("Connection suspended");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in onConnectionFailed.
        logger.info("Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        logger.info("Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of updateEnabled from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(UPDATE_ENABLED_KEY)) {
                updateEnabled = savedInstanceState.getBoolean(
                        UPDATE_ENABLED_KEY);
            }

            // Update the value of currentLocation from the Bundle and addUser the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that currentLocation
                // is not null.
                currentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of lastUpdateTime from the Bundle and addUser the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_KEY)) {
                lastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_KEY);
            }
        }
    }

    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(UPDATE_ENABLED_KEY, updateEnabled);
        savedInstanceState.putParcelable(LOCATION_KEY, currentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_KEY, lastUpdateTime);
    }

    public boolean isUpdateEnabled() {
        return updateEnabled;
    }

    public void setUpdateEnabled(boolean updateEnabled) {
        this.updateEnabled = updateEnabled;
    }
}
