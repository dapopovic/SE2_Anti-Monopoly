package at.aau.anti_mon.client.networking;


import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.util.Log;

import lombok.NonNull;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * WebSocketClientListener class to handle WebSocket connection to the server
 * If a message is received, it is passed to the WebSocketClient
 */
public class WebSocketClientListener extends WebSocketListener {

    private final WebSocketClient client;

    public WebSocketClientListener(WebSocketClient client) {
        this.client = client;
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        Log.d(DEBUG_TAG, "Opened Connection: " + response.message());
        client.onOpen();
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        client.handleIncomingMessage(text);
    }

    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        Log.d(DEBUG_TAG, "Closed Connection " + reason);
        client.onClose();
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
        String errorMessage = (response != null) ? response.message() : "No response";
        Log.e(DEBUG_TAG, "WebSocket - Failure - Error: " + t.getMessage() + ", Response Message: " + errorMessage);
        client.onClose();

        // TODO: Fehlderbehandlung -  Eventuell erneut versuchen, die Verbindung herzustellen ?
        client.connectToServer();
    }
}
