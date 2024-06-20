package at.aau.anti_mon.server.unittests;

import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.dtos.JsonDataDTO;
import at.aau.anti_mon.server.utilities.JsonDataUtility;
import at.aau.anti_mon.server.utilities.MessagingUtility;
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
        JsonDataDTO jsonDataDTO = JsonDataUtility.parseJsonMessage(json, JsonDataDTO.class);
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
        JsonDataDTO jsonDataDTO = JsonDataUtility.parseJsonMessage(json, JsonDataDTO.class);
        assertEquals(Commands.NEW_USER, jsonDataDTO.getCommand());
        assertEquals("message", jsonDataDTO.getData().get("datafield"));
    }

    @Test
    void createStringFromJsonMessageWithJsonDataDTOShouldReturnValidJson() throws JsonProcessingException {
        JsonDataDTO jsonDataDTO = new JsonDataDTO(Commands.NEW_USER, new HashMap<>());
        jsonDataDTO.putData("datafield", "message");
        String json = JsonDataUtility.createStringFromJsonMessage(jsonDataDTO);
        JsonDataDTO parsedJsonDataDTO = JsonDataUtility.parseJsonMessage(json, JsonDataDTO.class);
        assertEquals(Commands.NEW_USER, parsedJsonDataDTO.getCommand());
        assertEquals("message", parsedJsonDataDTO.getData().get("datafield"));
    }

    @Test
    public void testParseJsonMessageWithRandomDice() throws JsonProcessingException {
        String json = "{\"command\": \"RANDOM_DICE\"}";
        String json2 = MessagingUtility.createGameMessage("Testuser", 2, Commands.RANDOM_DICE).getMessage();

        JsonDataDTO result = JsonDataUtility.parseJsonMessage(json, JsonDataDTO.class);
        JsonDataDTO result2 = JsonDataUtility.parseJsonMessage(json2, JsonDataDTO.class);

        assertNotNull(result);
        assertNotNull(result2);

        assertEquals(Commands.RANDOM_DICE, result.getCommand());
        assertEquals(Commands.RANDOM_DICE, result2.getCommand());
    }

    @Test
    public void testJsonMessagesAreEqual() throws JsonProcessingException {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.DICE);
        jsonData.putData("dicenumber", "2");
        jsonData.putData("username", "testUser");

        String json1 = JsonDataUtility.createStringFromJsonMessage(jsonData);
        String json2 = MessagingUtility.createGameMessage("testUser", 2, Commands.DICE).getMessage();

        assertEquals(json1, json2);

        JsonDataDTO jsonData2 = JsonDataUtility.parseJsonMessage(json1, JsonDataDTO.class);
        JsonDataDTO jsonData3 = JsonDataUtility.parseJsonMessage(json2, JsonDataDTO.class);

        assertEquals(jsonData2.getCommand(), jsonData3.getCommand());
        assertEquals(jsonData2.getData().get("dicenumber"), jsonData3.getData().get("dicenumber"));
        assertEquals(jsonData2.getData().get("username"), jsonData3.getData().get("username"));

        String jsonData2_toString = JsonDataUtility.createStringFromJsonMessage(jsonData2);
        String jsonData3_toString = JsonDataUtility.createStringFromJsonMessage(jsonData3);

        assertEquals(jsonData2_toString, jsonData3_toString);
    }

}