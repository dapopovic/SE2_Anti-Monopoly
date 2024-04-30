package at.aau.anti_mon.client.unittests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;

/**
 * Unit tests for the JsonDataManager
 */
class JsonDataManagerUnitTest {

    @Test
    void createStringFromJsonMessageShouldReturnValidJson() throws JsonProcessingException {
        Map<String, String> data = new HashMap<>();
        data.put("key", "value");
        String json = JsonDataManager.createJsonMessage(Commands.NEW_USER, data);
        JsonDataDTO jsonDataDTO = JsonDataManager.parseJsonMessage(json);
        assertNotNull(jsonDataDTO);
        assertEquals(Commands.NEW_USER, jsonDataDTO.getCommand());
        assertEquals("value", jsonDataDTO.getData().get("key"));
    }

    @Test
    void parseJsonMessageShouldReturnValidJsonDataDTO() throws JsonProcessingException {
        String json = "{\"command\":\"NEW_USER\",\"data\":{\"datafield\":\"message\"}}";
        JsonDataDTO jsonDataDTO = JsonDataManager.parseJsonMessage(json);
        assertNotNull(jsonDataDTO);
        assertEquals(Commands.NEW_USER, jsonDataDTO.getCommand());
        assertEquals("message", jsonDataDTO.getData().get("datafield"));
    }

    @Test
    void createStringFromJsonMessageWithJsonDataDTOShouldReturnValidJson() throws JsonProcessingException {
        JsonDataDTO jsonDataDTO = new JsonDataDTO(Commands.NEW_USER, new HashMap<>());
        jsonDataDTO.putData("datafield", "message");
        String json = JsonDataManager.createJsonMessage(jsonDataDTO);
        JsonDataDTO parsedJsonDataDTO = JsonDataManager.parseJsonMessage(json);
        assertNotNull(parsedJsonDataDTO);
        assertEquals(Commands.NEW_USER, parsedJsonDataDTO.getCommand());
        assertEquals("message", parsedJsonDataDTO.getData().get("datafield"));
    }
}