package at.aau.anti_mon.client.networking;

import android.util.Log;

import androidx.annotation.NonNull;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import at.aau.anti_mon.client.command.Command;
import at.aau.anti_mon.client.command.CommandFactory;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.events.ReceiveMessageEvent;
import at.aau.anti_mon.client.events.SendMessageEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


public class WebSocketClient {

    // TODO use correct hostname:port
    /**
     * localhost from the Android emulator is reachable as 10.0.2.2
     * https://developer.android.com/studio/run/emulator-networking
     */
    //private final String WEBSOCKET_URI = "ws://10.0.2.2:8080/game";

    /**
     * URL for testing connection to se2-server
     */
    private static final String WEBSOCKET_URI = "ws://se2-demo.aau.at:53215/game";
    private OkHttpClient client;
    private WebSocket webSocket;
    private CommandFactory commandFactory;

    public WebSocketClient() {
        EventBus.getDefault().register(this);
        this.client = new OkHttpClient();
        this.commandFactory = new CommandFactory();
        connectToServer();
    }

    public void connectToServer() {
        Request request = new Request.Builder()
                .url(WEBSOCKET_URI)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                Log.println(Log.DEBUG, "Network", "Opened Connection: " + response.message());

            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                Log.println(Log.DEBUG, "Network", "Received message: " + text);
                EventBus.getDefault().post(new ReceiveMessageEvent(text));
            }

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                Log.println(Log.DEBUG, "Network", "Closed Connection " + reason);
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                Log.println(Log.WARN, "Network", "Connection Failure: " + response.message());
            }
        });
    }




    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onSendMessageEvent(SendMessageEvent event) {
        if (webSocket != null) {
            webSocket.send(event.getMessage());
        } else {
            Log.e("Network", "WebSocket is not connected");
        }
    }


    private void sendMessageToServer(String message) {
        if (webSocket != null) {
            webSocket.send(message);
        } else {
            Log.e("Network", "WebSocket is not connected");
        }
    }

    public void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Client disconnected");
            webSocket = null;
        }
        EventBus.getDefault().unregister(this);
        client.dispatcher().executorService().shutdown();
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            disconnect();
        } finally {
            super.finalize();
        }
    }

    // Simple method to demonstrate unit testing and test coverage with sonarcloud
    public static String concatenateStrings(String first, String second) {
        return first + " " + second;
    }
}
