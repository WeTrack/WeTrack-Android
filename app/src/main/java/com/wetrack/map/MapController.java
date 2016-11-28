package com.wetrack.map;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.wetrack.map.GoogleNavigation.GoogleNavigationFormat;
import com.wetrack.map.GoogleNavigation.GoogleNavigationManager;
import com.wetrack.map.GoogleNavigation.GoogleNavigationResultListener;
import com.wetrack.service.LocationServiceManager;
import com.wetrack.utils.ConstantValues;
import com.wetrack.utils.MathUtils;
import com.wetrack.utils.PreferenceUtils;
import com.wetrack.utils.Tags;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    private LocationServiceManager mLocationServiceManager = null;

    private MapController(Context context) {
        mContext = context;

        mGoogleNavigationManager = GoogleNavigationManager.getInstance(mContext);
        mMapHandler = new MapHandler();
        mGpsLocationManager = GpsLocationManager.getInstance(mContext);

        mGoogleNavigationManager.setmGoogleNavigationResultListener(new MyGoogleNavigationResultListener());

        mLocationServiceManager = new LocationServiceManager(mContext) {
            @Override
            public void onReceivedLocation(com.wetrack.model.Location location) {
                Log.d(Tags.Location.SERVICE, "MapController receives location: (" + location.getUsername() + ", "
                + location.getLatitude() + ", " + location.getLongitude() + ", "
                        + location.getTime());

                MarkerDataFormat marker = new MarkerDataFormat(
                        location.getUsername(),
                        new LatLng(location.getLatitude(), location.getLongitude()), location.getTime().toString());
                addMarker(marker);
            }
        };
//        usernameAndMarker.set(new HashMap<String, MarkerDataFormat>());
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

                if (mLocationServiceManager != null) {
                    com.wetrack.model.Location loc = new com.wetrack.model.Location(
                            PreferenceUtils.getCurrentUsername(),
                            location.getLongitude(), location.getLatitude(), LocalDateTime.now());
                    List<com.wetrack.model.Location>locationList =
                            new ArrayList<>(Arrays.asList(loc));
                    mLocationServiceManager.sendLocation(locationList);
                }
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
        mLocationServiceManager.start();
    }

    public void stop() {
        mGpsLocationManager.stop();
        mGoogleNavigationManager.stop();
        mMapController = null;
        mLocationServiceManager.stop();
        mLocationServiceManager = null;
    }

    //below three are for markers
//    private AtomicReference<Map<String, MarkerDataFormat>> usernameAndMarker = new AtomicReference<>();
    public void clearAllSymbols() {
        googleMapFragment.clearAllSymbols();
    }

    public void addMarker(MarkerDataFormat markerData) {
        googleMapFragment.addMarker(markerData.getUsername(), markerData.getLatLng(), markerData.getInformation());
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

    public void planNavigation(LatLng toPosition) {
        LatLng myLatLng = getMyLocation();
        if (myLatLng != null) {
            planNavigation(myLatLng, toPosition);
        }
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
