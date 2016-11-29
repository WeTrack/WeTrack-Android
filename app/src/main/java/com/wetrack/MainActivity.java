package com.wetrack;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.wetrack.client.EntityCallback;
import com.wetrack.client.WeTrackClient;
import com.wetrack.client.WeTrackClientWithDbCache;
import com.wetrack.map.GoogleMapFragment;
import com.wetrack.map.MapController;
import com.wetrack.map.MarkerDataFormat;
import com.wetrack.model.Chat;
import com.wetrack.model.ChatMessage;
import com.wetrack.model.Location;
import com.wetrack.model.Message;
import com.wetrack.service.ChatServiceManager;
import com.wetrack.utils.ConstantValues;
import com.wetrack.utils.PreferenceUtils;
import com.wetrack.view.AddOptionListView;
import com.wetrack.view.SidebarView;
import com.wetrack.view.adapter.AddContactAdapter;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class MainActivity extends FragmentActivity {
    private MapController mMapController = null;
    private ImageButton openSidebarButton;
    private SidebarView sidebarView = null;
    private ImageButton addContactButton;
    private AddOptionListView addOptionListView = null;
    private Button chatListButton;
    private ImageButton chatListImageButton;
    private RelativeLayout mainLayout;
    private RelativeLayout buttonLayout;

    private ChatServiceManager mChatServiceManager = null;
    private TextView unreadMessage;
    private int unread;
    private WeTrackClient client = WeTrackClientWithDbCache.singleton();


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
        initUnreadMessage();
    }

    private void initChatServiceManager() {
        mChatServiceManager = new ChatServiceManager(this) {
            @Override
            public void onReceivedMessage(ChatMessage receivedMessage) {
                // TODO Implement this
            }

            // Message will not be sent or ACKed on this activity.
            @Override
            public void onReceivedMessageAck(String ackedMessageId) {
            }
        };
        mChatServiceManager.start();
    }

    public void initMapInView(int viewId) {
        mMapController = MapController.getInstance(this);
        mMapController.addMapToView(getSupportFragmentManager(), viewId);
        mMapController.start();

        mMapController.setmOnInfoWindowClickListener(new GoogleMapFragment.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final MarkerDataFormat markerData) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(markerData.getUsername());
//              builder.setMessage("Last Updated: " + markerData.getInformation());

                if (!markerData.getUsername().equals(PreferenceUtils.getCurrentUsername())) {
                    builder.setPositiveButton("navigation", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mMapController.planNavigation(markerData.getLatLng());
                            dialog.dismiss();
                        }
                    });
                }
                builder.setNegativeButton("history", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        client.getUserLocationsSince(markerData.getUsername(),
                                LocalDateTime.now().minusHours(5), new EntityCallback<List<Location>>() {
                                    @Override
                                    protected void onReceive(List<Location> value) {
                                        super.onReceive(value);
                                        ArrayList<LatLng> newList = new ArrayList<>();
                                        newList.add(new LatLng(value.get(0).getLatitude(), value.get(0).getLongitude()));
                                        for (int i = 1; i < value.size(); i++) {
                                            Location location1 = value.get(i);
                                            Location location2 = value.get(i - 1);
                                            if (Math.abs(location1.getLongitude() - location2.getLongitude()) > 1e-9 ||
                                                    Math.abs(location1.getLatitude() - location2.getLatitude()) > 1e-9) {
                                                newList.add(new LatLng(location1.getLatitude(), location1.getLongitude()));
                                            }
                                        }
                                        mMapController.drawPathOnMap(newList);
                                    }

                                    @Override
                                    protected void onResponse(Response<List<Location>> response) {
                                        super.onResponse(response);
                                    }

                                    @Override
                                    protected void onException(Throwable ex) {
                                        super.onException(ex);
                                    }

                                    @Override
                                    protected void onErrorMessage(Message response) {
                                        super.onErrorMessage(response);
                                    }
                                });
                        dialog.dismiss();
                    }
                });

                builder.create().show();

            }
        });
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
        final int[] imgs = {R.drawable.ic_chat_bubble_black_24dp, R.drawable.ic_person_add_black_24dp};
        final String[] texts = {"New Chat", "Add Friend"};
        addContactButton = (ImageButton) findViewById(R.id.add_contact_button);
        addOptionListView = (AddOptionListView) findViewById(R.id.add_option_listview);
        addOptionListView.setVisibility(View.GONE);
        AddContactAdapter adapter = new AddContactAdapter(this, imgs, texts);

        addOptionListView.setAdapter(adapter);
        addOptionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addOptionListView.close();
                if (texts[position].equals(texts[0])) {
                    Intent intent = new Intent(MainActivity.this, CreateChatActivity.class);
                    startActivityForResult(intent, ConstantValues.CREATE_CHAT_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.fade_out);
                } else if (texts[position].equals(texts[1])) {
                    Intent intent = new Intent(MainActivity.this, AddFriendActivity.class);
                    startActivityForResult(intent, ConstantValues.ADD_FRIEND_REQUEST_CODE);
                    overridePendingTransition(R.anim.slide_in_up, R.anim.fade_out);
                }
            }
        });

        addContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addContactButton.setEnabled(false);
                if (addOptionListView.getVisibility() == View.GONE) {
                    hideAllOtherLayout(addOptionListView);
                    addOptionListView.open();
                } else {
                    addOptionListView.close();
                }
                //  addContactButton.setEnabled(true);
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
        //chatButton = (Button) findViewById(R.id.chat_button);
        //chatButton.setOnClickListener(new View.OnClickListener() {
        buttonLayout = (RelativeLayout) findViewById(R.id.button_layout);
        unreadMessage = (TextView) findViewById(R.id.unread_msg_number);
        buttonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(i);
            }
        });
    }

    private void initUnreadMessage() {
        unread = 0;
        mChatServiceManager = new ChatServiceManager(this) {
            @Override
            public void onReceivedMessage(ChatMessage receivedMessage) {
                if (!receivedMessage.getChatId().equals(PreferenceUtils.getCurrentChatId()))
                    return;
                unread++;
                unreadMessage.setText(String.valueOf(unread));
                unreadMessage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedMessageAck(String ackedMessageId) {

            }
        };
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
                        mMapController.clearAllSymbols();
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mChatServiceManager != null) {
            mChatServiceManager.stop();
            mChatServiceManager = null;
        }
        if (mMapController != null) {
            mMapController.stop();
        }
    }

}
