package at.aau.anti_mon.client.websocketserver;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class WebsocketHandlerServerImpl {
    private MockWebServer server;

    public void start() throws IOException {
        server = new MockWebServer();

        // Schedule some responses.
        server.enqueue(new MockResponse().withWebSocketUpgrade(new WebSocketEcho()));

        // Start the server.
        server.start();
    }

    public void stop() throws IOException {
        server.shutdown();
    }

    public String getUrl() {
        return server.url("/").toString();
    }

    private static class WebSocketEcho extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, @NonNull Response response) {
            webSocket.send("Hello, client!");
        }

        @Override
        public void onMessage(WebSocket webSocket, @NonNull String text) {
            // Echo back the received message
            webSocket.send(text);
        }
    }
}