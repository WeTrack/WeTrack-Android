package com.wetrack;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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

    private ImageButton openSidebarButton;
    private RelativeLayout sidebarLayout;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mMapController = MapController.getInstance(this);

        initMapInView(R.id.map_content);

        initSidebar();
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

    private void initSidebar() {
        openSidebarButton = (ImageButton) findViewById(R.id.open_sidebar_button);
        openSidebarButton.setOnClickListener(new MySidebarOpenOnClickListener());

        sidebarLayout = (RelativeLayout) findViewById(R.id.sidebar_layout);
    }

    private class MySidebarOpenOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int width = sidebarLayout.getWidth();
            if (sidebarLayout.getVisibility() == View.INVISIBLE) {
                sidebarLayout.setVisibility(View.VISIBLE);
                Animation am = new TranslateAnimation(-width * 1f, 0f, 0f, 0f);
                am.setDuration(1000);
//                am.setRepeatCount(0);
                am.setInterpolator(new AccelerateInterpolator());
                sidebarLayout.startAnimation(am);
            } else {
                Animation am = new TranslateAnimation(0f, -width * 1f, 0f, 0f);
                am.setDuration(1000);
//                am.setRepeatCount(0);
                am.setInterpolator(new AccelerateInterpolator());
                sidebarLayout.startAnimation(am);
                am.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        sidebarLayout.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        }
    }

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

                case ConstantValues.CHECK_GPS:
                    checkGps();
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

//        requestForLocationService();
        checkGps();

        mMapController.start();

//        showMarkerDemo();
        showNavigationDemo();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapController.stop();
    }

//    public boolean requestForLocationService() {
//        int locationCheck1 = ContextCompat.checkSelfPermission(this,
//                android.Manifest.permission.ACCESS_COARSE_LOCATION);
//        int locationCheck2 = ContextCompat.checkSelfPermission(this,
//                android.Manifest.permission.ACCESS_FINE_LOCATION);
//        if (locationCheck1 != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    ConstantValues.PERMISSION_ACCESS_FINE_LOCATION);
//        }
//        return false;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }

    private void checkGps() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(ConstantValues.permission, "not getting gps");

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // Setting Icon to Dialog
            //alertDialog.setIcon(R.drawable.delete);

            // On pressing Settings button
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        } else {
            Log.d(ConstantValues.permission, "got gps");
        }
    }

}
