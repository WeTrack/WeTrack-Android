package com.wetrack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.wetrack.client.EntityCallback;
import com.wetrack.client.WeTrackClient;
import com.wetrack.database.UserChat;
import com.wetrack.database.WeTrackDatabaseHelper;
import com.wetrack.model.Chat;
import com.wetrack.model.User;
import com.wetrack.utils.PreferenceUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {
    private WeTrackDatabaseHelper helper;
    private LinearLayout chatListLinearLayout = null;
    private ImageButton chatListBackButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        setResult(RESULT_CANCELED);

        chatListLinearLayout = (LinearLayout) findViewById(R.id.chat_list);
        chatListBackButton = (ImageButton) findViewById(R.id.chat_list_back_button);
        chatListBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.fade_in, R.anim.slide_out_up);
                ChatListActivity.this.finish();
            }
        });

        helper = OpenHelperManager.getHelper(this, WeTrackDatabaseHelper.class);

        dbReload();
        networkReload();
    }

    private void dbReload() {
        final String username = PreferenceUtils.getStringValue(PreferenceUtils.KEY_USERNAME);
        User userInDB = helper.getUserDao().queryForId(username);
        if (userInDB == null) // User does not exist in the database. Wait for network response.
            return;
        Collection<Chat> chats = helper.getUserChatDao().getUserChatList(userInDB);
        reloadChatList(chats);
    }

    private void networkReload() {
        final String username = PreferenceUtils.getStringValue(PreferenceUtils.KEY_USERNAME);
        String token = PreferenceUtils.getStringValue(PreferenceUtils.KEY_TOKEN);

        WeTrackClient.singleton().getUserChatList(username, token, new EntityCallback<List<Chat>>() {
            @Override
            protected void onReceive(List<Chat> receivedChats) {
                User userInDB = helper.getUserDao().queryForId(username);
                if (userInDB != null) {
                    for (Chat chat : receivedChats) {
                        helper.getChatDao().createOrUpdate(chat);
                        UserChat userChat = new UserChat(userInDB, chat);
                        if (!helper.getUserChatDao().userChatExists(userChat))
                            helper.getUserChatDao().create(userChat);
                    }
                }
                reloadChatList(receivedChats);
            }
        });
    }

    private void reloadChatList(Collection<Chat> chats) {
        chatListLinearLayout.removeAllViews();
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        // Show chat items in linearLayout
        for (Chat chat : chats) {
            RelativeLayout chatItemLayout = (RelativeLayout) layoutInflater.inflate(R.layout.chat_list_item, null);

            TextView chatNameView = (TextView) chatItemLayout.findViewById(R.id.chat_name);
            chatNameView.setText(chat.getName());

            TextView chatMemberView = (TextView) chatItemLayout.findViewById(R.id.chat_member);
            chatMemberView.setText(StringUtils.join(chat.getMemberNames(), ", "));

            chatItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO put the information into intent, then get it in 'onActivityResult' in MainActivity
                    Intent intent = new Intent();

                    setResult(RESULT_OK, intent);

                    overridePendingTransition(R.anim.fade_in, R.anim.slide_out_up);
                    ChatListActivity.this.finish();
                }
            });

            chatListLinearLayout.addView(chatItemLayout);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (helper != null) {
            helper = null;
            OpenHelperManager.releaseHelper();
        }
    }
}
