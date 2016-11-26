package com.wetrack.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import com.wetrack.BaseApplication;
import com.wetrack.model.ChatMessage;
import com.wetrack.utils.ConstantValues;

public abstract class ChatServiceManager {

    private ServiceConnection mConnection;
    private boolean connected;
    private ChatService mService = null;
    private Context mContext;

    public ChatServiceManager(Context context) {
        mContext = context;

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantValues.ACTION_UPDATE_CHAT_MSG);
        BaseApplication.getContext().registerReceiver(new MyBroadcastReceiver(), filter);

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                ChatService.ChatBinder binder = (ChatService.ChatBinder) service;
                mService = binder.getService();
                connected = true;
            }
            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                connected = false;
            }
        };
    }

    public void start() {
        Intent intent = new Intent(mContext, ChatService.class);
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void stop() {
        if (connected) {
            mContext.unbindService(mConnection);
            connected = false;
        }
    }

    public void sendChatMessage(ChatMessage chatMessage) {
        if (!connected) {
            Toast.makeText(mContext, "service-connection has been closed", Toast.LENGTH_SHORT).show();
        } else {
            mService.sendMessage(chatMessage);
        }
    }

    public abstract void onReceivedMessage();

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConstantValues.ACTION_UPDATE_CHAT_MSG))
                onReceivedMessage();
        }
    }
}
