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

import java.util.ArrayList;
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
        session = client.execute(new WebSocketHandlerClientImpl(messages), String.format(WEBSOCKET_URI, port)).get(3, TimeUnit.SECONDS);
    }

    @Test
    void testServerWithNoCommand() throws Exception {
        String message = "{\"data\":{\"username\":\"Test\"}}";
        Logger.info("TEST - sending message: " + message);
        session.sendMessage(new TextMessage(message));

        String messageResponse = messages.poll(10, TimeUnit.SECONDS);  // Erhöhe Timeout für Sicherheit
        assertNotNull(messageResponse, "Response should not be null");
        Logger.info("TEST - received messageResponse: " + messageResponse);

        JsonDataDTO jsonData = JsonDataManager.parseJsonMessage(messageResponse);

        assertNotNull(jsonData, "jsonData should not be null");
        String receivedMessage = jsonData.getData().get("message");
        assertNotNull(receivedMessage, "message should not be null");
        assertEquals("JSON-Nachricht enthält kein 'command'-Attribut.", receivedMessage, "Message should be 'JSON-Nachricht enthält kein 'command'-Attribut.'");
    }
    @Test
    void testServerWithCommandDoesNotExist() throws Exception {
        String message = "{\"command\":\"CREATE\",\"data\":{\"username\":\"Test\"}}";
        Logger.info("TEST - sending message: " + message);
        session.sendMessage(new TextMessage(message));

        String messageResponse = messages.poll(10, TimeUnit.SECONDS);  // Erhöhe Timeout für Sicherheit
        assertNotNull(messageResponse, "Response should not be null");
        Logger.info("TEST - received messageResponse: " + messageResponse);

        JsonDataDTO jsonData = JsonDataManager.parseJsonMessage(messageResponse);

        assertNotNull(jsonData, "jsonData should not be null");
        String receivedMessage = jsonData.getData().get("message");
        assertNotNull(receivedMessage, "message should not be null");
        assertEquals("Fehler beim Parsen der JSON-Nachricht.", receivedMessage, "Message should be 'Fehler beim Parsen der JSON-Nachricht.'");
    }
    @Test
    void testServerWithCommandError() throws Exception {
        String message = "{\"command\":\"ERROR\",\"data\":{\"username\":\"Test\"}}";
        Logger.info("TEST - sending message: " + message);
        session.sendMessage(new TextMessage(message));

        String messageResponse = messages.poll(10, TimeUnit.SECONDS);  // Erhöhe Timeout für Sicherheit
        assertNotNull(messageResponse, "Response should not be null");
        Logger.info("TEST - received messageResponse: " + messageResponse);

        JsonDataDTO jsonData = JsonDataManager.parseJsonMessage(messageResponse);

        assertNotNull(jsonData, "jsonData should not be null");
        String receivedMessage = jsonData.getData().get("message");
        assertNotNull(receivedMessage, "message should not be null");
        assertEquals("Unbekannter oder nicht unterstützter Befehl: ERROR", receivedMessage, "Message should be 'SERVER : Unbekannter oder nicht unterstützter Befehl: ERROR'");
    }

    @Test
    void testNewCreateGameAndGetPin() throws Exception {
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
    }

    @Test
    void testCreateGameWithNoName() throws Exception {
        String message = "{\"command\":\"CREATE_GAME\",\"data\":{}}";
        Logger.info("TEST - sending message: " + message);
        session.sendMessage(new TextMessage(message));

        String messageResponse = messages.poll(10, TimeUnit.SECONDS);  // Erhöhe Timeout für Sicherheit
        assertNotNull(messageResponse, "Response should not be null");
        Logger.info("TEST - received messageResponse: " + messageResponse);

        JsonDataDTO jsonData = JsonDataManager.parseJsonMessage(messageResponse);

        assertNotNull(jsonData, "jsonData should not be null");
        String receivedMessage = jsonData.getData().get("message");
        assertNotNull(receivedMessage, "message should not be null");

        assertEquals("JSON 'data' ist null oder 'name' ist nicht vorhanden.", receivedMessage, "Message should be 'JSON 'data' ist null oder 'name' ist nicht vorhanden.'");
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

    @Test
    void testJoinGameWithWrongPin() throws Exception {

        // now join Lobby with wrong pin
        String message = "{\"command\":\"JOIN_GAME\",\"data\":{\"username\":\"Test\", \"pin\":\"1234\"}}";
        Logger.info("TEST - sending message: " + message);
        session.sendMessage(new TextMessage(message));

        String messageResponse = messages.poll(10, TimeUnit.SECONDS);  // Erhöhe Timeout für Sicherheit
        assertNotNull(messageResponse, "Response should not be null");
        Logger.info("TEST - received messageResponse: " + messageResponse);

        JsonDataDTO jsonData = JsonDataManager.parseJsonMessage(messageResponse);

        assertNotNull(jsonData, "jsonData should not be null");
        String receivedMessage = jsonData.getData().get("message");
        assertNotNull(receivedMessage, "message should not be null");
        assertEquals("Lobby mit PIN 1234 existiert nicht.", receivedMessage, "Message should be 'Lobby mit PIN 1234 existiert nicht.'");
    }

    @Test
    void testJoinGameLobbyFull() throws Exception {
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
        for (int i = 0; i < 6; i++) {
            message = "{\"command\":\"JOIN_GAME\",\"data\":{\"username\":\"Test" + i + "\",\"pin\":\"" + pin + "\"}}";
            Logger.info("TEST - sending message: " + message);
            session.sendMessage(new TextMessage(message));

            messageResponse = messages.poll(10, TimeUnit.SECONDS);  // Erhöhe Timeout für Sicherheit
            assertNotNull(messageResponse, "Response should not be null");
            Logger.info("TEST - received messageResponse: " + messageResponse);

            jsonData = JsonDataManager.parseJsonMessage(messageResponse);

            assertNotNull(jsonData, "jsonData should not be null");
            String receivedMessage = jsonData.getData().get("message");
            assertNotNull(receivedMessage, "message should not be null");
            if (i < 5) {
                assertEquals("Erfolgreich der Lobby beigetreten.", receivedMessage, "Message should be 'Erfolgreich der Lobby beigetreten.'");
            } else {
                assertEquals("Lobby ist voll!", receivedMessage, "Message should be 'Lobby ist voll!'");
            }
        }
    }

    @Test
    void testJoinGameWithNoPin() throws Exception {
        // now join Lobby with empty pin
        String message = "{\"command\":\"JOIN_GAME\",\"data\":{\"username\":\"Test\"}}";
        Logger.info("TEST - sending message: " + message);
        session.sendMessage(new TextMessage(message));

        String messageResponse = messages.poll(10, TimeUnit.SECONDS);  // Erhöhe Timeout für Sicherheit
        assertNotNull(messageResponse, "Response should not be null");
        Logger.info("TEST - received messageResponse: " + messageResponse);

        JsonDataDTO jsonData = JsonDataManager.parseJsonMessage(messageResponse);

        assertNotNull(jsonData, "jsonData should not be null");
        String receivedMessage = jsonData.getData().get("message");
        assertNotNull(receivedMessage, "message should not be null");
        assertEquals("Erforderliche Daten für 'JOIN_GAME' fehlen.", receivedMessage, "Message should be 'Erforderliche Daten für 'JOIN_GAME' fehlen.'");
    }


    @AfterEach
    public void tearDown() throws Exception {
        if (session != null) {
            session.close();
        }
    }

}