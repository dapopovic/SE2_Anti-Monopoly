package at.aau.anti_mon.client.networking;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.sql.SQLOutput;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

import javax.inject.Inject;

import at.aau.anti_mon.client.command.Command;
import at.aau.anti_mon.client.command.CommandFactory;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;


/**
 * WebSocketClient class to handle WebSocket connection to the server
 * It is injected into the activities and handles the communication with the server.
 * --> Singleton
 */
public class WebSocketClient {

    /**
     * localhost from the Android emulator is reachable as 10.0.2.2
     * https://developer.android.com/studio/run/emulator-networking
     */
    //private static final String WEBSOCKET_URI = "ws://10.0.2.2:8080/game?userID=";
    private static final String WEBSOCKET_URI = "ws://10.0.2.2:53215/game?userID=";
   //private static final String WEBSOCKET_URI = "ws://192.168.31.176:53215/game";

    /**
     * URL for testing connection to se2-server
     */
//    private static final String BASE_WEBSOCKET_URI = "ws://se2-demo.aau.at:53215/game?userID=";

    @Getter
    @Setter
    private WebSocket webSocket;
    private final OkHttpClient client;
    @Setter
    private CommandFactory commandFactory;
    private final MutableLiveData<JsonDataDTO> liveData = new MutableLiveData<>();
    private boolean isConnected = false;
    private String userID;

    /**
     * Queue to store messages that are not sent yet ( because the connection is not established yet)
     */
    private final Queue<String> messageQueue = new LinkedList<>();

    @Inject
    public WebSocketClient(OkHttpClient client, CommandFactory commandFactory) {
        this.client = client;
        this.commandFactory = commandFactory;
        connectToServer(); // -> keine dauerhafte Verbindung
    }


    /**
     * Connects to the server
     */
    public synchronized void connectToServer() {
        Log.d(DEBUG_TAG, "Connecting to server");
        // Mehrfache Verbindungen verhindern:
        if (webSocket != null || userID == null){
            Log.d(DEBUG_TAG, "Connection already established or no userID set");
            return;
        }

        // Um Sessions besser zu speicher wird die Base URI mit der User ID erweitert:
        String urlWithUserId = WEBSOCKET_URI + userID;
        Request request = new Request.Builder().url(urlWithUserId).build();
        webSocket = client.newWebSocket(request, createWebSocketListener());
    }

    /**
     * Creates a WebSocketListener to handle WebSocket events
     * @return The WebSocketListener
     */
    private WebSocketListener createWebSocketListener() {
        return new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                Log.d(DEBUG_TAG, "Opened Connection: " + response.message());

                isConnected = true;
                // Versuche alle wartenden Nachrichten zu senden
                flushMessageQueue();
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                handleIncomingMessage(text);
            }

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                Log.d(DEBUG_TAG, "Closed Connection " + reason);

                isConnected = false;
                WebSocketClient.this.webSocket = null;
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                String errorMessage = (response != null) ? response.message() : "No response";
                Log.e(DEBUG_TAG, "WebSocket - Failure - Error: " + t.getMessage() + ", Response Message: " + errorMessage);

                isConnected = false;

                if (response != null) {
                    Log.e(DEBUG_TAG, "WebSocket - Failure - Response Message: " + response.message());
                } else {
                    Log.e(DEBUG_TAG, "WebSocket - Failure - Received null response");
                }
                WebSocketClient.this.webSocket = null;
                Log.e(DEBUG_TAG, "Connection Failure", t);

                // TODO: Eventuell erneut versuchen, die Verbindung herzustellen ?
                connectToServer();  // TEST!
            }
        };
    }


    /**
     * Handles incoming messages from the server
     * @param text The message from the server
     */
    private void handleIncomingMessage(String text) {
        Log.d(DEBUG_TAG, "Received message: " + text);
        JsonDataDTO jsonDataDTO = JsonDataManager.parseJsonMessage(text, JsonDataDTO.class);
        if (jsonDataDTO == null) {
            Log.e(DEBUG_TAG, "Failed to parse JSON message");
            return;
        }
        if (jsonDataDTO.getCommand() == null) {
            Log.e(DEBUG_TAG, "Received message without command");
            return;
        }
        Command command = commandFactory.getCommand(jsonDataDTO.getCommand().name());
        command.execute(jsonDataDTO);
        liveData.postValue(jsonDataDTO);
    }


    /**
     * Sends a message to the server if the connection is established or queues the message if the connection is not established yet
     */
    public void sendMessageToServer(String message) {
        Log.d(DEBUG_TAG, "Sending message to server: " + message);

        if (webSocket != null && isConnected) {
            flushMessageQueue();
            sendWebSocketMessage(message);
        } else {
            Log.d(DEBUG_TAG, "Connection not established, adding message to queue");
            messageQueue.add(message);
            connectToServer();
        }
    }

    /**
     * Sends all messages in the queue to the server
     */
    private void flushMessageQueue() {
        while (!messageQueue.isEmpty() && isConnected && webSocket != null) {
            sendWebSocketMessage(messageQueue.poll());
        }
    }

    /**
     * Sends a message to the server
     * @param message The message to send
     */
    private void sendWebSocketMessage(String message) {
        if (webSocket != null && webSocket.send(message)) {
            Log.d(DEBUG_TAG, "Message sent: " + message);
        } else {
            Log.e(DEBUG_TAG, "Failed to send message, requeuing");
            // Füge die Nachricht erneut zur Queue hinzu, wenn das Senden fehlschlägt
            messageQueue.add(message);
        }
    }

    /**
     * Sends a JsonDataDTO message to the server
     * @param message The message to send as JsonDataDTO
     */
    public void sendJsonData(JsonDataDTO message) {
        if (webSocket != null && webSocket.send(Objects.requireNonNull(JsonDataManager.createJsonMessage(message)))) {
            Log.d(DEBUG_TAG, "Message sent: " + message);
        } else {
            Log.e(DEBUG_TAG, "Failed to send message or WebSocket not connected");
        }
    }

    /**
     * Checks if the WebSocket connection is established
     * @return True if the connection is established, false otherwise
     */
    public boolean isConnected() {
        // TODO: Implement isConnected
        return webSocket != null;
    }


    /**
     * Disconnects the WebSocket connection
     */
    public synchronized void disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Client disconnected");
            webSocket = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            disconnect();
        } finally {
            super.finalize();
        }
    }

    public LiveData<JsonDataDTO> getLiveData() {
        return liveData;
    }
    public WebSocketListener getWebSocketListener() {
        return createWebSocketListener();
    }

    public void setUserId(String userID) {
        this.userID = userID;
    }


}
