package com.wetrack;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.wetrack.map.MapController;
import com.wetrack.map.MarkerDataFormat;
import com.wetrack.utils.ConstantValues;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends FragmentActivity {

    private MapController mMapController;
    private MyHandler mHandler = new MyHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //all operations for MapController should be in Main Thread
        mMapController = MapController.getInstance(this);

        initMapInView(R.id.map_content);
    }

    public void initMapInView(int viewId) {
        mMapController.addMapToView(getSupportFragmentManager(), viewId);
    }

    private void showMapDemo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);

                    Message message = new Message();
                    message.what = ConstantValues.DEMO_TAG;
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ConstantValues.DEMO_TAG:

                    try {
                        Log.d(ConstantValues.debugTab, "demo started");
                        Log.d(ConstantValues.debugTab, "demo goes on");

                        ArrayList<MarkerDataFormat> markers =
                                new ArrayList<>(Arrays.asList(
                                        new MarkerDataFormat("Title1", new LatLng(30, 30), "info1"),
                                        new MarkerDataFormat("Title2", new LatLng(35, 30), "info2"),
                                        new MarkerDataFormat("Title3", new LatLng(40, 30), "info3"),
                                        new MarkerDataFormat("Title4", new LatLng(30, 35), "info4"),
                                        new MarkerDataFormat("Title5", new LatLng(35, 35), "info5"),
                                        new MarkerDataFormat("Title6", new LatLng(40, 35), "info6"),
                                        new MarkerDataFormat("Title7", new LatLng(30, 40), "info7"),
                                        new MarkerDataFormat("Title8", new LatLng(35, 40), "info8"),
                                        new MarkerDataFormat("Title9", new LatLng(40, 40), "info9")
                                ));
                        mMapController.resetMarkers(markers, true);
                        Log.d(ConstantValues.debugTab, "demo finished");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mMapController.start();

        showMapDemo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapController.stop();
    }


}
