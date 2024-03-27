package at.aau.serg.websocketdemoapp.networking;

import android.util.Log;

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
    private final String WEBSOCKET_URI = "ws://10.0.2.2:8080/websocket-example-handler";

    private WebSocket webSocket;

    public void connectToServer(WebSocketMessageHandler<String> messageHandler) {
        if (messageHandler == null)
            throw new IllegalArgumentException("messageHandler is required");

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(WEBSOCKET_URI)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d("Network", "connected");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                messageHandler.onMessageReceived(text);
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                // WebSocket connection closed
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                // Permission needed to transmit cleartext in the manifest
                // https://stackoverflow.com/questions/45940861/android-8-cleartext-http-traffic-not-permitted
                Log.d("Network", "connection failure");
            }
        });
    }

    public void sendMessageToServer(String msg) {
        webSocket.send(msg);
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            webSocket.close(1000, "Closing");
        } finally {
            super.finalize();
        }
    }

    // Simple method to demonstrate unit testing and test coverage with sonarcloud
    public static String concatenateStrings(String first, String second) {
        return first + " " + second;
    }
}
