package com.wetrack.map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.android.gms.maps.model.LatLng;
import com.wetrack.utils.ConstantValues;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by moziliang on 16/10/15.
 */
public class MapHandler extends Handler {
    public MapHandler() {
    }

    private static MapController mMapController;
    public void setmMapController(MapController mMapController) {
        this.mMapController = mMapController;
    }

    public void sendMapMessage(int messageTag, Object parcelableObject) {
        Message message = new Message();
        message.what = messageTag;
        Bundle bundle = new Bundle();
        bundle.putSerializable("serializableObject", (Serializable) parcelableObject);
        message.setData(bundle);
        sendMessage(message);
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case ConstantValues.NAVIGATION_RESULT_TAG:

                Bundle bundle = msg.getData();
                ArrayList<LatLng> resultPath = (ArrayList<LatLng>)bundle.getSerializable("serializableObject");

//                for (LatLng position : resultPath) {
//                    Log.d(ConstantValues.debugTab, "(" + position.latitude + ", " + position.longitude + ")");
//                }

                mMapController.drawPathOnMap(resultPath);


                break;
            default:
                break;
        }
        super.handleMessage(msg);
    }
}
