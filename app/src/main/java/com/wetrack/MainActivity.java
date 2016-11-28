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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.model.LatLng;
import com.wetrack.client.EntityCallback;
import com.wetrack.client.WeTrackClientWithDbCache;
import com.wetrack.model.Chat;
import com.wetrack.model.ChatMessage;
import com.wetrack.utils.PreferenceUtils;
import com.wetrack.view.AddOptionListView;
import com.wetrack.view.SidebarView;
import com.wetrack.service.ChatServiceManager;
import com.wetrack.map.MapController;
import com.wetrack.map.MarkerDataFormat;
import com.wetrack.utils.ConstantValues;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends FragmentActivity {
    private MyHandler mHandler = new MyHandler();
    private MapController mMapController;
    private ImageButton openSidebarButton;
    private SidebarView sidebarView = null;
    private ImageButton addContactButton;
    private AddOptionListView addOptionListView = null;
    private Button chatListButton;
    private ImageButton chatListImageButton;
    private RelativeLayout mainLayout;
    private Button chatButton;

    private ChatServiceManager mChatServiceManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initChatServiceManager();

        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);

        initMapInView(R.id.map_content);
        initSidebar();
        initAddContact();
        initChatList();
        initChatButton();
    }

    private void initChatServiceManager() {
        mChatServiceManager = new ChatServiceManager(this) {
            @Override
            public void onReceivedMessage(ChatMessage receivedMessage) {
                // TODO Implement this
            }

            // Message will not be sent or ACKed on this activity.
            @Override
            public void onReceivedMessageAck(String ackedMessageId) {}
        };
        mChatServiceManager.start();
    }

    public void initMapInView(int viewId) {
        mMapController = MapController.getInstance(this);
        mMapController.addMapToView(getSupportFragmentManager(), viewId);
    }

    private void initSidebar() {
        sidebarView = new SidebarView(this);

        mainLayout.addView(sidebarView, 1);
        openSidebarButton = (ImageButton) findViewById(R.id.open_sidebar_button);
        openSidebarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sidebarView.getSidebarState() == SidebarView.OPEN_STATE) {
                    sidebarView.close();
                } else {
                    hideAllOtherLayout(sidebarView);
                    sidebarView.open();
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
        final String[] list = { "New Chat", "Add Friend" };
        addContactButton = (ImageButton) findViewById(R.id.add_contact_button);
        addOptionListView = (AddOptionListView) findViewById(R.id.add_option_listview);
        addOptionListView.setVisibility(View.GONE);

        ArrayAdapter<String> listAdapter = new ArrayAdapter(this, R.layout.add_contact, list);
        addOptionListView.setAdapter(listAdapter);
        addOptionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addOptionListView.close();
                if (list[position].equals(list[0])) {
                    Intent intent = new Intent(MainActivity.this, CreateChatActivity.class);
                    startActivityForResult(intent, ConstantValues.CREATE_CHAT_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.fade_out);
                } else if (list[position].equals(list[1])) {
                    Intent intent = new Intent(MainActivity.this, AddFriendActivity.class);
                    startActivityForResult(intent, ConstantValues.ADD_FRIEND_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.fade_out);
                }
            }
        });

        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContactButton.setEnabled(false);
                if (addOptionListView.getVisibility() == View.GONE) {
                    hideAllOtherLayout(addOptionListView);
                    addOptionListView.open();
                } else {
                    addOptionListView.close();
                }
                addContactButton.setEnabled(true);
            }
        });
    }

    private void initChatList() {
        chatListButton = (Button) findViewById(R.id.chat_list_button);
        chatListImageButton = (ImageButton) findViewById(R.id.chat_list_imagebutton);
        WeTrackClientWithDbCache.singleton().getChatInfo(
                PreferenceUtils.getCurrentChatId(), PreferenceUtils.getCurrentToken(),
                new EntityCallback<Chat>() {
                    @Override
                    protected void onReceive(Chat chat) {
                        chatListButton.setText(chat.getName());
                    }
                }
        );

        View.OnClickListener chatListListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideAllOtherLayout(null);
                Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
                startActivityForResult(intent, ConstantValues.CHAT_LIST_REQUEST_CODE);
                overridePendingTransition(R.anim.slide_in_up, R.anim.fade_out);
            }
        };

        chatListImageButton.setOnClickListener(chatListListener);
        chatListButton.setOnClickListener(chatListListener);
    }

    private void initChatButton() {
        chatButton = (Button) findViewById(R.id.chat_button);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(i);
            }
        });
    }

    private void hideAllOtherLayout(Object object) {
        if (!(object instanceof AddOptionListView)) {
            if (addOptionListView.getVisibility() == View.VISIBLE) {
                addOptionListView.close();
            }
        }
        if (!(object instanceof SidebarView)) {
            if (sidebarView.getSidebarState() == SidebarView.OPEN_STATE) {
                sidebarView.close();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_up);
        switch (requestCode) {
            case ConstantValues.CHAT_LIST_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_CANCELED:
                        Log.d(ConstantValues.debugTab, "chat list canceled");
                        break;
                    case RESULT_OK:
                        Log.d(ConstantValues.debugTab, "chat list succeed");
                        String chatName = data.getStringExtra(ChatListActivity.KEY_CHAT_NAME);
                        chatListButton.setText(chatName);
                        break;
                }
                break;
            case ConstantValues.CREATE_CHAT_REQUEST_CODE:
                switch (resultCode) {
                    case RESULT_CANCELED:
                        Log.d(ConstantValues.debugTab, "create group cenceled");
                        break;
                    case RESULT_OK:
                        Log.d(ConstantValues.debugTab, "create group succeed");
                        //TODO get information from 'data' here
                        break;
                }
                break;
            case ConstantValues.ADD_FRIEND_REQUEST_CODE:
                switch (requestCode) {
                    case RESULT_CANCELED:
                        Log.d(ConstantValues.debugTab, "add friend cenceled");
                        break;
                    case RESULT_OK:
                        Log.d(ConstantValues.debugTab, "add friend success");
                        //TODO get information from 'data' here
                        break;
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
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

        mMapController.start();

//        showMarkerDemo();
//        showNavigationDemo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapController.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChatServiceManager != null) {
            mChatServiceManager.stop();
            mChatServiceManager = null;
        }
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
