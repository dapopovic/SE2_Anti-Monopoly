package at.aau.anti_mon.client.unittests;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import at.aau.anti_mon.client.enums.Commands;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;

/**
 * Unit tests for the JsonDataManager
 */
class JsonDataManagerTest {

    @Test
    void createStringFromJsonMessageShouldReturnValidJson() {
        Map<String, String> data = new HashMap<>();
        data.put("key", "value");
        String json = JsonDataManager.createJsonMessage(Commands.NEW_USER, data);
        JsonDataDTO jsonDataDTO = JsonDataManager.parseJsonMessage(json, JsonDataDTO.class);
        assertNotNull(jsonDataDTO);
        assertEquals(Commands.NEW_USER, jsonDataDTO.getCommand());
        assertEquals("value", jsonDataDTO.getData().get("key"));
    }

    @Test
    void parseJsonMessageShouldReturnValidJsonDataDTO() {
        String json = "{\"command\":\"NEW_USER\",\"data\":{\"datafield\":\"message\"}}";
        JsonDataDTO jsonDataDTO = JsonDataManager.parseJsonMessage(json, JsonDataDTO.class);
        assertNotNull(jsonDataDTO);
        assertEquals(Commands.NEW_USER, jsonDataDTO.getCommand());
        assertEquals("message", jsonDataDTO.getData().get("datafield"));
    }

    @Test
    void createStringFromJsonMessageWithJsonDataDTOShouldReturnValidJson() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO(Commands.NEW_USER, new HashMap<>());
        jsonDataDTO.putData("datafield", "message");
        String json = JsonDataManager.createJsonMessage(jsonDataDTO);
        JsonDataDTO parsedJsonDataDTO = JsonDataManager.parseJsonMessage(json, JsonDataDTO.class);
        assertNotNull(parsedJsonDataDTO);
        assertEquals(Commands.NEW_USER, parsedJsonDataDTO.getCommand());
        assertEquals("message", parsedJsonDataDTO.getData().get("datafield"));
    }

    @Test
    void createStringFromJsonMessageUsingAnObjectShouldReturnValidValue() {
        User user = new User("username", true, true);
        User user2 = new User("username2", false, false);
        User[] users = {user, user2};
        String json = JsonDataManager.createJsonMessage(users);
        ObjectMapper mapper = new ObjectMapper();
        users = assertDoesNotThrow(() -> mapper.readValue(json, User[].class));
        assertEquals(users[0], user);
        assertEquals(users[1], user2);
    }

    @Test
    void createStringFromJsonMessageUsingAnObjectShouldReturnNull() {
        Object object = mock(Object.class);
        when(object.toString()).thenReturn("mocked object");
        String json = JsonDataManager.createJsonMessage(object);
        assertNull(json);
    }

    @Test
    void parseJsonMessageShouldReturnNull() {
        String json = "{\"command\":\"NEW_USER\",\"data\":{\"datafield\"s:\"message\"}}";
        JsonDataDTO jsonDataDTO = JsonDataManager.parseJsonMessage(json, JsonDataDTO.class);
        assertNull(jsonDataDTO);
    }
}