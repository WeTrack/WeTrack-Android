package com.wetrack.map;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by moziliang on 16/10/2.
 */
public class MapController {
    private static MapController mMapController = null;

    public static MapController getInstance(Context context) {
        if (mMapController == null) {
            mMapController = new MapController(context);
        }
        return mMapController;
    }

    private Context mContext;
    private GoogleMapFragment googleMapFragment;

    private MapController(Context context) {
        mContext = context;
    }

    public void addMapToView(FragmentManager fragmentManager, int viewId) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        googleMapFragment = GoogleMapFragment.newInstance(mContext);
        fragmentTransaction.add(viewId, googleMapFragment, "map_fragment");
        fragmentTransaction.commit();

        GpsLocationManager.getInstance(mContext).setmGpsLocationListener(new GpsLocationManager.GpsLocationListener() {
            @Override
            public void onGpsLocationReceived(Location location) {
                myCurrentLocation = location;
            }
        });
    }

    /**
     * myCurrentLocation is the last gps location
     * */
    private Location myCurrentLocation = null;
    public LatLng getMyLocation() {
        if (myCurrentLocation != null) {
            return new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude());
        }
        return null;
    }

    public void resetMarkers(ArrayList<MarkerDataFormat> markerData, boolean resetCamera) {
        googleMapFragment.clearMarkers();
        for (MarkerDataFormat aMarkerData : markerData) {
            googleMapFragment.addMarker(aMarkerData.title, aMarkerData.latLng, aMarkerData.information);
        }


        if (resetCamera) {
            double allLatitude = 0;
            double allLongitude = 0;
            double latitudeMax = -90, latitudeMin = 90;
            double longitudeMax = -180, longitudeMin = 180;
            for (MarkerDataFormat aMarkerData : markerData) {
                allLatitude += aMarkerData.latLng.latitude;
                allLongitude += aMarkerData.latLng.longitude;

                if (aMarkerData.latLng.latitude > latitudeMax) {
                    latitudeMax = aMarkerData.latLng.latitude;
                }
                if (aMarkerData.latLng.latitude < latitudeMin){
                    latitudeMin = aMarkerData.latLng.latitude;
                }
                if (aMarkerData.latLng.longitude > longitudeMax) {
                    longitudeMax = aMarkerData.latLng.longitude;
                }
                if (aMarkerData.latLng.longitude < longitudeMin) {
                    longitudeMin = aMarkerData.latLng.longitude;
                }
            }
            allLatitude /= markerData.size();
            allLongitude /= markerData.size();

            double latitudeRangeLength = Math.abs(latitudeMax - latitudeMin);
            double longitudeRangeLength = Math.abs(longitudeMax - longitudeMin);

            googleMapFragment.setCameraLocation(
                    new LatLng(allLatitude, allLongitude),
                    latitudeRangeLength,
                    longitudeRangeLength);
        }
    }

    public void start() {
        GpsLocationManager.getInstance(mContext).start();
    }

    public void stop() {
        GpsLocationManager.getInstance(mContext).stop();
    }
}
