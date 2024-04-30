package at.aau.anti_mon.client.json;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

import at.aau.anti_mon.client.command.Commands;

/**
 * This class is responsible for creating and parsing JSON messages.
 */
public class JsonDataManager {

    public static String  createJsonMessage(Commands command, Map<String, String> data) {
        Log.d("ANTI-MONOPOLY-DEBUG", "createJsonMessage: " + command + " " + data);
        try{
            JsonDataDTO jsonDataDTO = new JsonDataDTO(command, data);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(jsonDataDTO);
        } catch (JsonProcessingException e) {
            Log.e("ANTI-MONOPOLY-DEBUG", "Failed to create JSON message", e);
            e.printStackTrace();
            return null;
        }
    }

    public static JsonDataDTO parseJsonMessage(String json) {
        Log.d("ANTI-MONOPOLY-DEBUG", "parseJsonMessage: " + json);
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, JsonDataDTO.class);
        } catch (JsonProcessingException e) {
            Log.e("ANTI-MONOPOLY-DEBUG", "Failed to parse JSON message", e);
            e.printStackTrace();
            return null;
        }
    }

    public static String createJsonMessage( JsonDataDTO jsonData)  {
        Log.d("ANTI-MONOPOLY-DEBUG", "createJsonMessage: " + jsonData);
        try{
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(jsonData);
        } catch (JsonProcessingException e) {
            Log.e("ANTI-MONOPOLY-DEBUG", "Failed to create JSON message", e);
            e.printStackTrace();
            return null;
        }
    }


}