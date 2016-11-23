package com.wetrack;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wetrack.client.CreatedMessageCallback;
import com.wetrack.client.EntityCallback;
import com.wetrack.client.WeTrackClient;
import com.wetrack.client.WeTrackClientWithDbCache;
import com.wetrack.model.Chat;
import com.wetrack.model.User;
import com.wetrack.utils.ConstantValues;
import com.wetrack.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateChatActivity extends AppCompatActivity {

    private WeTrackClient client = WeTrackClientWithDbCache.singleton();

    private String username;
    private String token;

    private ImageButton backButton;
    private Button createButton;
    private LinearLayout friendListLayout;

    private ArrayList<String> allFriendNamesToCreateChat = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        username = PreferenceUtils.getStringValue(PreferenceUtils.KEY_USERNAME);
        token = PreferenceUtils.getStringValue(PreferenceUtils.KEY_TOKEN);

        setResult(RESULT_CANCELED);
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out_up);

        backButton = (ImageButton) findViewById(R.id.chat_create_back_btn);
        createButton = (Button) findViewById(R.id.chat_create_btn);
        friendListLayout = (LinearLayout) findViewById(R.id.friend_list);


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateChatActivity.this.finish();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createButton.setEnabled(false);
                if (allFriendNamesToCreateChat.isEmpty()) {
                    Toast.makeText(CreateChatActivity.this, "Cannot create a chat without any member.", Toast.LENGTH_SHORT).show();
                    createButton.setEnabled(true);
                    return;
                }
                final Chat chat = new Chat();
                chat.setName("A chat"); // TODO Set chat name by user
                chat.setMemberNames(Collections.unmodifiableList(allFriendNamesToCreateChat));
                client.createChat(token, chat, new CreatedMessageCallback() {
                    @Override
                    protected void onSuccess(String newEntityId, String message) {
                        chat.setChatId(newEntityId);
                        Toast.makeText(CreateChatActivity.this, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        CreateChatActivity.this.finish();
                    }

                    @Override
                    protected void onFail(String message, int failedStatusCode) {
                        createButton.setEnabled(true);
                        Toast.makeText(CreateChatActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void onError(Throwable ex) {
                        createButton.setEnabled(true);
                        Toast.makeText(CreateChatActivity.this, "Exception occurred during the connection: " + ex.getClass().getName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        client.getUserFriendList(username, token, new EntityCallback<List<User>>() {
            @Override
            protected void onReceive(List<User> friends) {
                friendListLayout.removeAllViews();
                for (User friend : friends) {
                    CreateChatItemView createChatItemView = new CreateChatItemView(CreateChatActivity.this);
                    createChatItemView.setUser(friend);
                    friendListLayout.addView(createChatItemView);
                    createChatItemView.setOnMyCheckBoxChangeListener(new ItemCheckedChangedListener());
                }
            }
        });
    }

    private class ItemCheckedChangedListener implements CreateChatItemView.OnMyCheckBoxChangedListener {
        @Override
        public void addFriendToGroup(String friendName) {
            if (!allFriendNamesToCreateChat.contains(friendName)) {
                allFriendNamesToCreateChat.add(friendName);
            }
            refreshCreateButton();
        }

        @Override
        public void removeFriendFromGroup(String friendName) {
            if (allFriendNamesToCreateChat.contains(friendName)) {
                allFriendNamesToCreateChat.remove(friendName);
            }
            refreshCreateButton();
        }

        private void refreshCreateButton() {
            if (allFriendNamesToCreateChat.isEmpty()) {
                createButton.setEnabled(false);
                createButton.setTextColor(Color.LTGRAY);
            } else {
                createButton.setEnabled(true);
                createButton.setTextColor(Color.BLACK);
            }
            Log.d(ConstantValues.debugTab, "create group size: " + allFriendNamesToCreateChat.size());
        }
    }
}
