package at.aau.anti_mon.client.json;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;
import android.util.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.networking.WebSocketClient;
import lombok.Getter;

/**
 * This class is responsible for creating and parsing JSON messages.
 * It uses the Jackson library to serialize and deserialize JSON messages.
 * The class is implemented as a singleton.
 */
@Getter
public class JsonDataManager {

    @Getter private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String FAILURE_MESSAGE = "Failed to create JSON message";

    //private static WebSocketClient webSocketClient;

    private JsonDataManager(){
    }

    // Todo:  Dies könnte durch Dagger injiziert werden, sobald WebSocketClient konfiguriert ist.
   /* public static void initialize(WebSocketClient client) {
        if (webSocketClient == null) {
            webSocketClient = client;
        } else {
            Log.d(DEBUG_TAG, "WebSocketClient is already initialized.");
        }
    }

    */


    /**
     * This class is used to send messages to the server.
     * Don't convert to record -> Android-Studio has problems with records
     */
    /*public static class MessageSender {
        private final String message;

        public MessageSender(String message) {
            this.message = message;
        }

        //@SneakyThrows
        public MessageSender(JsonDataDTO jsonDataDTO) {
                //message = MAPPER.writeValueAsString(jsonDataDTO);
            this.message = createJsonMessage(jsonDataDTO);
        }

        public void sendMessage() {
           // JsonDataManager.getInstance().sendMessage(message);
            JsonDataManager.sendMessage(this.message);
        }

        public String getMessage() {
            return message;
        }
    }

     */


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