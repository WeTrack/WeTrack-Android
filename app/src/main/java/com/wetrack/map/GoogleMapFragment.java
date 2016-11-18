package com.wetrack.map;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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
            UiSettings myUiSettings = mMap.getUiSettings();
            myUiSettings.setCompassEnabled(true);
            myUiSettings.setMyLocationButtonEnabled(true);
            myUiSettings.setZoomControlsEnabled(true);

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    return false;
                }
            });

            mMap.setOnInfoWindowClickListener(new MyOnInfoWindowClickListener());

        }
    }

    /**
     * if latitudeRangeLength == -1 || longitudeRangeLength == -1,
     * then these two paramters will not be used
     */
    public void setCameraLocation(LatLng centerPoint, double latitudeRangeLength, double longitudeRangeLength) {

        CameraPosition.Builder builder = new CameraPosition.Builder().bearing(0).target(centerPoint).tilt(0);
        if (latitudeRangeLength != -1 && longitudeRangeLength != -1) {
            builder.zoom(MathUtils.getZoomFromLatLngRange(getView().getHeight(), getView().getWidth(), latitudeRangeLength, longitudeRangeLength));
        }
        CameraPosition cameraPosition = builder.build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    //the below 4 functions are for marker operation
    private ArrayList<Marker> allMarkers = new ArrayList<>();

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
//        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(R.drawable.));
        Marker currentMarker = mMap.addMarker(markerOptions);
        markersArrayListOperation(ConstantValues.MARKERLIST_ADD, currentMarker);
    }

    public void clearMarkers() {
        markersArrayListOperation(ConstantValues.MARKERLIST_CLEAR, null);
    }

    //the below 4 are for marker OnInfoWindowClickListener
    private class MyOnInfoWindowClickListener implements GoogleMap.OnInfoWindowClickListener {
        @Override
        public void onInfoWindowClick(Marker marker) {
            if (mOnInfoWindowClickListener != null) {;
                mOnInfoWindowClickListener.onInfoWindowClick(
                        new MarkerDataFormat(marker.getTitle(), marker.getPosition(), marker.getSnippet()));
            }
        }
    }

    private OnInfoWindowClickListener mOnInfoWindowClickListener = null;

    public void setmOnInfoWindowClickListener(OnInfoWindowClickListener mOnInfoWindowClickListener) {
        this.mOnInfoWindowClickListener = mOnInfoWindowClickListener;
    }

    public interface OnInfoWindowClickListener {
        public void onInfoWindowClick(MarkerDataFormat markerData);
    }

    //the below is for navigation
    public void drawPathOnMap(ArrayList<LatLng>positions) {
        PolylineOptions polylineOptions = new PolylineOptions();
        for (LatLng position : positions) {
            polylineOptions.add(position);
        }
        polylineOptions.color(Color.BLUE).width(10);
        Polyline polyline = mMap.addPolyline(polylineOptions);
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
