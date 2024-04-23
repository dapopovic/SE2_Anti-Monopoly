package at.aau.anti_mon.server;

import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.game.JsonDataDTO;
import at.aau.anti_mon.server.websocket.WebSocketHandlerClientImpl;
import at.aau.anti_mon.server.websocket.manager.JsonDataManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.tinylog.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"logging.level.org.springframework=DEBUG"})
class WebSocketHandlerIntegrationTest {

    @LocalServerPort
    private int port;

    private BlockingQueue<String> messages;
    private WebSocketSession session;

    @BeforeEach
    public void setup() throws Exception {
        messages = new LinkedBlockingQueue<>();
        WebSocketClient client = new StandardWebSocketClient();
        String WEBSOCKET_URI = "ws://localhost:%d/game";
        session = client.execute(new WebSocketHandlerClientImpl(messages ), String.format(WEBSOCKET_URI, port)).get(3, TimeUnit.SECONDS);
    }

    @Test
    void testNewCreateGameAndGetPin() throws Exception {
        JsonDataDTO jsonData = new JsonDataDTO(Commands.CREATE_GAME, new HashMap<>());
        jsonData.putData("username", "Test");

        String jsonMessage =  JsonDataManager.createJsonMessage(jsonData);

        // Senden des serialisierten JSON-Strings über eine WebSocket-Session
        assert jsonMessage != null;
        session.sendMessage(new TextMessage(jsonMessage));

        String messageResponse = messages.poll(10, TimeUnit.SECONDS);  // Erhöhe Timeout für Sicherheit
        assertNotNull(messageResponse, "Response should not be null");
        Logger.info("TEST - received messageResponse: " + messageResponse);

        JsonDataDTO receivedData = JsonDataManager.parseJsonMessage(messageResponse);

        assertNotNull(receivedData, "receivedData should not be null");
        Commands command = receivedData.getCommand();
        Map<String, String> data = receivedData.getData();
        String pin = data.get("pin");

        Logger.info("Received command: " + command);
        Logger.info("Received name: " + pin);

        Assertions.assertFalse(pin.isEmpty(), "PIN should not be empty");
        assertTrue(pin.matches("\\d{4}"), "PIN should be a 4-digit number");
    }

    @Test
    void testJoinGame() throws Exception {
        // first create Lobby
        String message = "{\"command\":\"CREATE_GAME\",\"data\":{\"username\":\"Test\"}}";
        Logger.info("TEST - sending message: " + message);
        session.sendMessage(new TextMessage(message));

        String messageResponse = messages.poll(10, TimeUnit.SECONDS);  // Erhöhe Timeout für Sicherheit
        assertNotNull(messageResponse, "Response should not be null");
        Logger.info("TEST - received messageResponse: " + messageResponse);

        JsonDataDTO jsonData = JsonDataManager.parseJsonMessage(messageResponse);

        assertNotNull(jsonData, "jsonData should not be null");
        String pin = jsonData.getData().get("pin");
        assertNotNull(pin, "pin should not be null");
        assertTrue(pin.matches("\\d{4}"), "PIN should be a 4-digit number");

        // now join Lobby
        message = "{\"command\":\"JOIN_GAME\",\"data\":{\"username\":\"Test2\",\"pin\":\"" + pin + "\"}}";
        Logger.info("TEST - sending message: " + message);
        session.sendMessage(new TextMessage(message));

        messageResponse = messages.poll(10, TimeUnit.SECONDS);  // Erhöhe Timeout für Sicherheit
        assertNotNull(messageResponse, "Response should not be null");
        Logger.info("TEST - received messageResponse: " + messageResponse);

        jsonData = JsonDataManager.parseJsonMessage(messageResponse);

        assertNotNull(jsonData, "jsonData should not be null");
        String receivedMessage = jsonData.getData().get("message");
        assertNotNull(receivedMessage, "message should not be null");
        assertEquals("Erfolgreich der Lobby beigetreten.", receivedMessage, "Message should be 'Erfolgreich der Lobby beigetreten.'");
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (session != null) {
            session.close();
        }
    }

}