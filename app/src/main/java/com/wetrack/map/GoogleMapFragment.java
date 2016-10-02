package com.wetrack.map;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wetrack.BaseApplication;
import com.wetrack.R;
import com.wetrack.utils.ConstantValues;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.wetrack.utils.MathUtils;

import java.util.ArrayList;

public class GoogleMapFragment extends SupportMapFragment {

    private GoogleMap mMap;
    static private Context mContext;

    public static GoogleMapFragment newInstance(Context context) {
        mContext = context;
        return new GoogleMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

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
            } catch (SecurityException e) {
                e.printStackTrace();
            }

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    return false;
                }
            });
        }
    }

    /**
     * if latitudeRangeLength == -1 || longitudeRangeLength == -1,
     * then these two paramters will not be used
     * */
    public void setCameraLocation(LatLng centerPoint, double latitudeRangeLength, double longitudeRangeLength) {

        CameraPosition.Builder builder = new CameraPosition.Builder().bearing(0).target(centerPoint).tilt(0);
        if (latitudeRangeLength != -1 && longitudeRangeLength != -1) {
            builder.zoom(MathUtils.getZoomFromLatLngRange(getView().getHeight(), getView().getWidth(), latitudeRangeLength, longitudeRangeLength));
        }
        CameraPosition cameraPosition = builder.build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private ArrayList<Marker>allMarkers = new ArrayList<>();

    synchronized public void markersArrayListOperation(int operationCode, Marker marker) {
        switch (operationCode) {
            case ConstantValues.MARKERLIST_CLEAR:
                allMarkers.clear();
                break;
            case ConstantValues.MARKERLIST_ADD:
                allMarkers.add(marker);
                break;
            default:
                break;
        }
    }

    public void addMarker(String title, LatLng latLng, String information) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng).title(title).snippet(information).alpha(0.8f);
        Marker currentMarker = mMap.addMarker(markerOptions);
        markersArrayListOperation(ConstantValues.MARKERLIST_ADD, currentMarker);

//        currentMarker.showInfoWindow();
    }

    public void clearMarkers() {
        markersArrayListOperation(ConstantValues.MARKERLIST_CLEAR, null);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
