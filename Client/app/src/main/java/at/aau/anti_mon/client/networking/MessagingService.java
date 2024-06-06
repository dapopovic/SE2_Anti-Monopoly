package at.aau.anti_mon.client.networking;

import javax.inject.Inject;

import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;
import android.util.Log;



/**
 * Facade class to reduce the complexity between JsonDataManager and WebSocketClient.
 * This class is responsible for sending and receiving messages.
 * Test
 */
public class MessagingService {

    private static WebSocketClient webSocketClient;

    @Inject
    public MessagingService(WebSocketClient client) {
        webSocketClient = client;
    }

    public static void initialize(WebSocketClient client) {
        if (webSocketClient == null) {
            webSocketClient = client;
        } else {
            Log.d(DEBUG_TAG, "WebSocketClient is already initialized.");
        }
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
    public static MessageSender createUserMessage(String username, boolean isOwner, boolean isReady, Commands command) {
        Log.d(DEBUG_TAG, " Username sending to new user:" + username + " isOwner: " + isOwner + " isReady: " + isReady + " command: " + command);

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("username", username)
                .addBoolean("isOwner", isOwner)
                .addBoolean("isReady", isReady)
                .build();

        Log.d(DEBUG_TAG, "Created jsonDataDTO: " + jsonDataDTO.toString());


        return new MessageSender(jsonDataDTO);
    }

    /**
     * Creates a JSON message for the game
     * { username: String, pin: String, command: Commands }
     * @param username the username of the game
     * @param pin the pin of the game -> method is overloaded
     * @param command the command to be executed
     * @return the JSON message as a string
     */
    public static MessageSender createUserMessage(String username, String pin, Commands command) {
        Log.d(DEBUG_TAG, " Username sending to new user:" + username + " pin: " + pin + " command: " + command);

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("username", username)
                .addString("pin", pin)
                .build();

        Log.d(DEBUG_TAG, "Created jsonDataDTO: " + jsonDataDTO.toString());


        return new MessageSender(jsonDataDTO);
    }


    /**
     * Creates a JSON message for the game
     * { username: String, command: Commands }
     * @param username the username of the game
     * @param command the command to be executed
     * @return the JSON message as a string
     */
    public static MessageSender createUserMessage(String username, Boolean isReady, Commands command) {
        Log.d(DEBUG_TAG, " Username sending to new user:" + username + " command: " + command);

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("username", username)
                .addBoolean("isReady", isReady)
                .build();

        Log.d(DEBUG_TAG, "Created jsonDataDTO: " + jsonDataDTO.toString());


        return new MessageSender(jsonDataDTO);
    }

    /**
     * Creates a JSON message for the game
     * { username: String, command: Commands }
     * @param username the username of the game
     * @param command the command to be executed
     * @return the JSON message as a string
     */
    public static MessageSender createUserMessage(String username, Commands command) {
        Log.d(DEBUG_TAG, " Username sending to new user:" + username + " command: " + command);

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("username", username)
                .build();

        Log.d(DEBUG_TAG, "Created jsonDataDTO: " + jsonDataDTO.toString());

        return new MessageSender(jsonDataDTO);
    }

    /**
     * Creates a JSON message for the game
     * { msg: String, command: Commands }
     * @param msg the message to be sent
     * @param command the command to be executed
     * @return the JSON message as a string
     */
    public static MessageSender createMessage(String msg, Commands command) {
        Log.d(DEBUG_TAG, " Message sending to new user:" + msg + " command: " + command);

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addString("msg", msg)
                .build();

        return new MessageSender(jsonDataDTO);
    }

    /**
     * Creates a JSON message for the game
     * { objectname: Object, command: Commands }
     * @param objectname the name of the object
     * @param object the object to be sent
     * @param command the command to be executed
     * @return the JSON message as a string
     */
    public static MessageSender createMessage(String objectname, Object object, Commands command) {
        Log.d(DEBUG_TAG, " Message sending to new user:" + object.toString() + " command: " + command);

        JsonDataDTO jsonDataDTO = new JsonDataDTO.Builder(command)
                .addObject(objectname, object)
                .build();

        return new MessageSender(jsonDataDTO);
    }

    public static class MessageSender {
        private final String message;


        //@SneakyThrows
        private MessageSender(JsonDataDTO jsonDataDTO) {
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

        public String getMessage() {
            return message;
        }
    }
}
