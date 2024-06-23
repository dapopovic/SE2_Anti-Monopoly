package at.aau.anti_mon.server.integrationtests;

import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.dtos.JsonDataDTO;
import at.aau.anti_mon.server.utilities.MessagingUtility;
import at.aau.anti_mon.server.websocketclient.WebSocketHandlerClientImpl;
import at.aau.anti_mon.server.utilities.JsonDataUtility;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
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

/**
 * Integration test for the WebSocketHandler.
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "logging.level.org.springframework=DEBUG" })
class WebSocketHandlerIntegrationTest {

    @LocalServerPort
    private int port;

    private BlockingQueue<String> messages;
    private WebSocketSession session;

    @BeforeEach
    void setup() throws Exception {
        messages = new LinkedBlockingQueue<>();
        WebSocketClient client = new StandardWebSocketClient();
        String BASE_WEBSOCKET_URI = "ws://localhost:%d/game?userID=%s";

        String userID = "Test";

        session = client
                .execute(new WebSocketHandlerClientImpl(messages), String.format(BASE_WEBSOCKET_URI, port, userID))//String.format(BASE_WEBSOCKET_URI + userID, port))
                .get(3, TimeUnit.SECONDS);
    }

    @Test
    void testCreateGameAndGetPin() throws Exception {
        //String message = "{\"command\":\"CREATE_GAME\",\"data\":{\"username\":\"Test\"}}";
        String message = MessagingUtility.createUsernameMessage("Test", Commands.CREATE_GAME).getMessage();

        Logger.info("TEST - sending message: " + message);
        session.sendMessage(new TextMessage(message));

        String messageResponse = messages.poll(10, TimeUnit.SECONDS);
        Assertions.assertNotNull(messageResponse, "Response should not be null");
        Logger.info("TEST - received messageResponse: " + messageResponse);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(messageResponse);
        String pin = rootNode.path("pin").asText();
        Logger.debug("Extracted PIN: " + pin);

        Assertions.assertFalse(pin.isEmpty(), "PIN should not be empty");
        Assertions.assertTrue(pin.matches("\\d{4}"), "PIN should be a 4-digit number");
    }

    @Test
    void testNewCreateGameAndGetPin() throws Exception {
        // Beispiel: Verwendung der JsonDataDTO Klasse

        JsonDataDTO jsonData = new JsonDataDTO(Commands.CREATE_GAME, new HashMap<>());
        jsonData.putData("username", "Test");

        String jsonMessage = JsonDataUtility.createStringFromJsonMessage(jsonData);

        // Senden des serialisierten JSON-Strings über eine WebSocket-Session
        assert jsonMessage != null;
        session.sendMessage(new TextMessage(jsonMessage));

        String messageResponse = messages.poll(10, TimeUnit.SECONDS); // Erhöhe Timeout für Sicherheit
        Assertions.assertNotNull(messageResponse, "Response should not be null");
        Logger.info("TEST - received messageResponse: " + messageResponse);

        JsonDataDTO receivedData = JsonDataUtility.parseJsonMessage(messageResponse, JsonDataDTO.class);

        // Zugriff auf die Daten
        assert receivedData != null;
        Commands command = receivedData.getCommand();
        Map<String, String> data = receivedData.getData();
        String pin = data.get("pin");

        Logger.info("Received command: " + command);
        Logger.info("Received name: " + pin);

        Assertions.assertFalse(pin.isEmpty(), "PIN should not be empty");
        Assertions.assertTrue(pin.matches("\\d{4}"), "PIN should be a 4-digit number");
    }

    @AfterEach
    void tearDown() throws Exception {
        if (session != null) {
            session.close();
        }
    }

}