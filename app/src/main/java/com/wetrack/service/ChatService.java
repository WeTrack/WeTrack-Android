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
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.wetrack.BaseApplication;
import com.wetrack.client.WeTrackClientWithDbCache;
import com.wetrack.client.json.LocalDateTimeTypeAdapter;
import com.wetrack.database.WeTrackDatabaseHelper;
import com.wetrack.model.ChatMessage;
import com.wetrack.service.ws.ChatMessageAck;
import com.wetrack.service.ws.WebSocketManager;
import com.wetrack.service.ws.WsMessage;
import com.wetrack.service.ws.WsResponse;
import com.wetrack.utils.ConstantValues;
import com.wetrack.utils.PreferenceUtils;
import com.wetrack.utils.Tags;

import org.joda.time.LocalDateTime;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.ws.WebSocket;
import okhttp3.ws.WebSocketListener;
import okio.Buffer;

public class ChatService extends Service {

    private final IBinder mBinder = new ChatBinder();

    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
            .create();
    private final RuntimeExceptionDao<ChatMessage, String> chatMessageDao =
            OpenHelperManager.getHelper(this, WeTrackDatabaseHelper.class).getChatMessageDao();

    public ChatService() {}

    @Override
    public void onCreate() {
        super.onCreate();
        wsManager.start();
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
        wsManager.sendTextMessage("chat_message" + gson.toJson(chatMessage));
    }

    public void onReceivedMessage(ChatMessage chatMessage) {
        chatMessageDao.create(chatMessage);

        // Inform registered listeners
        Intent intent = new Intent(ConstantValues.ACTION_UPDATE_CHAT_MSG);
        intent.putExtra("chat_message", gson.toJson(chatMessage));
        BaseApplication.getContext().sendBroadcast(intent);
    }

    public void onReceivedMessageAck(ChatMessageAck messageAck) {
        // Update the respective database record
        ChatMessage messageInDB = chatMessageDao.queryForId(messageAck.getMessageId());
        if (messageInDB != null) {
            messageInDB.setSendTime(messageAck.getActualSendTime());
            chatMessageDao.update(messageInDB);
        }

        // Inform registered listeners
        Intent intent = new Intent(ConstantValues.ACTION_UPDATE_CHAT_MSG_STATUS);
        intent.putExtra("message_id", messageAck.getMessageId());
        BaseApplication.getContext().sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        wsManager.stop();
        OpenHelperManager.releaseHelper();
        super.onDestroy();
    }

    private final WebSocketListener listener = new WebSocketListener() {
        private static final String TYPE_CHAT_MESSAGE = "chat_message";
        private static final String TYPE_CHAT_MESSAGE_ACK = "chat_message_ack";
        private static final String TYPE_WS_MESSAGE = "message";

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Log.d(Tags.Chat.SERVICE, "WebSocket session established.");
        }

        @Override
        public void onFailure(IOException e, Response response) {
            Log.e(Tags.Chat.SERVICE, "Exception occurred during the WebSocket communication: ", e);
        }

        @Override
        public void onMessage(ResponseBody message) throws IOException {
            if (message.contentType().equals(WebSocket.BINARY))
                Log.d(Tags.Chat.SERVICE, "Received binary message from server.");
            else {
                String receivedText = message.string();
                Log.d(Tags.Chat.SERVICE, "Received text message: `" + receivedText + "`");
                if (receivedText.startsWith(TYPE_CHAT_MESSAGE_ACK)) {
                    Log.d(Tags.Chat.SERVICE, "Deserializing it as ChatMessageAck...");
                    ChatMessageAck chatMessageAck =
                            gson.fromJson(receivedText.substring(TYPE_CHAT_MESSAGE_ACK.length()), ChatMessageAck.class);
                    onReceivedMessageAck(chatMessageAck);
                } else if (receivedText.startsWith(TYPE_CHAT_MESSAGE)) {
                    Log.d(Tags.Chat.SERVICE, "Deserializing it as ChatMessage...");
                    ChatMessage chatMessage =
                            gson.fromJson(receivedText.substring(TYPE_CHAT_MESSAGE.length()), ChatMessage.class);
                    onReceivedMessage(chatMessage);
                } else if (receivedText.startsWith(TYPE_WS_MESSAGE)) {
                    Log.d(Tags.Chat.SERVICE, "Deserializing it as WsMessage...");
                    WsMessage receivedMessage =
                            gson.fromJson(receivedText.substring(TYPE_WS_MESSAGE.length()), WsMessage.class);
                    Log.d(Tags.Chat.SERVICE, receivedMessage.getMessage());
                }
            }
        }

        @Override
        public void onPong(Buffer payload) {
            Log.d(Tags.Chat.SERVICE, "Received Pong message from server.");
        }

        @Override
        public void onClose(int code, String reason) {
            Log.d(Tags.Chat.SERVICE, "WebSocket closed on " + code + ": " + reason);
        }
    };

    private final WebSocketManager wsManager = new WebSocketManager(
            new OkHttpClient.Builder().readTimeout(0, TimeUnit.MILLISECONDS).build(),
            WeTrackClientWithDbCache.singleton().getBaseUrl() + "ws/notifications",
            listener, "Token:" + PreferenceUtils.getCurrentToken()
    );
}
