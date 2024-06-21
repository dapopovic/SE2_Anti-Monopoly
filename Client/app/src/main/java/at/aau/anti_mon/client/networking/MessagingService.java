package at.aau.anti_mon.client.networking;

import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import lombok.Getter;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.util.Log;

import javax.inject.Inject;


/**
 * Mediator class to reduce the complexity between JsonDataManager and WebSocketClient.
 * This class is responsible for communicating with the WebSocketClient.
 */
public class MessagingService {

    private static WebSocketClient webSocketClient;
    private static MessagingService instance;

    private MessagingService() {
    }

    public static synchronized MessagingService getInstance() {
        if (instance == null) {
            instance = new MessagingService();
        }
        return instance;
    }

    /**
     * Initialisiert den MessagingService und injiziert den WebSocketClient
     * Diese Methode wird einmalig im Konstruktor des WebSocketClients aufgerufen.
     * @param webSocketClient der WebSocketClient
     */
    @Inject
    public void initialize(WebSocketClient webSocketClient) {
        MessagingService.webSocketClient = webSocketClient;
    }

    public static void reconnectToServer() {
        webSocketClient.restartConnection();
    }

    /**
     * Checks if the WebSocket connection is established
     * @return True if the connection is established and websocket is not null, false otherwise
     */
    public static boolean getWebSocketConnectionStatus() {
        return webSocketClient.isConnected();
    }

    public static void connectToServerWithUserID(String userID) {
        if (webSocketClient != null) {
            webSocketClient.setUserID(userID);
            webSocketClient.restartConnection();
        } else {
            Log.e("MessagingService", "WebSocketClient is not initialized.");
        }
    }

    public static MessageContainer createHeartbeatMessage() {
        return new MessageContainer(new JsonDataDTO.Builder(Commands.HEARTBEAT).addString("msg","PONG").build());
    }

    /**
     * Creates a JSON message for the game with the dice number
     * { username: String, dicenumber: int, command: Commands }
     * @param username the username of the game
     * @param dice the dice number
     * @return the JSON message as a string
     */
    public static MessageContainer createGameMessage(String username, int dice, Commands command) {

        Log.d(DEBUG_TAG, " Username sending to new user:" + username + " dice: " + dice + " command: " + command);

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("username", username)
                .addInt("dicenumber", dice)
                .build();

        Log.d(DEBUG_TAG, "Created jsonDataDTO: " + jsonDataDTO.toString());

        return new MessageContainer(jsonDataDTO);
    }

    public static MessageContainer createGameBalanceMessage(String username, int balance, Commands command) {

        Log.d(DEBUG_TAG, " Username sending to new user:" + username + " balance: " + balance + " command: " + command);

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("username", username)
                .addInt("new_balance", balance)
                .build();

        Log.d(DEBUG_TAG, "Created jsonDataDTO: " + jsonDataDTO.toString());

        return new MessageContainer(jsonDataDTO);
    }

    /**
     * Creates a JSON message for the game
     * { username: String, isOwner: boolean, isReady: boolean, command: Commands}
     * @param username the username of the game
     * @param isOwner true if the user is the owner of the game
     * @param isReady true if the user is ready
     * @param command the command to be executed
     * @return the JSON message as a string
     */
    public static MessageContainer createUserMessage(String username, boolean isOwner, boolean isReady, Commands command) {

        Log.d(DEBUG_TAG, " Username sending to new user:" + username + " isOwner: " + isOwner + " isReady: " + isReady + " command: " + command);

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("username", username)
                .addBoolean("isOwner", isOwner)
                .addBoolean("isReady", isReady)
                .build();

        Log.d(DEBUG_TAG, "Created jsonDataDTO: " + jsonDataDTO.toString());

        return new MessageContainer(jsonDataDTO);
    }

    /**
     * Creates a JSON message for the game
     * { username: String, pin: String, command: Commands }
     * @param username the username of the game
     * @param pin the pin of the game -> method is overloaded
     * @param command the command to be executed
     * @return the JSON message as a string
     */
    public static MessageContainer createUserMessage(String username, String pin, Commands command) {

        Log.d(DEBUG_TAG, " Username sending to new user:" + username + " pin: " + pin + " command: " + command);

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("username", username)
                .addString("pin", pin)
                .build();

        Log.d(DEBUG_TAG, "Created jsonDataDTO: " + jsonDataDTO.toString());

        return new MessageContainer(jsonDataDTO);
    }


    /**
     * Creates a JSON message for the game
     * { username: String, command: Commands }
     * @param username the username of the game
     * @param command the command to be executed
     * @return the JSON message as a string
     */
    public static MessageContainer createUserMessage(String username, Boolean isReady, Commands command) {

        Log.d(DEBUG_TAG, " Username sending to new user:" + username + " command: " + command);

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("username", username)
                .addBoolean("isReady", isReady)
                .build();

        Log.d(DEBUG_TAG, "Created jsonDataDTO: " + jsonDataDTO.toString());

        return new MessageContainer(jsonDataDTO);
    }

    /**
     * Creates a JSON message for the game
     * { username: String, command: Commands }
     * @param username the username of the game
     * @param command the command to be executed
     * @return the JSON message as a string
     */
    public static MessageContainer createUserMessage(String username, Commands command) {

        Log.d(DEBUG_TAG, " Username sending to new user:" + username + " command: " + command);

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("username", username)
                .build();

        Log.d(DEBUG_TAG, "Created jsonDataDTO: " + jsonDataDTO.toString());

        return new MessageContainer(jsonDataDTO);
    }

    /**
     * Creates a JSON message for the game
     * { msg: String, command: Commands }
     * @param msg the message to be sent
     * @param command the command to be executed
     * @return the JSON message as a string
     */
    public static MessageContainer createMessage(String msg, Commands command) {

        Log.d(DEBUG_TAG, " Message sending to new user:" + msg + " command: " + command);

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("msg", msg)
                .build();

        return new MessageContainer(jsonDataDTO);
    }

    /**
     * Creates a JSON message for the game
     * { objectname: Object, command: Commands }
     * @param objectname the name of the object
     * @param object the object to be sent
     * @param command the command to be executed
     * @return the JSON message as a string
     */
    public static MessageContainer createMessage(String objectname, Object object, Commands command) {

        Log.d(DEBUG_TAG, " Message sending to new user:" + object.toString() + " command: " + command);

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addObject(objectname, object)
                .build();

        return new MessageContainer(jsonDataDTO);
    }

    @Getter
    public static class MessageContainer {
        private final String message;

        private MessageContainer(JsonDataDTO jsonDataDTO) {
            //message = MAPPER.writeValueAsString(jsonDataDTO);
            this.message = JsonDataManager.createJsonMessage(jsonDataDTO);
        }

        public void sendMessage() {
            if (webSocketClient != null && message != null) {
                webSocketClient.sendMessageToServer(message);
                Log.d(DEBUG_TAG, "Sent message: " + message);
            } else {
                Log.e(DEBUG_TAG, "Failed to send message from JsonDataManager, WebSocketClient or message is null");
            }
        }
    }
}
