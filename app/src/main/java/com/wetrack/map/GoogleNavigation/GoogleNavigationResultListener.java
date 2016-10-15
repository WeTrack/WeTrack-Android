package com.wetrack.map.GoogleNavigation;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by moziliang on 16/10/15.
 */
public interface GoogleNavigationResultListener {
    public void onReceiveResult(ArrayList<LatLng>resultPath);
}
