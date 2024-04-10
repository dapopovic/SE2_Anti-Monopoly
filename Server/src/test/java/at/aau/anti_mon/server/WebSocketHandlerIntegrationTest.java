package at.aau.anti_mon.server;

import at.aau.anti_mon.server.websocket.WebSocketHandlerClientImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.tinylog.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketHandlerIntegrationTest {

    @LocalServerPort
    private int port;

    private BlockingQueue<String> messages;
    private WebSocketSession session;

    @BeforeEach
    public void setup() throws Exception {
        messages = new LinkedBlockingQueue<>();
        WebSocketClient client = new StandardWebSocketClient();
        String WEBSOCKET_URI = "ws://localhost:%d/game";
        session = client.execute(new WebSocketHandlerClientImpl(messages ), String.format(WEBSOCKET_URI, port)).get(1, TimeUnit.SECONDS);
    }

    @Test
    public void testCreateGameAndGetPin() throws Exception {
        String message = "{\"command\":\"CREATE_GAME\",\"data\":{\"name\":\"Test\"}}";
        Logger.info("TEST - sending message: " + message);
        session.sendMessage(new TextMessage(message));

        String messageResponse = messages.poll(10, TimeUnit.SECONDS);  // Erhöhe Timeout für Sicherheit
        Assertions.assertNotNull(messageResponse, "Response should not be null");
        Logger.info("TEST - received messageResponse: " + messageResponse);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(messageResponse);
        String pin = rootNode.path("pin").asText();  // Sicherstellen, dass 'pin' existiert und ein String ist
        Logger.debug("Extracted PIN: " + pin);

        Assertions.assertFalse(pin.isEmpty(), "PIN should not be empty");
        Assertions.assertTrue(pin.matches("\\d{4}"), "PIN should be a 4-digit number");
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (session != null) {
            session.close();
        }
    }

}