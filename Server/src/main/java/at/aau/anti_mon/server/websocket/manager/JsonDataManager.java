package at.aau.anti_mon.server.websocket.manager;

import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.game.JsonDataDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * This class is a utility and responsible for creating and parsing JSON messages.
 */
public class JsonDataManager {

    public static String  createJsonMessage(Commands command, Map<String, String> data) throws JsonProcessingException {
        JsonDataDTO jsonDataDTO = new JsonDataDTO(command, data);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(jsonDataDTO);
    }

    public static JsonDataDTO createJsonDataDTO (Commands commands, String message, String datafield) {
        JsonDataDTO jsonData = new JsonDataDTO(commands, new HashMap<>());
        jsonData.putData(datafield, message);
        return jsonData;
    }

    public static JsonDataDTO parseJsonMessage(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, JsonDataDTO.class);
    }

    public static String createJsonMessage( JsonDataDTO jsonData) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(jsonData);
    }

    public static void send(WebSocketSession session, JsonDataDTO jsonData) {
        try {
            String jsonResponse = createJsonMessage(jsonData);
            if (session.isOpen()) {
                Logger.info("Nachricht senden: " + jsonResponse);
                session.sendMessage(new TextMessage(jsonResponse));
            } else {
                Logger.error("Versuch, eine Nachricht zu senden, aber die Session ist bereits geschlossen.");
                throw new IOException("Session is closed");
            }
        } catch (IOException e) {
            Logger.error("Fehler beim Senden der Nachricht: " + e.getMessage());
        }
    }


    public static void sendJoinedUser(WebSocketSession session, String message) {
        JsonDataDTO jsonData = createJsonDataDTO(Commands.JOIN, message, "username");
        send(session, jsonData);
    }

    public static void sendError(WebSocketSession session, String message) {
        JsonDataDTO jsonData = createJsonDataDTO(Commands.ERROR, message, "message");
        send(session, jsonData);
    }

    public static void sendInfo(WebSocketSession session, String message) {
        JsonDataDTO jsonData = createJsonDataDTO(Commands.INFO, message, "message");
        send(session, jsonData);
    }



    /**
     * Sendet eine Nachricht an den Benutzer
     * @param session WebSocket-Sitzung
     * @param message Nachricht
     */
    public static void sendAnswer(WebSocketSession session, String message) throws JsonProcessingException {
        JsonDataDTO jsonData = createJsonDataDTO(Commands.ANSWER, message, "answer");
        send(session, jsonData);
    }

    /**
     * Sendet eine Nachricht an den Benutzer
     * @param session WebSocket-Sitzung
     * @param message Nachricht
     */
    public static void sendPin(WebSocketSession session, String message) {
        JsonDataDTO jsonData = createJsonDataDTO(Commands.PIN, message, "pin");
        send(session, jsonData);
    }


}
