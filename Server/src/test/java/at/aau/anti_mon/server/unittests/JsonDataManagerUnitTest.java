package at.aau.anti_mon.server.unittests;

import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.game.JsonDataDTO;
import at.aau.anti_mon.server.utilities.JsonDataUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the JsonDataManager
 */
class JsonDataManagerUnitTest {

    @Test
    void createStringFromJsonMessageShouldReturnValidJson() throws JsonProcessingException {
        Map<String, String> data = new HashMap<>();
        data.put("key", "value");
        String json = JsonDataUtility.createStringFromJsonMessage(Commands.NEW_USER, data);
        JsonDataDTO jsonDataDTO = JsonDataUtility.parseJsonMessage(json);
        assertEquals(Commands.NEW_USER, jsonDataDTO.getCommand());
        assertEquals("value", jsonDataDTO.getData().get("key"));
    }

    @Test
    void createJsonDataDTOShouldReturnValidJsonDataDTO() {
        JsonDataDTO jsonDataDTO = JsonDataUtility.createJsonDataDTO(Commands.NEW_USER, "message", "datafield");
        assertEquals(Commands.NEW_USER, jsonDataDTO.getCommand());
        assertEquals("message", jsonDataDTO.getData().get("datafield"));
    }

    @Test
    void parseJsonMessageShouldReturnValidJsonDataDTO() throws JsonProcessingException {
        String json = "{\"command\":\"NEW_USER\",\"data\":{\"datafield\":\"message\"}}";
        JsonDataDTO jsonDataDTO = JsonDataUtility.parseJsonMessage(json);
        assertEquals(Commands.NEW_USER, jsonDataDTO.getCommand());
        assertEquals("message", jsonDataDTO.getData().get("datafield"));
    }

    @Test
    void createStringFromJsonMessageWithJsonDataDTOShouldReturnValidJson() throws JsonProcessingException {
        JsonDataDTO jsonDataDTO = new JsonDataDTO(Commands.NEW_USER, new HashMap<>());
        jsonDataDTO.putData("datafield", "message");
        String json = JsonDataUtility.createStringFromJsonMessage(jsonDataDTO);
        JsonDataDTO parsedJsonDataDTO = JsonDataUtility.parseJsonMessage(json);
        assertEquals(Commands.NEW_USER, parsedJsonDataDTO.getCommand());
        assertEquals("message", parsedJsonDataDTO.getData().get("datafield"));
    }
}