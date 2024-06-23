package at.aau.anti_mon.client.networking;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.util.Log;

import com.itkacher.okprofiler.BuildConfig;
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

import javax.inject.Inject;

import at.aau.anti_mon.client.command.Command;
import at.aau.anti_mon.client.command.CommandFactory;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.utilities.JsonDataUtility;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;


/**
 * WebSocketClient class to handle WebSocket connection to the server
 * It is injected into the activities and handles the communication with the server.
 * --> Singleton
 */
@Setter
@Getter
public class WebSocketClient { // implements AutoCloseable --> not needed i think?

    /**
     * localhost from the Android emulator is reachable as 10.0.2.2
     * <a href="https://developer.android.com/studio/run/emulator-networking">...</a>
     * URL for connecting to localhost:8080
     * URL for testing to localhost:53215
     * URL for testing outside of localhost
     * URL for testing connection to se2-server
     */
    private static final String[] WEBSOCKET_URIS = {
            "ws://10.0.2.2:8080/game?userID=",
            "ws://10.0.2.2:53215/game?userID=",
            "ws://192.168.31.176:53215/game",
            "ws://se2-demo.aau.at:53215/game?userID="};

    private static final String LOCAL_WEBSOCKET_URI_1 = "ws://10.0.2.2:8080/game?userID=";
    private static final String LOCAL_WEBSOCKET_URI_2 = "ws://10.0.2.2:53215/game";
    private static final String SE2_SERVER_WEBSOCKET_URI = "http://se2-demo.aau.at:53215/game?userID=";

    private WebSocket webSocket;
    private final OkHttpClient client;
    private CommandFactory commandFactory;
    //private final MutableLiveData<JsonDataDTO> liveData = new MutableLiveData<>();
    private boolean isConnected = false;
    public String userID;

    /**
     * Queue to store messages that are not sent yet ( because the connection is not established yet)
     */
    private final Queue<String> messageQueue = new LinkedList<>();

    @Inject
    public WebSocketClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(new OkHttpProfilerInterceptor());
        }
        this.client = builder.build();
        commandFactory = CommandFactory.getInstance();

       // connectToServer(); // -> keine dauerhafte Verbindung
    }


    /**
     * Connects to the server
     */
    public synchronized void connectToServer() {
        Log.d(DEBUG_TAG, "Connecting to server");

        // Mehrfache Verbindungen verhindern:
        if (webSocket != null){
            Log.d(DEBUG_TAG, "Connection already established");
            return;
        }

        // Um Sessions besser zu speicher wird die Base URI mit der User ID erweitert:
        String urlWithUserId = SE2_SERVER_WEBSOCKET_URI + userID;
        Request request = new Request.Builder().url(urlWithUserId).build();
        webSocket = client.newWebSocket(request, new WebSocketClientListener(this));
        Log.d(DEBUG_TAG, "Connection established");
        setConnected(true);
    }

    /**
     * Connects to the server with a specific user ID
     */
    public synchronized void connectToServer(String userID) {
        this.userID = userID;
        connectToServer();
    }

    /**
     * Handles incoming messages from the server
     * @param text The message from the server
     */
    void handleIncomingMessage(String text) {
        Log.d(DEBUG_TAG, "Received message: " + text);
        JsonDataDTO jsonDataDTO = JsonDataUtility.parseJsonMessage(text, JsonDataDTO.class);
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
        //liveData.postValue(jsonDataDTO);
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
        if (webSocket != null && webSocket.send(Objects.requireNonNull(JsonDataUtility.createJsonMessage(message)))) {
            Log.d(DEBUG_TAG, "Message sent: " + message);
        } else {
            Log.e(DEBUG_TAG, "Failed to send message or WebSocket not connected");
        }
    }

    /**
     * Checks if the WebSocket connection is established
     * @return True if the connection is established and websocket is not null, false otherwise
     */
    public boolean isConnected() {
        return isConnected && webSocket != null;
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            disconnect();
        } finally {
            super.finalize();
        }
    }

    public void close() {
        if (webSocket == null) {
            return;
        }
        try {
            disconnect();
        } catch (Exception e) {
            Log.e(DEBUG_TAG, "Failed to disconnect WebSocket", e);
        }
    }


    /**
     * Disconnects the WebSocket connection
     */
    public synchronized void disconnect(String reason) {
        if (webSocket != null) {
            webSocket.close(1000, Objects.requireNonNullElse(reason, "Client disconnected"));
            webSocket = null;
            isConnected = false;
        }
    }
    public synchronized void disconnect() {
        disconnect("no reason given");
    }


    public void onOpen() {
        isConnected = true;
        flushMessageQueue();
    }

    public void onClose() {
        isConnected = false;
        webSocket = null;
    }

    /**
     * Restarts the WebSocket connection
     */
    public synchronized void restartConnection() {
        disconnect("Client disconnected because of reconnection!");
        connectToServer();
    }

}
