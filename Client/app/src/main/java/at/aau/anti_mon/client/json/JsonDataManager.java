package at.aau.anti_mon.client.json;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import at.aau.anti_mon.client.command.Commands;

/**
 * This class is responsible for creating and parsing JSON messages.
 */
public class JsonDataManager {
    private JsonDataManager() {
    }

    private static final String FAILURE_MESSAGE = "Failed to create JSON message";

    public static String createJsonMessage(Commands command, Map<String, String> data) {
        Log.d(DEBUG_TAG, "createJsonMessage: " + command + " " + data);
        try {
            JsonDataDTO jsonDataDTO = new JsonDataDTO(command, data);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(jsonDataDTO);
        } catch (JsonProcessingException e) {
            Log.e(DEBUG_TAG, FAILURE_MESSAGE, e);
            return null;
        }
    }

    public static <T> T parseJsonMessage(String json, Class<T> clazz) {
        Log.d(DEBUG_TAG, "parseJsonMessage: " + json);
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            Log.e(DEBUG_TAG, FAILURE_MESSAGE, e);
            return null;
        }
    }

    public static String createJsonMessage(Object object) {
        Log.d(DEBUG_TAG, "createJsonStringFromObject: " + object);
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            Log.e(DEBUG_TAG, FAILURE_MESSAGE, e);
            return null;
        }
    }
}