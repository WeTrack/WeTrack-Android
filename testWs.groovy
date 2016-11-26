@Grab(group='com.squareup.okhttp3', module='okhttp-ws', version='3.4.2')
import okio.*
import okhttp3.*
import okhttp3.ws.*

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

// Main starts here
final OkHttpClient client = new OkHttpClient.Builder().readTimeout(0, TimeUnit.MILLISECONDS).build()
final Request establishRequest = new Request.Builder().url("http://${args[0]}").build()

final Wrapper<WebSocket> wsWrapper = new Wrapper<>();
final AtomicBoolean opened = new AtomicBoolean(false)
final WebSocketListener listener = new MyWsListener(wsWrapper, opened)
println '> Start to establish connection...'
connect(client, establishRequest, listener, wsWrapper)

WebSocket ws = wsWrapper.get()
BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in))
// Enter REPL loop
while (true) {
    String line = stdin.readLine()
    if (line == ':stop')
        break
    if (!opened.get()) {
        println '> WebSocket closed. Re-establish connection.'
        connect(client, establishRequest, listener, wsWrapper)
        ws = wsWrapper.get()
    }
    if (line == ':ping') {
        try {
            ws.sendPing(null)
        } catch (e) {
            println "> Exception occurred when trying to send Ping message: ${e.class.name}"
            e.printStackTrace()
            handleException(ws, opened, e)
        }
    } else {
        try {
            ws.sendMessage(RequestBody.create(WebSocket.TEXT, line))
        } catch (e) {
            println "> Exception occurred when trying to send text message: ${e.class.name}"
            e.printStackTrace()
            handleException(ws, opened, e)
        }
    }
}
ws.close(1000, 'Client exit')
client.dispatcher().executorService().shutdown()

def connect(client, establishRequest, listener, wsWrapper) {
    WebSocketCall call = WebSocketCall.create(client, establishRequest)
    call.enqueue(listener)
    synchronized (wsWrapper) {
        wsWrapper.wait() // Wait for connection open
    }
}

def handleException(ws, opened, e) {
    if (e instanceof SocketException && e.message.equalsIgnoreCase("Socket closed")) {
        opened.set(false)
    }
}

// Listener for received message from server
public class MyWsListener implements WebSocketListener {
    private Wrapper<WebSocket> wsWrapper;
    private AtomicBoolean opened;

    MyWsListener(Wrapper<WebSocket> wsWrapper, AtomicBoolean opened) {
        this.wsWrapper = wsWrapper
        this.opened = opened
    }

    public void onOpen(WebSocket webSocket, Response response) {
        println '> Connection established.'

        opened.compareAndSet(false, true)
        wsWrapper.set(webSocket);
        synchronized (wsWrapper) {
            wsWrapper.notify()
        }
    }

    public void onFailure(IOException e, Response response) {
        println "> Exception occurred during the communication: ${e.class.name}"
        e.printStackTrace()
        synchronized (wsWrapper) {
            wsWrapper.notify()
        }
    }

    public void onMessage(ResponseBody message) throws IOException {
        if (message.contentType().equals(WebSocket.TEXT)) // Interpret as UTF-8
            println message.source().readUtf8()
        else
            println '> Received binary message from server.'
        message.close()
    }

    public void onPong(Buffer payload) {
        println '> Received Pong message from server.'
    }

    public void onClose(int code, String reason) {
        println "> WebSocket closed on `$code:$reason`"
        opened.compareAndSet(true, false)
    }
}

// Util wrapper
public class Wrapper<T> {
    private T inner;

    public void set(T inner) {
        this.inner = inner
    }

    public T get() {
        return inner
    }
}