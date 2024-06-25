package at.aau.anti_mon.client.utilities;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import at.aau.anti_mon.client.enums.Commands;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.json.JsonDataDTO;

/**
 * This class is responsible for creating and parsing JSON messages.
 */
public class JsonDataUtility {
    private JsonDataUtility() {
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String FAILURE_MESSAGE = "Failed to create JSON message";

    public static String createJsonMessage(Commands command, Map<String, String> data) {
        Log.d(DEBUG_TAG, "createJsonMessage: " + command + " " + data);
        try {
            JsonDataDTO jsonDataDTO = new JsonDataDTO(command, data);
            return MAPPER.writeValueAsString(jsonDataDTO);
        } catch (JsonProcessingException e) {
            Log.e(DEBUG_TAG, FAILURE_MESSAGE, e);
            return null;
        }
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
        Log.d(DEBUG_TAG, "createJsonStringFromObject: " + object);
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            Log.e(DEBUG_TAG, FAILURE_MESSAGE, e);
            return null;
        }
    }

    public static List<User> parseUserList(String json) {
        try {
            return MAPPER.readValue(json, new TypeReference<>() {
            });
        } catch (IOException e) {
            Log.e(DEBUG_TAG, FAILURE_MESSAGE, e);
            return null;
        }
    }
}