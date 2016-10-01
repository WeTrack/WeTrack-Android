package com.wetrack.map;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.wetrack.R;
import com.wetrack.utils.ConstantValues;

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
    private MapFragment mapFragment;

    private MapController(Context context) {
        mContext = context;

    }

    public void addMapToView(FragmentManager fragmentManager, int viewId) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        mapFragment = new MapFragment();
        fragmentTransaction.add(viewId, mapFragment, "map_fragment");
        fragmentTransaction.commit();
    }
}
