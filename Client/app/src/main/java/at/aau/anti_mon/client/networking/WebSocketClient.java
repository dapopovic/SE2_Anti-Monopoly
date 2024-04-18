package at.aau.anti_mon.client.networking;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

import javax.inject.Inject;

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


/**
 * WebSocketClient class to handle WebSocket connection to the server
 * It is injected into the activities and handles the communication with the server.
 */
public class WebSocketClient {

    /**
     * localhost from the Android emulator is reachable as 10.0.2.2
     * https://developer.android.com/studio/run/emulator-networking
     */
   // private static final String WEBSOCKET_URI = "ws://10.0.2.2:51234/game";
    private static final String WEBSOCKET_URI = "ws://10.0.2.2:53215/game";
   //private static final String WEBSOCKET_URI = "ws://192.168.31.176:53215/game";

    /**
     * URL for testing connection to se2-server
     */
    //private static final String WEBSOCKET_URI = "ws://se2-demo.aau.at:53215/game";


    private WebSocket webSocket;
    private OkHttpClient client = new OkHttpClient();
    private CommandFactory commandFactory;

    private boolean isConnected = false;

    private Queue<String> messageQueue = new LinkedList<>();



    // TODO : Debug if Events not working
    //private WebSocketMessageHandler<JsonDataDTO> messageHandler;

    @Inject
    public WebSocketClient(OkHttpClient client, CommandFactory commandFactory) {
        this.client = client;
        this.commandFactory = commandFactory;
        //connectToServer(); // -> keine dauerhafte Verbindung
    }

    public synchronized void connectToServer() {
        // Mehrfache Verbindungen verhindern:
        if (webSocket != null) return;
        Request request = new Request.Builder().url(WEBSOCKET_URI).build();
        webSocket = client.newWebSocket(request, createWebSocketListener());
    }

    private WebSocketListener createWebSocketListener() {
        return new WebSocketListener() {
            @Override
            public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
                isConnected = true;
                Log.println(Log.DEBUG, "Network", "Opened Connection: " + response.message());

                // Versuche alle wartenden Nachrichten zu senden
                flushMessageQueue();
            }

            @Override
            public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
                handleIncomingMessage(text);
            }

            @Override
            public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
                isConnected = false;
                WebSocketClient.this.webSocket = null;
                Log.println(Log.DEBUG, "Network", "Closed Connection " + reason);
            }

            @Override
            public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, Response response) {
                isConnected = false;
                String errorMessage = (response != null) ? response.message() : "No response";
                Log.e("WebSocket Failure", "Error: " + t.getMessage() + ", Response Message: " + errorMessage);

                if (response != null) {
                    Log.e("WebSocket Failure", "Response Message: " + response.message());
                    Log.println(Log.WARN, "Network", "Connection Failure: " + response.message());
                } else {
                    Log.e("WebSocket Failure", "Received null response");
                    Log.println(Log.WARN, "Network", "Connection Failure: No response received");

                }
                WebSocketClient.this.webSocket = null;
                Log.e("Network", "Connection Failure", t);

                // TODO: Eventuell erneut versuchen, die Verbindung herzustellen ?
            }
        };
    }

    private void handleIncomingMessage(String text){
        try {
            Log.d("WebSocketClient", "Received message: " + text);
            JsonDataDTO jsonDataDTO = JsonDataManager.parseJsonMessage(text);
            if (jsonDataDTO != null) {
                Command command = commandFactory.getCommand(jsonDataDTO.getCommand().getCommand());
                if (command != null) {
                    command.execute(jsonDataDTO);
                } else {
                    Log.w("WebSocketClient", "Received unknown command: " + jsonDataDTO.getCommand());
                }
            } else {
                Log.e("WebSocketClient", "Failed to parse JSON message");
            }
        } catch (Exception e) {
            Log.e("WebSocketClient", "Error handling incoming message", e);
        }
    }


    /**
     * Test
     */
    public void sendMessageToServer(String message) {
        if (webSocket != null && isConnected) {

            flushMessageQueue();
            sendWebSocketMessage(message);

            //if (webSocket.send(message)) {
            //    Log.d("WebSocket", "Message sent: " + message);
            //} else {
            //    Log.e("Network", "Failed to send message");
            //}
        } else {
            //Log.e("Network", "WebSocket is not connected");
            messageQueue.add(message);  // Füge die Nachricht zur Warteschlange hinzu
            connectToServer();  // Stelle sicher, dass eine Verbindungsanforderung läuft
        }
    }

    private void flushMessageQueue() {
        if (!isConnected){
            return;
        }

        while (!messageQueue.isEmpty()) {
            String msg = messageQueue.poll();
            sendWebSocketMessage(msg);
        }
    }

    private void sendWebSocketMessage(String message) {
        if (webSocket != null && webSocket.send(message)) {
            Log.d("WebSocket", "Message sent: " + message);
        } else {
            Log.e("Network", "Failed to send message, requeuing");
            // Füge die Nachricht erneut zur Queue hinzu, wenn das Senden fehlschlägt
            messageQueue.add(message);
        }
    }

    /**
     * Test
     * @param message
     */
    public void sendMessage(JsonDataDTO message) {
        if (webSocket != null && webSocket.send(Objects.requireNonNull(JsonDataManager.createJsonMessage(message)))) {
            Log.d("WebSocket", "Message sent: " + message);
        } else {
            Log.e("Network", "Failed to send message or WebSocket not connected");
        }
    }

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

    // Simple method to demonstrate unit testing and test coverage with sonarcloud
    public static String concatenateStrings(String first, String second) {
        return first + " " + second;
    }

    public boolean isConnected() {
        // TODO: Implement isConnected
        return webSocket != null;
    }



}
