package at.aau.anti_mon.client.json;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.app.Application;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import javax.inject.Singleton;

import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.networking.NetworkModule;
import at.aau.anti_mon.client.networking.WebSocketClient;
import lombok.Getter;
import lombok.SneakyThrows;

import at.aau.anti_mon.client.DaggerAppComponent;

/**
 * This class is responsible for creating and parsing JSON messages.
 * It uses the Jackson library to serialize and deserialize JSON messages.
 * The class is implemented as a singleton.
 */
@Singleton
public class JsonDataManager {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String FAILURE_MESSAGE = "Failed to create JSON message";
    WebSocketClient webSocketClient;

    @Inject
    public JsonDataManager(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    /**
     * This class is used to send messages to the server.
     * Don't convert to record -> Android-Studio has problems with records
     */
    @Getter
    public static class MessageSender {
        private final String message;

        public MessageSender(String message) {
            this.message = message;
        }

        @SneakyThrows
        public MessageSender(JsonDataDTO jsonDataDTO) {
                message = MAPPER.writeValueAsString(jsonDataDTO);
        }

        public void sendMessage() {
            JsonDataManager instance = DaggerAppComponent.builder()
                    .build()
                    .getJsonDataManager();
            instance.sendMessage(this.message);
        }
    }

    /**
     * Sends a message to the server
     * @param message
     */
    private void sendMessage(String message) {
        if (webSocketClient != null && message != null) {
            webSocketClient.sendMessageToServer(message);
            Log.d(DEBUG_TAG, "Sent message: " + message);
        } else {
            Log.d(DEBUG_TAG, webSocketClient.isConnected()+" "+message);
            Log.e(DEBUG_TAG, "Failed to send message from JsonDataManager, WebSocketClient or message is null");
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

    public static <T> T parseJsonMessage(String json, Class<T> clazz) {
        Log.d(DEBUG_TAG, "parseJsonMessage: " + json);
        try {
            return MAPPER.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            Log.e(DEBUG_TAG, FAILURE_MESSAGE, e);
            return null;
        }
    }

    public static String createJsonMessage(Object object) {
        Log.d(DEBUG_TAG, "createJsonMessage: " + object);
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            Log.e(DEBUG_TAG, FAILURE_MESSAGE, e);
            return null;
        }
    }
}