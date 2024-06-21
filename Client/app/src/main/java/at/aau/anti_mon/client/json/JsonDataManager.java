package at.aau.anti_mon.client.json;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;
import android.util.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;

/**
 * This class is responsible for creating and parsing JSON messages.
 * It uses the Jackson library to serialize and deserialize JSON messages.
 * The class is implemented as a singleton.
 */
@Getter
public class JsonDataManager {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String FAILURE_MESSAGE = "Failed to create JSON message";

    private JsonDataManager(){
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