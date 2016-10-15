package com.wetrack;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;
import com.wetrack.map.GoogleMapFragment;
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

        mMapController = MapController.getInstance(this);

        initMapInView(R.id.map_content);
    }

    public void initMapInView(int viewId) {
        mMapController.addMapToView(getSupportFragmentManager(), viewId);

//        mMapController.setmOnInfoWindowClickListener(new MyOnInfoWindowClickListener());
    }

//    private class MyOnInfoWindowClickListener implements GoogleMapFragment.OnInfoWindowClickListener {
//        @Override
//        public void onInfoWindowClick(MarkerDataFormat markerData) {
//            Log.d(ConstantValues.markerDebug, "marker callback: " + markerData.toString());
//        }
//    }

    private void showMarkerDemo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);

                    Message message = new Message();
                    message.what = ConstantValues.MARKER_DEMO_TAG;
                    mHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void showNavigationDemo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);

                    Message message = new Message();
                    message.what = ConstantValues.NAVIGATION_DEMO_TAG;
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
                case ConstantValues.MARKER_DEMO_TAG:

                    Log.d(ConstantValues.debugTab, "marker demo started");

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
                    mMapController.clearMarkers();
                    mMapController.addMarkers(markers, true);
                    Log.d(ConstantValues.debugTab, "marker demo finished");

                    break;

                case ConstantValues.NAVIGATION_DEMO_TAG:
                    mMapController.planNavigation(new LatLng(22.3353712, 114.2636767), new LatLng(22.3363712, 114.2736767));

                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapController.start();

//        showMarkerDemo();
        showNavigationDemo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapController.stop();
    }


}
