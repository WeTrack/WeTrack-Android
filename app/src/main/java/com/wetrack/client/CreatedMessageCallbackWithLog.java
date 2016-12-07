package com.wetrack.client;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.wetrack.utils.Tags;

public abstract class CreatedMessageCallbackWithLog extends CreatedMessageCallback {
    private static final String TAG = Tags.Client.CALLBACK;

    private Context context;

    public CreatedMessageCallbackWithLog(Context context) {
        this.context = context;
    }

    protected abstract void onSuccess(String newEntityId, String message);

    @Override
    protected void onFail(String message, int failedStatusCode) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onError(Throwable ex) {
        Toast.makeText(context, "Exception occurred during the connection: " + ex.getClass().getName(), Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Exception occurred during the connection: ", ex);
    }
}
