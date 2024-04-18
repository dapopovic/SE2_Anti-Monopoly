package at.aau.anti_mon.server.websocket.manager;

import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.game.JsonDataDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;


/**
 * This class is responsible for creating and parsing JSON messages.
 */
public class JsonDataManager {

    public static String  createJsonMessage(Commands command, Map<String, String> data) {
        try{
            JsonDataDTO jsonDataDTO = new JsonDataDTO(command, data);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(jsonDataDTO);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JsonDataDTO parseJsonMessage(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, JsonDataDTO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String createJsonMessage( JsonDataDTO jsonData)  {
        try{
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(jsonData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }


}
