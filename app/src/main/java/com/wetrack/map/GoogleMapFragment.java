package com.wetrack.map;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.wetrack.R;
import com.wetrack.utils.MathUtils;
import com.wetrack.utils.Tags;
import com.wetrack.utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.location.Geocoder;
import android.widget.Toast;

public class GoogleMapFragment extends SupportMapFragment {

    private GoogleMap mMap = null;
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

            if (mOnMarkerClickListener != null) {
                mMap.setOnMarkerClickListener(mOnMarkerClickListener);
            }
        }
    }

    GoogleMap.OnMarkerClickListener mOnMarkerClickListener = null;
    public void setmOnMarkerClickListener(GoogleMap.OnMarkerClickListener onMarkerClickListener) {
        mOnMarkerClickListener = onMarkerClickListener;
    }

    /**
     * if latitudeRangeLength == -1 || longitudeRangeLength == -1,
     * then these two paramters will not be used
     */
    private float currentZoomLevel = 14;
    public void setCameraLocation(LatLng centerPoint, double latitudeRangeLength, double longitudeRangeLength) {

        CameraPosition.Builder builder = new CameraPosition.Builder().bearing(0).target(centerPoint).tilt(0);
        if (latitudeRangeLength != -1 && longitudeRangeLength != -1) {
            currentZoomLevel = MathUtils.getZoomFromLatLngRange(
                    getView().getHeight(), getView().getWidth(),
                    latitudeRangeLength, longitudeRangeLength);

        }
        builder.zoom(currentZoomLevel);
        CameraPosition cameraPosition = builder.build();

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    //the below 4 functions are for marker operation
    private Map<String, Marker> allMarkers = new HashMap<>();
    private Object allMarkersSynObject = new Object();
    public void addMarker(String title, LatLng latLng) {
        synchronized (allMarkersSynObject) {
            if (allMarkers.containsKey(title)) {
                Marker marker = allMarkers.get(title);
                marker.setPosition(latLng);
                asynGetLocationInfo(title, latLng);
                return;
            }
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng).title(title).snippet("getting location description").alpha(0.8f);
        Marker currentMarker = mMap.addMarker(markerOptions);
        currentMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                Tools.getMarkerFromBitmap(BitmapFactory.decodeResource(
                        mContext.getResources(), R.drawable.portrait_boy))));
        asynGetLocationInfo(title, latLng);
        synchronized (allMarkersSynObject) {
            allMarkers.put(currentMarker.getTitle(), currentMarker);
        }
        // check if it is the first time show location
        int mapSize;
        synchronized (allMarkersSynObject) {
            mapSize = allMarkers.size();
        }
        if (mapSize == 1) {
            List<Marker> allValues;
            synchronized (allMarkersSynObject) {
                allValues = new ArrayList<>(allMarkers.values());
            }
            ArrayList<LatLng> locations = new ArrayList<>();
            for (Marker mk : allValues) {
                locations.add(mk.getPosition());
            }
            double[] result = MathUtils.getCenterAndLengthRange(locations);
            double centerLatitude = result[0];
            double centerLongitude = result[1];
            double latitudeRangeLength = result[2];
            double longitudeRangeLength = result[3];

            setCameraLocation(
                    new LatLng(centerLatitude, centerLongitude),
                    latitudeRangeLength,
                    longitudeRangeLength);

        }
    }

    public void clearAllSymbols() {
        synchronized (allMarkersSynObject) {
            allMarkers.clear();
        }
        mMap.clear();
    }

    private void asynGetLocationInfo(final String title, final LatLng latLng) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(Tags.Map.MARKER, "asynGetLocationInfo thread runs");
                Geocoder geocoder = new Geocoder(mContext, Locale.ENGLISH);
                try {
                    List<Address> addresses = geocoder.getFromLocation(latLng.latitude,
                            latLng.longitude, 1);
                    Address address = addresses.get(0);
                    String result = "";
                    for (int i = 0; i < address.getMaxAddressLineIndex() - 1; i++) {
                        result += address.getAddressLine(i) + ", ";
                    }
                    result += address.getCountryCode();

                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putString("title", title);
                    bundle.putString("snippet", result);
                    message.setData(bundle);
                    handler.sendMessage(message);
                    Log.d(Tags.Map.MARKER, "asynGetLocationInfo result: " + result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    Polyline currentPolyline = null;
    public void drawPathOnMap(ArrayList<LatLng> positions) {
        if (currentPolyline != null) {
            currentPolyline.remove();
        }
        PolylineOptions polylineOptions = new PolylineOptions();
        for (LatLng position : positions) {
            polylineOptions.add(position);
        }
        polylineOptions.color(Color.BLUE).width(10);
        currentPolyline = mMap.addPolyline(polylineOptions);
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String title = bundle.getString("title");
            String snippet = bundle.getString("snippet");
            synchronized (allMarkersSynObject) {
                if (allMarkers.containsKey(title)) {
                    allMarkers.get(title).setSnippet(snippet);
                }
            }
            return false;
        }
    });
}
