package com.wetrack.map;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wetrack.BaseApplication;
import com.wetrack.utils.ConstantValues;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MapFragment extends SupportMapFragment {

    private GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getMapAsync(new MyOnMapReadyCallback());

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private class MyOnMapReadyCallback implements OnMapReadyCallback {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            try {
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationChangeListener(new MyLocationListener());
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }


    private class MyLocationListener implements GoogleMap.OnMyLocationChangeListener {
        public void onMyLocationChange(Location location){
            Log.d(ConstantValues.debugTab,
                    "LocationListener in MapFragment: (" + location.getLatitude() + ", " + location.getLongitude() + ")");
            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
        }
    }
}
