package com.wetrack;

/**
 * Created by moziliang on 16/9/25.
 */
import android.app.Application;
import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.wetrack.database.WeTrackDatabaseHelper;

public class BaseApplication extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        OpenHelperManager.setOpenHelperClass(WeTrackDatabaseHelper.class);
    }

    public static Context getContext() {
        return mContext;
    }
}
