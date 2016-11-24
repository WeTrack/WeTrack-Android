package com.wetrack.ChatService;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.wetrack.BaseApplication;
import com.wetrack.model.ChatMessage;
import com.wetrack.utils.ConstantValues;

public class ChatService extends Service {
    private final IBinder mBinder = new ChatBinder();

    public ChatService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class ChatBinder extends Binder {
        ChatService getService() {
            return ChatService.this;
        }
    }

    public void sendMessage(ChatMessage chatMessage) {
        //TODO send message to server
    }

    /**
     * after receiving message from server, call this function
     * */
    public void onReceivedMessage(ChatMessage chatMessage) {
        //TODO store the received message into local database

        //below is to info activities to load new message from local database
        Intent intent1 = new Intent(ConstantValues.ACTION_UPDATE_CHAT_MSG);
        BaseApplication.getContext().sendBroadcast(intent1);
    }
}
