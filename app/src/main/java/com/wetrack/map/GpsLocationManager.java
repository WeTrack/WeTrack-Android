package com.wetrack.map;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.wetrack.utils.ConstantValues;

/**
 * Created by moziliang on 16/10/2.
 */
public class GpsLocationManager {
    private static GpsLocationManager mGpsLocationManager = null;

    public static GpsLocationManager getInstance(Context context) {
        if (mGpsLocationManager == null) {
            mGpsLocationManager = new GpsLocationManager(context);
        }
        return mGpsLocationManager;
    }

    private Context mContext;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private MyLocationListener myLocationListener;

    private GpsLocationManager(Context context) {
        mContext = context;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(500);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new MyConnectionCallbacks())
                .addOnConnectionFailedListener(new MyOnConnectionFailedListener())
                .build();

        myLocationListener = new MyLocationListener();
    }

    public void start() {
        mGoogleApiClient.connect();
    }

    public void stop() {
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
    }


    private class MyConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.e(ConstantValues.gpsDebug, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
            startLocationUpdates();
            Log.e(ConstantValues.gpsDebug, "Location update started ..............: ");
        }

        @Override
        public void onConnectionSuspended(int i) {

        }
    }

    private class MyOnConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener {
        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.e(ConstantValues.gpsDebug, "Connection failed: " + connectionResult.toString());
        }
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            Log.e(ConstantValues.gpsDebug, "Firing onLocationChanged..............................................");
            Log.e(ConstantValues.gpsDebug, "location: " + location.getLatitude() + ", " + location.getLongitude());
            if (mGpsLocationListener != null) {
                mGpsLocationListener.onGpsLocationReceived(location);
            }
        }
    }

    protected void startLocationUpdates() {
        try {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, myLocationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, myLocationListener);
        Log.e(ConstantValues.gpsDebug, "Location update stopped .......................");
    }

    private GpsLocationListener mGpsLocationListener = null;

    public interface GpsLocationListener {
        public void onGpsLocationReceived(Location location);
    }

    public void setmGpsLocationListener(GpsLocationListener gpsLocationListener) {
        mGpsLocationListener = gpsLocationListener;
    }
}
