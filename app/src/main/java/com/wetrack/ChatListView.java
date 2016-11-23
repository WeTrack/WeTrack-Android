package com.wetrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.wetrack.client.EntityCallback;
import com.wetrack.client.WeTrackClient;
import com.wetrack.database.UserChat;
import com.wetrack.database.WeTrackDatabaseHelper;
import com.wetrack.model.Chat;
import com.wetrack.model.User;
import com.wetrack.utils.ConstantValues;
import com.wetrack.utils.PreferenceUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;

public class ChatListView extends RelativeLayout{
    private ChatListUpdateReceiver updateReceiver = null;

    private WeTrackDatabaseHelper helper;

    private LinearLayout chatListLayout;

    public ChatListView(Context context) {
        super(context);
        init();
    }

    public ChatListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChatListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        helper = OpenHelperManager.getHelper(getContext(), WeTrackDatabaseHelper.class);

        initBroadcastReceiver();
        // Set params for this view
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.menu_bar);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        setLayoutParams(layoutParams);
        setBackgroundColor(Color.WHITE);

        // Use linear layout to store all chat items
        ScrollView scrollView = new ScrollView(getContext());
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        chatListLayout = new LinearLayout(getContext());
        chatListLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        chatListLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(chatListLayout);
        addView(scrollView);

        dbReload();
        networkReload();
    }

    private void dbReload() {
        final String username = PreferenceUtils.getStringValue(getContext(), PreferenceUtils.KEY_USERNAME);
        User userInDB = helper.getUserDao().queryForId(username);
        if (userInDB == null) // User does not exist in the database. Wait for network response.
            return;
        Collection<Chat> chats = helper.getUserChatDao().getUserChatList(userInDB);
        reloadChatList(chats);
    }

    private void networkReload() {
        final String username = PreferenceUtils.getStringValue(getContext(), PreferenceUtils.KEY_USERNAME);
        String token = PreferenceUtils.getStringValue(getContext(), PreferenceUtils.KEY_TOKEN);

        WeTrackClient.getInstance().getUserChatList(username, token, new EntityCallback<List<Chat>>() {
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
        chatListLayout.removeAllViews();
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());

       // Show chat items in linearLayout
       for (Chat chat : chats) {
           RelativeLayout chatItemLayout = (RelativeLayout) layoutInflater.inflate(R.layout.chat_list_item, null);

           TextView chatNameView = (TextView) chatItemLayout.findViewById(R.id.chat_name);
           chatNameView.setText(chat.getName());

           TextView chatMemberView = (TextView) chatItemLayout.findViewById(R.id.chat_member);
           chatMemberView.setText(StringUtils.join(chat.getMemberNames(), ", "));

           chatItemLayout.setOnClickListener(new OnClickListener() {
               @Override
               public void onClick(View v) {
                    // TODO Switch to this chat
               }
           });

           chatListLayout.addView(chatItemLayout);
       }
    }

    public void close() {
        int height = getHeight();
        Animation am = new TranslateAnimation(0f, 0f, 0f, -height * 1f);
        am.setDuration(500);
        am.setInterpolator(new AccelerateInterpolator());
        startAnimation(am);
        am.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    public void open() {
        int height = getHeight();
        setVisibility(View.VISIBLE);
        Animation am = new TranslateAnimation(0f, 0f, -height * 1f, 0f);
        am.setDuration(500);
        am.setInterpolator(new AccelerateInterpolator());
        startAnimation(am);
    }

    private void initBroadcastReceiver() {
        updateReceiver = new ChatListUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(ConstantValues.ACTION_UPDATE_GROUP_LIST);
        getContext().registerReceiver(updateReceiver, intentFilter);
    }

    public void destroy() {
        if (updateReceiver != null) {
            getContext().unregisterReceiver(updateReceiver);
            updateReceiver = null;
        }
        if (helper != null) {
            helper = null;
            OpenHelperManager.releaseHelper();
        }
    }

    private class ChatListUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            dbReload();
        }
    }
}
