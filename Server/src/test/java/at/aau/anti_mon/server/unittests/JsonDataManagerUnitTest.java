package at.aau.anti_mon.server.unittests;

import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.game.JsonDataDTO;
import at.aau.anti_mon.server.websocket.manager.JsonDataManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for the JsonDataManager
 */
public class JsonDataManagerUnitTest {

    @Test
    public void createStringFromJsonMessageShouldReturnValidJson() throws JsonProcessingException {
        Map<String, String> data = new HashMap<>();
        data.put("key", "value");
        String json = JsonDataManager.createStringFromJsonMessage(Commands.NEW_USER, data);
        JsonDataDTO jsonDataDTO = JsonDataManager.parseJsonMessage(json);
        assertEquals(Commands.NEW_USER, jsonDataDTO.getCommand());
        assertEquals("value", jsonDataDTO.getData().get("key"));
    }

    @Test
    public void createJsonDataDTOShouldReturnValidJsonDataDTO() {
        JsonDataDTO jsonDataDTO = JsonDataManager.createJsonDataDTO(Commands.NEW_USER, "message", "datafield");
        assertEquals(Commands.NEW_USER, jsonDataDTO.getCommand());
        assertEquals("message", jsonDataDTO.getData().get("datafield"));
    }

    @Test
    public void parseJsonMessageShouldReturnValidJsonDataDTO() throws JsonProcessingException {
        String json = "{\"command\":\"NEW_USER\",\"data\":{\"datafield\":\"message\"}}";
        JsonDataDTO jsonDataDTO = JsonDataManager.parseJsonMessage(json);
        assertEquals(Commands.NEW_USER, jsonDataDTO.getCommand());
        assertEquals("message", jsonDataDTO.getData().get("datafield"));
    }

    @Test
    public void createStringFromJsonMessageWithJsonDataDTOShouldReturnValidJson() throws JsonProcessingException {
        JsonDataDTO jsonDataDTO = new JsonDataDTO(Commands.NEW_USER, new HashMap<>());
        jsonDataDTO.putData("datafield", "message");
        String json = JsonDataManager.createStringFromJsonMessage(jsonDataDTO);
        JsonDataDTO parsedJsonDataDTO = JsonDataManager.parseJsonMessage(json);
        assertEquals(Commands.NEW_USER, parsedJsonDataDTO.getCommand());
        assertEquals("message", parsedJsonDataDTO.getData().get("datafield"));
    }
}