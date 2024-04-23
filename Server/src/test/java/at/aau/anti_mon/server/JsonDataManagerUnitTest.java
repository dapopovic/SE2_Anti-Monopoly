package at.aau.anti_mon.server;

import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.game.JsonDataDTO;
import at.aau.anti_mon.server.websocket.manager.JsonDataManager;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonDataManagerUnitTest {

    @Test
    void testCreateJsonDataDTOWithMap() {
        Map<String, String> data = new HashMap<>();
        data.put("username", "test");
        String json = assertDoesNotThrow(() -> JsonDataManager.createJsonMessage(Commands.JOIN, data));
        assertNotNull(json);
        assertEquals("{\"command\":\"JOIN\",\"username\":\"test\"}", json);
    }
    @Test
    void testCreateJsonDataDTO() {
        JsonDataDTO json = assertDoesNotThrow(() -> JsonDataManager.createJsonDataDTO(Commands.JOIN, "test", "username"));
        assertNotNull(json);
        String data = json.getData().get("username");
        assertEquals("test", data);
        Commands command = json.getCommand();
        assertEquals(Commands.JOIN, command);
    }
}
