package com.wetrack.map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.wetrack.client.EntityCallback;
import com.wetrack.client.WeTrackClient;
import com.wetrack.client.WeTrackClientWithDbCache;
import com.wetrack.map.GoogleNavigation.GoogleNavigationFormat;
import com.wetrack.map.GoogleNavigation.GoogleNavigationManager;
import com.wetrack.map.GoogleNavigation.GoogleNavigationResultListener;
import com.wetrack.model.Message;
import com.wetrack.service.LocationServiceManager;
import com.wetrack.utils.MathUtils;
import com.wetrack.utils.PreferenceUtils;
import com.wetrack.utils.Tags;
import com.wetrack.utils.Tools;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Response;

public class MapController {
    private static MapController mMapController = null;

    public static MapController getInstance() {
        if (mMapController == null) {
            mMapController = new MapController();
        }
        return mMapController;
    }
    private MapController() {}

    private Context mContext;
    private GoogleMapFragment googleMapFragment;
    private GoogleNavigationManager mGoogleNavigationManager;
    private GpsLocationManager mGpsLocationManager;
    private LocationServiceManager mLocationServiceManager = null;
    private WeTrackClient client = WeTrackClientWithDbCache.singleton();

    public void createFragmentInContainer(FragmentManager fragmentManager, int containerViewId) {
        mContext = Tools.getMainContext();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        googleMapFragment = GoogleMapFragment.newInstance(mContext);
        fragmentTransaction.add(containerViewId, googleMapFragment, "map_fragment");
        fragmentTransaction.commit();

        mGoogleNavigationManager = GoogleNavigationManager.getInstance(mContext);
        mGoogleNavigationManager.setmGoogleNavigationResultListener(new MyGoogleNavigationResultListener());

        mLocationServiceManager = new LocationServiceManager(mContext) {
            @Override
            public void onReceivedLocation(com.wetrack.model.Location location) {
                Log.d(Tags.Location.SERVICE, "MapController receives location: (" + location.getUsername() + ", "
                        + location.getLatitude() + ", " + location.getLongitude() + ", "
                        + location.getTime());

                addMarker(location.getUsername(),
                        new LatLng(location.getLatitude(), location.getLongitude()));
            }
        };

        mGpsLocationManager = GpsLocationManager.getInstance(mContext);
        mGpsLocationManager.setmGpsLocationListener(new MyGpsLocationListener());

        googleMapFragment.setMyOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                createMarkerDialog(marker);
                return true;
            }
        });
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

    private Location myCurrentLocation = null;
    public LatLng getMyCurrentLocation() {
        if (myCurrentLocation != null) {
            return new LatLng(myCurrentLocation.getLatitude(), myCurrentLocation.getLongitude());
        }
        return null;
    }

    private class MyGpsLocationListener implements GpsLocationManager.GpsLocationListener {
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
    }

    private void createMarkerDialog(final Marker marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                new ContextThemeWrapper(mContext, android.R.style.Theme_DeviceDefault_Light_Dialog));
        builder.setTitle(marker.getTitle());
        builder.setMessage(marker.getSnippet());

        if (!marker.getTitle().equals(PreferenceUtils.getCurrentUsername())) {
            builder.setPositiveButton("navigation", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    planNavigation(marker.getPosition());
                    dialog.dismiss();
                }
            });
        }
        builder.setNegativeButton("history", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                client.getUserLocationsSince(marker.getTitle(),
                        LocalDateTime.now().minusHours(5), new EntityCallback<List<com.wetrack.model.Location>>() {
                            @Override
                            protected void onReceive(List<com.wetrack.model.Location> value) {
                                super.onReceive(value);
                                ArrayList<LatLng> newList = new ArrayList<>();
                                newList.add(new LatLng(value.get(0).getLatitude(), value.get(0).getLongitude()));
                                for (int i = 1; i < value.size(); i++) {
                                    com.wetrack.model.Location location1 = value.get(i);
                                    com.wetrack.model.Location location2 = value.get(i - 1);
                                    if (Math.abs(location1.getLongitude() - location2.getLongitude()) > 1e-9 ||
                                            Math.abs(location1.getLatitude() - location2.getLatitude()) > 1e-9) {
                                        newList.add(new LatLng(location1.getLatitude(), location1.getLongitude()));
                                    }
                                }
                                drawPathOnMap(newList);
                            }

                            @Override
                            protected void onResponse(Response<List<com.wetrack.model.Location>> response) {
                                super.onResponse(response);
                            }

                            @Override
                            protected void onException(Throwable ex) {
                                super.onException(ex);
                                Toast.makeText(mContext, "Fail in getting history path",
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            protected void onErrorMessage(Message response) {
                                super.onErrorMessage(response);
                                Toast.makeText(mContext, "Fail in getting history path",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    public void clearAllSymbols() {
        googleMapFragment.clearAllSymbols();
    }

    public void addMarker(String username, LatLng latLng) {
        googleMapFragment.addMarker(username, latLng);
    }

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
        LatLng myLatLng = getMyCurrentLocation();
        if (myLatLng != null) {
            planNavigation(myLatLng, toPosition);
        }
    }

    private class MyGoogleNavigationResultListener implements GoogleNavigationResultListener {
        @Override
        public void onReceiveResult(ArrayList<LatLng> resultPath) {
            drawPathOnMap(resultPath);
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
