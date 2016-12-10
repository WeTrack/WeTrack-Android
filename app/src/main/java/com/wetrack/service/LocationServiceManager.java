package com.wetrack.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wetrack.BaseApplication;
import com.wetrack.client.json.LocalDateTimeTypeAdapter;
import com.wetrack.model.Location;
import com.wetrack.utils.ConstantValues;
import com.wetrack.utils.Tags;

import org.joda.time.LocalDateTime;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class LocationServiceManager {

    private ServiceConnection mConnection;
    private boolean connected;
    private LocationService mService = null;
    private Context mContext;
    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();
    static private boolean shareLocPermission;

    public LocationServiceManager(Context mContext) {
        this.mContext = mContext;

        shareLocPermission = true;

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConstantValues.ACTION_UPDATE_LOCATION);
        BaseApplication.getContext().registerReceiver(new LocServiceBroadcastReceiver(), filter);

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                LocationService.LocBinder binder = (LocationService.LocBinder) service;
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
        Intent intent = new Intent(mContext, LocationService.class);
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

//    public void update() {
//        mService.update();
//    }

    public void stop() {
        if (connected) {
            mContext.unbindService(mConnection);
            connected = false;
        }
    }

    public void setShareLocPermission(boolean permission) {
        shareLocPermission = permission;
    }

    public void sendLocation(List<Location> locationList) {
        if (!connected) {
            Toast.makeText(mContext, "service-connection has been closed", Toast.LENGTH_SHORT).show();
        } else if (shareLocPermission){
//            Log.d(Tags.Setting.SHARE_LOC_PERMISSION, "has permission to share");
            mService.sendLocation(locationList);
        } else {
//            Log.d(Tags.Setting.SHARE_LOC_PERMISSION, "has no permission to share");
        }
    }

    public abstract void onReceivedLocation(Location location);

    public class LocServiceBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String locationString = intent.getStringExtra("received location");
            onReceivedLocation(gson.fromJson(locationString, Location.class));
        }
    }
}
