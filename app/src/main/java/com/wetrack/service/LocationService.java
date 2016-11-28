package com.wetrack.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.wetrack.BaseApplication;
import com.wetrack.client.EntityCallback;
import com.wetrack.client.MessageCallback;
import com.wetrack.client.WeTrackClient;
import com.wetrack.client.WeTrackClientWithDbCache;
import com.wetrack.client.json.LocalDateTimeTypeAdapter;
import com.wetrack.model.Location;
import com.wetrack.model.Message;
import com.wetrack.model.User;
import com.wetrack.utils.ConstantValues;
import com.wetrack.utils.PreferenceUtils;
import com.wetrack.utils.Tags;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Response;

public class LocationService extends Service {

    private WeTrackClient client = WeTrackClientWithDbCache.singleton();
    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();
    private final IBinder mBinder = new LocBinder();
    private boolean started = false;
    private AtomicReference<Map<String, Location>> receivedLocations = new AtomicReference<>();
    private long reloadDelay = 10000;

    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        receivedLocations.set(new HashMap<String, Location>());
        if (!started) {
            worker.start();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }
    }

    public void sendLocation(List<Location> locationList) {
        client.uploadLocations(PreferenceUtils.getCurrentUsername(),
                PreferenceUtils.getCurrentToken(), locationList, new MessageCallback() {
                    @Override
                    protected void onSuccess(String message) {
                        super.onSuccess(message);
                        Log.d(Tags.Location.SERVICE, "sending location succeeds");
                    }

                    @Override
                    protected void onFail(String message, int failedStatusCode) {
                        super.onFail(message, failedStatusCode);
                        Log.d(Tags.Location.SERVICE, "sending location fails");
                    }

                    @Override
                    protected void onError(Throwable ex) {
                        super.onError(ex);
                        Log.d(Tags.Location.SERVICE, "sending location gets error");
                    }
                });
    }

    public void onReceivedLocation(Location location) {
//        receivedLocations.get().put(location.getUsername(), location);
        String locationString = gson.toJson(location);
        Log.d(Tags.Location.SERVICE, "on received location: " + locationString);
        Intent intent = new Intent(ConstantValues.ACTION_UPDATE_LOCATION);
        intent.putExtra("received location", locationString);
//        intent.putExtra("receivedLocations", new ArrayList<Location>(receivedLocations.get().values()));
        BaseApplication.getContext().sendBroadcast(intent);
    }

//    public void update() {
//        receivedLocations.get().clear();
//    }

    private final BlockingDeque<LocTask> taskQueue = new LinkedBlockingDeque<>();

    private final Thread worker = new Thread(new Runnable() {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                if (taskQueue.isEmpty()) {
                    Log.d(Tags.Location.SERVICE, "taskQueue empty");
                    fillTaskQueue();
                    synchronized (taskQueue) {
                        try {
                            taskQueue.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Log.d(Tags.Location.SERVICE, "taskQueue not empty");
                    LocTask next = taskQueue.poll();
                    next.run();
                    if (taskQueue.isEmpty()) {
                        try {
                            Thread.sleep(reloadDelay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    });

    private void fillTaskQueue() {
        Log.d(Tags.Location.SERVICE, "fillTaskQueue starts");
        EntityCallback<List<User>> entityCallback = new EntityCallback<List<User>>() {
            @Override
            protected void onReceive(List<User> value) {
                super.onReceive(value);
                for (User user : value) {
                    try {
                        Log.d(Tags.Location.SERVICE, "fillTaskQueue onReceive, get user:" + user.getUsername());
                        taskQueue.put(getLocationTask(user));
                    } catch (InterruptedException e) {
                    }
                }
                synchronized (taskQueue) {
                    Log.d(Tags.Location.SERVICE, "fillTaskQueue onReceive, then notifyAll");
                    taskQueue.notifyAll();
                }
            }

            @Override
            protected void onResponse(Response<List<User>> response) {
                super.onResponse(response);
            }

            @Override
            protected void onException(Throwable ex) {
                super.onException(ex);
                synchronized (taskQueue) {
                    Log.d(Tags.Location.SERVICE, "fillTaskQueue onException, then notifyAll");
                    taskQueue.notifyAll();
                }
            }

            @Override
            protected void onErrorMessage(Message response) {
                super.onErrorMessage(response);
                synchronized (taskQueue) {
                    Log.d(Tags.Location.SERVICE, "fillTaskQueue onErrorMessage, then notifyAll");
                    taskQueue.notifyAll();
                }
            }
        };

        if (PreferenceUtils.getCurrentChatId().equals("")) {
            Log.d(Tags.Location.SERVICE, "no chat id, the first time login");
            client.getUserFriendList(PreferenceUtils.getCurrentUsername(),
                    PreferenceUtils.getCurrentToken(), entityCallback);
        } else {
            client.getChatMembers(PreferenceUtils.getCurrentChatId(),
                    PreferenceUtils.getCurrentToken(), entityCallback);
        }
    }

    private LocTask getLocationTask(final User user) {
        return new LocTask() {
            @Override
            void run() {
                client.getUserLatestLocation(user.getUsername(), new EntityCallback<Location>() {
                    @Override
                    protected void onReceive(Location value) {
                        super.onReceive(value);
                        Log.d(Tags.Location.SERVICE, "getUserLatestLocation onReceive");
                        onReceivedLocation(value);
                    }

                    @Override
                    protected void onResponse(Response<Location> response) {
                        super.onResponse(response);
                        Log.d(Tags.Location.SERVICE, "getUserLatestLocation onResponse");
                    }

                    @Override
                    protected void onException(Throwable ex) {
                        super.onException(ex);
                        Log.d(Tags.Location.SERVICE, "getUserLatestLocation onException");
//                        try {
//                            Thread.sleep(5000);
//                            taskQueue.putLast(getLocationTask(user));
//                            synchronized (taskQueue) {
//                                Log.d(Tags.Location.SERVICE, "getUserLatestLocation onException, then notifyAll");
//                                taskQueue.notifyAll();
//                            }
//                        } catch (InterruptedException e) {
//                        }
                    }

                    @Override
                    protected void onErrorMessage(Message response) {
                        super.onErrorMessage(response);
                        Log.d(Tags.Location.SERVICE, "getUserLatestLocation onErrorMessage");
//                        try {
//                            Thread.sleep(5000);
//                            taskQueue.putLast(getLocationTask(user));
//                            synchronized (taskQueue) {
//                                Log.d(Tags.Location.SERVICE, "getUserLatestLocation onErrorMessage, then notifyAll");
//                                taskQueue.notifyAll();
//                            }
//                        } catch (InterruptedException e) {
//                        }
                    }
                });
            }
        };
    }

    private static abstract class LocTask {
        abstract void run();
    }

    @Override
    public void onDestroy() {
        if (started) {
            worker.interrupt();
        }
        super.onDestroy();
    }
}
