package com.wetrack.utils;

import android.content.Intent;

import com.wetrack.BaseApplication;

public class BroadcastSerivce {

    static public void infoUpdateGroupList() {
        Intent intent = new Intent();
        intent.setAction(ConstantValues.ACTION_UPDATE_GROUP_LIST);
        BaseApplication.getContext().sendBroadcast(intent);
    }

    static public void infoUpdateFriendList() {
        Intent intent = new Intent();
        intent.setAction(ConstantValues.ACTION_UPDATE_FRIEND_LIST);
        BaseApplication.getContext().sendBroadcast(intent);
    }

    static public void infoUpdateUserInfo() {
        Intent intent = new Intent();
        intent.setAction(ConstantValues.ACTION_UPDATE_USER_INFO);
        BaseApplication.getContext().sendBroadcast(intent);
    }

    static public void infoUpdateUserPos() {
        Intent intent = new Intent();
        intent.setAction(ConstantValues.ACTION_UPDATE_USER_POS);
        BaseApplication.getContext().sendBroadcast(intent);
    }
}
