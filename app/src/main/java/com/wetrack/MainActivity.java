package com.wetrack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.wetrack.contact.ContactView;
import com.wetrack.database.WeTrackDatabaseHelper;
import com.wetrack.login.LoginActivity;
import com.wetrack.map.MapController;
import com.wetrack.map.MarkerDataFormat;
import com.wetrack.model.User;
import com.wetrack.utils.ConstantValues;
import com.wetrack.utils.Tools;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends FragmentActivity implements View.OnClickListener {
    private MapController mMapController;
    private MyHandler mHandler = new MyHandler();

    private RuntimeExceptionDao<User, String> userDao;

    private ImageButton openSidebarButton;
    private ContactView contactView = null;
    private SidebarView sidebarView = null;
    private ImageButton addContactButton;
    private AddOptionListView addOptionListView = null;
    private Button groupListButton;
    private ImageButton dropListImageButton;
    private GroupListView groupListView = null;

    private RelativeLayout mainContain;
    private RelativeLayout mainLayout;

    private Button chat_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userDao = OpenHelperManager.getHelper(this, WeTrackDatabaseHelper.class).getUserDao();

        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        mainContain = (RelativeLayout) findViewById(R.id.main_contain);

        initMapInView(R.id.map_content);
        initSidebar();
        initAddContact();
        initDropList();

        initChatButton();
    }

    public void initMapInView(int viewId) {
        mMapController = MapController.getInstance(this);
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
        sidebarView = new SidebarView(this);
        sidebarView.setLayoutParams(new RelativeLayout.LayoutParams(
                Tools.getScreenW() * 2 / 3,
                ViewGroup.LayoutParams.MATCH_PARENT));

        mainLayout.addView(sidebarView, 1);
        openSidebarButton = (ImageButton) findViewById(R.id.open_sidebar_button);
        openSidebarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sidebarView.getVisibility() == View.GONE) {
                    hideAllLayout(sidebarView);
                    sidebarView.open();
                } else {
                    sidebarView.close();
                }
            }
        });
        sidebarView.setLogoutListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,
                        LoginActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
            }
        });
    }

    private void initAddContact() {
        contactView = new ContactView(this);
        mainLayout.addView(contactView, mainLayout.getChildCount());
        contactView.setVisibility(View.GONE);
        contactView.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        final String[] list = {"NewGroup","AddFriend"};
        addContactButton = (ImageButton) findViewById(R.id.add_contact_button);
        addOptionListView = (AddOptionListView) findViewById(R.id.add_option_listview);
        addOptionListView.setVisibility(View.GONE);

        ArrayAdapter<String> listAdapter = new ArrayAdapter(this,R.layout.add_contact,list);
        addOptionListView.setAdapter(listAdapter);
        addOptionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addOptionListView.setVisibility(View.GONE);
                addOptionListView.close();
                if (list[position].equals(list[0])) {
                    contactView.setMode(ConstantValues.CONTACT_MODE_NEW_GROUP);
                    contactView.show();
                } else if (list[position].equals(list[1])){
                    contactView.setMode(ConstantValues.CONTACT_MODE_ADD_FRIEND);
                    contactView.show();
                }
            }
        });

        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContactButton.setEnabled(false);
                if (addOptionListView.getVisibility() == View.GONE) {
                    hideAllLayout(addOptionListView);
                    addOptionListView.open();
                } else {
                    addOptionListView.close();
                }
                addContactButton.setEnabled(true);
            }
        });
    }

    private void initDropList() {
        groupListButton = (Button) findViewById(R.id.group_list_button);
        dropListImageButton = (ImageButton) findViewById(R.id.drop_list_imagebutton);

        groupListButton.setText(R.string.allFriend);

        groupListView = new GroupListView(this);
        groupListView.setVisibility(View.GONE);
        mainLayout.addView(groupListView, 1);

        View.OnClickListener dropListListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupListView.getVisibility() == View.GONE) {
                    hideAllLayout(groupListView);
                    dropListImageButton.setImageResource(R.drawable.list_drop_up);
                    groupListView.open();
                } else {
                    dropListImageButton.setImageResource(R.drawable.list_drop_down);
                    groupListView.close();
                }
            }
        };

        dropListImageButton.setOnClickListener(dropListListener);
        groupListButton.setOnClickListener(dropListListener);
    }

    private void hideAllLayout(Object object) {
        if (!(object instanceof GroupListView)) {
            if (groupListView.getVisibility() == View.VISIBLE) {
                dropListImageButton.setImageResource(R.drawable.list_drop_down);
                groupListView.close();
            }
        }
        if (!(object instanceof AddOptionListView)) {
            if (addOptionListView.getVisibility() == View.VISIBLE) {
                addOptionListView.setVisibility(View.GONE);
            }
        }
        if (!(object instanceof SidebarView)) {
            if (sidebarView.getVisibility() == View.VISIBLE) {
                sidebarView.close();
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
//        checkGps();

//        mMapController.start();

//        showMarkerDemo();
//        showNavigationDemo();

    }

    @Override
    protected void onPause() {
        super.onPause();
//        mMapController.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (groupListView != null) {
            groupListView.destroy();
        }
        if (contactView != null) {
            contactView.destroy();
        }
        if (sidebarView != null) {
            sidebarView.destroy();
        }
        userDao = null;
        OpenHelperManager.releaseHelper();
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

    private void initChatButton(){
        chat_button = (Button) findViewById(R.id.chat_button);
        chat_button.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chat_button:
                Intent i = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }
}
