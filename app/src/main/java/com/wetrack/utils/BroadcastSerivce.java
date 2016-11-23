package com.wetrack.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.wetrack.BaseApplication;

public class BroadcastSerivce extends Service{

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

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
