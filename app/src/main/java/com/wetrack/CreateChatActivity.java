package com.wetrack;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.wetrack.client.CreatedMessageCallback;
import com.wetrack.client.EntityCallback;
import com.wetrack.client.WeTrackClient;
import com.wetrack.model.Chat;
import com.wetrack.model.User;
import com.wetrack.utils.PreferenceUtils;
import com.wetrack.view.CreateChatItemView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CreateChatActivity extends AppCompatActivity {

    private WeTrackClient client = WeTrackClient.singleton();

    private String username;
    private String token;

    private ImageButton backButton;
    private Button createButton;
    private TextView groupName;
    private LinearLayout friendListLayout;

    private ScrollView scrollview;
    private InputMethodManager imeManager;
    private ArrayList<String> allFriendNamesToCreateChat = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        username = PreferenceUtils.getCurrentUsername();
        token = PreferenceUtils.getCurrentToken();

        setResult(RESULT_CANCELED);

        backButton = (ImageButton) findViewById(R.id.chat_create_back_btn);
        createButton = (Button) findViewById(R.id.chat_create_btn);
        groupName = (TextView) findViewById(R.id.group_name);

        scrollview = (ScrollView) findViewById(R.id.create_group_view);

        friendListLayout = (LinearLayout) findViewById(R.id.friend_list);

        imeManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
                if (groupName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(CreateChatActivity.this, "Chat name cannot be empty.", Toast.LENGTH_SHORT).show();
                    createButton.setEnabled(true);
                    return;
                }
                final Chat chat = new Chat();
                chat.setName(groupName.getText().toString());
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

        scrollview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
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
            } else {
                createButton.setEnabled(true);
            }
            hideKeyboard();
        }
    }
    private void hideKeyboard(){
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                imeManager.hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
