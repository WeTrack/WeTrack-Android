package com.wetrack.service.ws;

import android.util.Log;

import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.wetrack.utils.Tags;

import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.ws.WebSocket;
import okhttp3.ws.WebSocketCall;
import okhttp3.ws.WebSocketListener;
import okio.Buffer;

public class WebSocketManager {

    private final WebSocketListener delegateListener;

    private final OkHttpClient client;
    private final Request establishRequest;

    private boolean started = false;
    private final AtomicBoolean opened = new AtomicBoolean(false);
    private final AtomicReference<WebSocket> wsWrapper = new AtomicReference<>(null);

    private final BlockingDeque<WsTask> taskQueue = new LinkedBlockingDeque<>();

    private final String authenMessage;

    public WebSocketManager(OkHttpClient client, String endpointPath,
                            WebSocketListener delegateListener,
                            String authenMessage
    ) {
        this.client = client;
        this.delegateListener = delegateListener;
        this.authenMessage = authenMessage;

        establishRequest = new Request.Builder().url(endpointPath).build();
        connect();
    }

    public void start() {
        if (!started) {
            worker.start();
            started = true;
        }
    }

    public void stop() {
        if (started)
            worker.interrupt();
    }

    public void sendTextMessage(String text) {
        Log.d(Tags.Chat.WS_MANAGER, "Adding sending task for text `" + text + "`");
        try {
            taskQueue.put(sendTextTask(text));
            synchronized (taskQueue) {
                taskQueue.notifyAll();
            }
        } catch (InterruptedException e) {}
    }

    public void connect() {
        try {
            taskQueue.putFirst(connectTask());
            taskQueue.put(sendTextTask(authenMessage));
            synchronized (taskQueue) {
                taskQueue.notifyAll();
            }
        } catch (InterruptedException e) {}
    }

    private WsTask sendTextTask(final String text) {
        return new WsTask() {
            @Override
            Type getType() {
                return Type.Send;
            }

            @Override
            boolean run() {
                Log.d(Tags.Chat.WS_MANAGER_TASK, "Trying to send text message `" + text + "`");
                if (!opened.get()) {
                    Log.d(Tags.Chat.WS_MANAGER_TASK, "WebSocket session is closed. Waiting for reconnection...");
                    connect();
                    return false;
                }
                try {
                    Log.d(Tags.Chat.WS_MANAGER_TASK, "Sending message...");
                    wsWrapper.get().sendMessage(RequestBody.create(WebSocket.TEXT, text));
                } catch (IOException ex) {
                    Log.e(Tags.Chat.WS_MANAGER_TASK, "Exception occurred when trying to send text message: ", ex);
                    if (ex instanceof SocketException &&
                            ex.getMessage().equalsIgnoreCase("Socket closed")) {
                        opened.set(false);
                        connect();
                        return false;
                    }
                }
                return true;
            }
        };
    }

    private WsTask connectTask() {
        return new WsTask() {
            @Override
            Type getType() {
                return Type.Connect;
            }

            @Override
            boolean run() {
                Log.d(Tags.Chat.WS_MANAGER_TASK, "Establishing WebSocket session...");
                WebSocketCall call = WebSocketCall.create(client, establishRequest);
                call.enqueue(listener);
                Log.d(Tags.Chat.WS_MANAGER_WORKER, "Waiting for response...");
                synchronized (wsWrapper) {
                    try {
                        wsWrapper.wait();
                    } catch (InterruptedException e) {}
                }
                Log.d(Tags.Chat.WS_MANAGER_TASK, "Response received.");
                return opened.get();
            }
        };
    }

    private final WebSocketListener listener = new WebSocketListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            Log.d(Tags.Chat.WS_MANAGER, "WebSocket session established.");
            opened.set(true);
            wsWrapper.set(webSocket);
            synchronized (wsWrapper) {
                wsWrapper.notify();
            }
            delegateListener.onOpen(webSocket, response);
        }

        @Override
        public void onFailure(IOException e, Response response) {
            Log.e(Tags.Chat.WS_MANAGER, "Exception occurred during session establishment: ", e);
            delegateListener.onFailure(e, response);
        }

        @Override
        public void onMessage(ResponseBody message) throws IOException {
            Log.d(Tags.Chat.WS_MANAGER, "Received text message from server.");
            delegateListener.onMessage(message);
        }

        @Override
        public void onPong(Buffer payload) {
            Log.d(Tags.Chat.WS_MANAGER, "Received Pong message from server.");
            delegateListener.onPong(payload);
        }

        @Override
        public void onClose(int code, String reason) {
            opened.set(false);
            Log.d(Tags.Chat.WS_MANAGER, "WebSocket session closed on " + code + ": " + reason);
            delegateListener.onClose(code, reason);
        }
    };

    private final Thread worker = new Thread(new Runnable() {
        @Override
        public void run() {
            Log.d(Tags.Chat.WS_MANAGER_WORKER, "Worker thread started.");
            while (!Thread.interrupted()) {
                if (taskQueue.isEmpty()) {
                    Log.d(Tags.Chat.WS_MANAGER_WORKER, "Worker thread waiting for new task...");
                    synchronized (taskQueue) {
                        try {
                            taskQueue.wait(10000); // Wait for 10 seconds
                        } catch (InterruptedException ex) {}
                    }
                    try {
                        wsWrapper.get().sendPing(null);
                    } catch (IOException ex) {
                        Log.e(Tags.Chat.WS_MANAGER_TASK, "Exception occurred when trying to send ping message: ", ex);
                        if (ex instanceof SocketException &&
                                ex.getMessage().equalsIgnoreCase("Socket closed")) {
                            opened.set(false);
                            connect();
                        }
                    }
                } else {
                    Log.d(Tags.Chat.WS_MANAGER_WORKER, "New task received.");
                    WsTask next = taskQueue.poll();
                    if (!next.run()) {
                        if (next.getType() == WsTask.Type.Connect) {
                            Log.d(Tags.Chat.WS_MANAGER_WORKER, "Failed on connect task. Putting it to the front of the queue...");
                            try {
                                taskQueue.putFirst(next);
                            } catch (InterruptedException ex) {}
                        } else {
                            Log.d(Tags.Chat.WS_MANAGER_WORKER, "Failed on sending task. Putting it to the back of the queue...");
                            try {
                                taskQueue.put(next);
                            } catch (InterruptedException ex) {}
                        }
                    }
                }
            }
            Log.d(Tags.Chat.WS_MANAGER_WORKER, "Worker thread interrupted. Trying to close WebSocket session.");
            if (opened.get()) {
                try {
                    wsWrapper.get().close(1000, "Client exit");
                } catch (IOException ex) {
                    Log.w(Tags.Chat.WS_MANAGER_WORKER, "Exception occurred when trying to close the WebSocket session: ", ex);
                }
            }
        }
    });

    private static abstract class WsTask {
        abstract Type getType();

        abstract boolean run();

        enum Type {
            Send, Connect
        }
    }
}
