package com.wetrack.map;

import android.content.Context;
import android.location.Location;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.phenotype.Flag;
import com.wetrack.map.GoogleNavigation.GoogleNavigationFormat;
import com.wetrack.map.GoogleNavigation.GoogleNavigationManager;
import com.wetrack.map.GoogleNavigation.GoogleNavigationResultListener;
import com.wetrack.utils.ConstantValues;
import com.wetrack.utils.MathUtils;

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
    private MapHandler mMapHandler;
    private GoogleMapFragment googleMapFragment;
    private GoogleNavigationManager mGoogleNavigationManager;
    private GpsLocationManager mGpsLocationManager;


    private MapController(Context context) {
        mContext = context;
        mGoogleNavigationManager = GoogleNavigationManager.getInstance(mContext);
        mMapHandler = new MapHandler();
        mGpsLocationManager = GpsLocationManager.getInstance(mContext);

        mGoogleNavigationManager.setmGoogleNavigationResultListener(new MyGoogleNavigationResultListener());
    }

    public void addMapToView(FragmentManager fragmentManager, int viewId) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        googleMapFragment = GoogleMapFragment.newInstance(mContext);
        fragmentTransaction.add(viewId, googleMapFragment, "map_fragment");
        fragmentTransaction.commit();

        mMapHandler.setmMapController(mMapController);

        mGpsLocationManager.setmGpsLocationListener(new GpsLocationManager.GpsLocationListener() {
            @Override
            public void onGpsLocationReceived(Location location) {
                myCurrentLocation = location;
            }
        });
    }

    //below four are for localization service
    private Location myCurrentLocation = null;

    public LatLng getMyLocation() {
        if (myCurrentLocation != null) {
            return new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude());
        }
        return null;
    }

    public void start() {
        mGpsLocationManager.start();
    }

    public void stop() {
        mGpsLocationManager.stop();
        mGoogleNavigationManager.stop();
        mMapController = null;
    }

    //below three are for markers
    public void clearMarkers() {
        googleMapFragment.clearMarkers();
    }

    public void addMarkers(ArrayList<MarkerDataFormat> markerData, boolean resetCamera) {
        for (MarkerDataFormat aMarkerData : markerData) {
            googleMapFragment.addMarker(aMarkerData.title, aMarkerData.latLng, aMarkerData.information);
        }

        if (resetCamera) {

            ArrayList<LatLng> allLatLng = new ArrayList<>();
            for (MarkerDataFormat aMarkerData : markerData) {
                allLatLng.add(aMarkerData.getLatLng());
            }
            double[]result = MathUtils.getCenterAndLengthRange(allLatLng);
            double centerLatitude = result[0];
            double centerLongitude = result[1];
            double latitudeRangeLength = result[2];
            double longitudeRangeLength = result[3];

            googleMapFragment.setCameraLocation(
                    new LatLng(centerLatitude, centerLongitude),
                    latitudeRangeLength,
                    longitudeRangeLength);
        }
    }

    public void setmOnInfoWindowClickListener(GoogleMapFragment.OnInfoWindowClickListener mOnInfoWindowClickListener) {
        googleMapFragment.setmOnInfoWindowClickListener(mOnInfoWindowClickListener);
    }

    //below three are for navigation
    /**
     * show navigation path
     * **/
    public void planNavigation(LatLng fromPosition, LatLng toPosition) {
        GoogleNavigationFormat googleNavigationData = new GoogleNavigationFormat();
        googleNavigationData.origin = fromPosition;
        googleNavigationData.destination = toPosition;

        mGoogleNavigationManager.getResultFromGoogle(googleNavigationData);
    }

    /**
     * send result path to handler, back to the UI thread to draw path
     * */
    private class MyGoogleNavigationResultListener implements GoogleNavigationResultListener {
        @Override
        public void onReceiveResult(ArrayList<LatLng> resultPath) {
            mMapHandler.sendMapMessage(ConstantValues.NAVIGATION_RESULT_TAG, (Object) resultPath);
        }
    }

    public void drawPathOnMap(ArrayList<LatLng> positions) {
        googleMapFragment.drawPathOnMap(positions);

        double[]result = MathUtils.getCenterAndLengthRange(positions);
        double centerLatitude = result[0];
        double centerLongitude = result[1];
        double latitudeRangeLength = result[2];
        double longitudeRangeLength = result[3];

        googleMapFragment.setCameraLocation(
                new LatLng(centerLatitude, centerLongitude),
                latitudeRangeLength,
                longitudeRangeLength);
    }
}
