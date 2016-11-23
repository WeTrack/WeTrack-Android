package com.wetrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.wetrack.client.WeTrackClient;
import com.wetrack.utils.ConstantValues;
import com.wetrack.utils.Tools;

public class GroupListView extends RelativeLayout {
    private WeTrackClient client = WeTrackClient.getInstance(ConstantValues.serverBaseUrl, ConstantValues.timeoutSeconds);
    private GroupListUpdateReceiver mGroupListUpdateReceiver = null;
    private LinearLayout groupListLinearLayout;

    //false means close, true means open
    private boolean groupListViewState;
    final static public boolean CLOSE_STATE = false;
    final static public boolean OPEN_STATE = true;

    public GroupListView(Context context) {
        super(context);
        init();
    }

    public GroupListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GroupListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        groupListViewState = CLOSE_STATE;

        initBroadcastReceiver();
        //set params for this view
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                Tools.getScreenW(), Tools.getScreenH());

        layoutParams.setMargins(0, -Tools.getScreenH(), 0, 0);
        setLayoutParams(layoutParams);
        setBackgroundColor(Color.WHITE);

        //linearlayout to store all groupList items
        ScrollView scrollView = new ScrollView(getContext());
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        groupListLinearLayout = new LinearLayout(getContext());
        groupListLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        groupListLinearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(groupListLinearLayout);
        addView(scrollView);
        reLoadGroupList();
    }

    public boolean getGroupListViewState() {
        return groupListViewState;
    }

    private void reLoadGroupList() {
//        groupListLinearLayout.removeAllViews();
//        // get all groupList items
//        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
//        allgroups = GroupDataFormat.getAllGroups();
//
//        //get all friends to form a group, and add this group into allgroups
//        ArrayList<String>allFriends = new ArrayList<>();
//        for (DataFormat friendDataFormat : FriendDataFormat.getAllFriend()) {
//            allFriends.add(friendDataFormat.getValueByName(FriendDataFormat.ATTRI_FRIEND_NAME));
//        }
//        allgroups.add(0, new GroupDataFormat(getContext().getResources().getText(R.string.allFriend).toString(), allFriends));
//
//        //show all groups items in linearLayout
//        for (DataFormat dataFormat : allgroups) {
//            GroupDataFormat groupDataFormat = new GroupDataFormat(dataFormat);
//            RelativeLayout groupListItemLayout = (RelativeLayout) layoutInflater.inflate(R.layout.group_list_item, null);
//
//            TextView groupNameTextView = (TextView) groupListItemLayout.findViewById(R.id.group_name);
//            groupNameTextView.setText(groupDataFormat.getValueByName(GroupDataFormat.ATTRI_NAME));
//
//            TextView groupMemberTextView = (TextView) groupListItemLayout.findViewById(R.id.group_member);
//            String memberString = "";
//            ArrayList<String> members = groupDataFormat.getGroupMembers();
//            for (int i = 0; i < members.size(); i++) {
//                if (i != members.size() - 1) {
//                    memberString += members.get(i) + ", ";
//                } else {
//                    memberString += members.get(i);
//                }
//            }
//            groupMemberTextView.setText(memberString);
//            groupListLinearLayout.addView(groupListItemLayout);
//
//            groupListItemLayout.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
//        }
    }

    public void close() {
        groupListViewState = CLOSE_STATE;

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
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        Tools.getScreenW(), Tools.getScreenH());
                layoutParams.setMargins(0, -Tools.getScreenH(), 0, 0);
                setLayoutParams(layoutParams);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    public void open() {
        groupListViewState = OPEN_STATE;

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                Tools.getScreenW(), ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.menu_bar);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.setMargins(0,0,0,0);
        setLayoutParams(layoutParams);

        int height = getHeight();
        Animation am = new TranslateAnimation(0f, 0f, -height * 1f, 0f);
        am.setDuration(500);
        am.setInterpolator(new AccelerateInterpolator());
        startAnimation(am);
    }

    private void initBroadcastReceiver() {
        mGroupListUpdateReceiver = new GroupListUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter(ConstantValues.ACTION_UPDATE_GROUP_LIST);
        getContext().registerReceiver(mGroupListUpdateReceiver, intentFilter);
    }

    public void destroy() {
        if (mGroupListUpdateReceiver != null) {
            getContext().unregisterReceiver(mGroupListUpdateReceiver);
            mGroupListUpdateReceiver = null;
        }
    }

    private class GroupListUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //reload

        }
    }
}
