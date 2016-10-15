package com.wetrack.map.GoogleNavigation;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by moziliang on 16/10/15.
 */
public class GoogleNavigationFormat {
    // required
    public LatLng origin;
    public LatLng destination;
    public String travelMode;

    // optional
    public String transitOptions;
    public String drivingOptions;
    public String unitSystem;
    public LatLng[]waypoints;
    public boolean optimizeWaypoints;
    public boolean provideRouteAlternatives;
    public boolean avoidHighways;
    public boolean avoidTolls;
    public boolean region;

}
