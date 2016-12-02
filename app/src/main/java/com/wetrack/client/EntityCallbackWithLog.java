package com.wetrack.client;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.wetrack.model.Message;
import com.wetrack.utils.Tags;

public abstract class EntityCallbackWithLog<T> extends EntityCallback<T> {
    private static final String TAG = Tags.Client.CALLBACK;

    private Context context;

    public EntityCallbackWithLog(Context context) {
        this.context = context;
    }

    protected abstract void onReceive(T value);

    @Override
    protected void onErrorMessage(Message message) {
        Toast.makeText(context, message.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onException(Throwable ex) {
        Toast.makeText(context, "Exception occurred during the connection: " + ex.getClass().getName(), Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Exception occurred during the connection: ", ex);
    }
}
