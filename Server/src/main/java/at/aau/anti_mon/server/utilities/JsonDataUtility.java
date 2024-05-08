package at.aau.anti_mon.server.utilities;

import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.dtos.JsonDataDTO;
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
public class JsonDataUtility {

    public static String createStringFromJsonMessage(Commands command, Map<String, String> data) {
        try{
            JsonDataDTO jsonDataDTO = new JsonDataDTO(command, data);
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(jsonDataDTO);
        } catch (JsonProcessingException e) {
            Logger.error("Fehler beim Erstellen der JSON-Nachricht: " + e.getMessage());
            return null;
        }
    }

    public static JsonDataDTO createJsonDataDTO(Commands commands, String message, String datafield) {
        JsonDataDTO jsonData = new JsonDataDTO(commands, new HashMap<>());
        jsonData.putData(datafield, message);
        return jsonData;
    }

    public static JsonDataDTO parseJsonMessage(String json) throws JsonProcessingException {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, JsonDataDTO.class);
    }

    public static String createStringFromJsonMessage(JsonDataDTO jsonData)  {
        try{
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(jsonData);
        } catch (JsonProcessingException e) {
            Logger.error("Fehler beim Erstellen der JSON-Nachricht: " + e.getMessage());
            return null;
        }
    }

    public static void send(WebSocketSession session, JsonDataDTO jsonData) {
        String jsonResponse = createStringFromJsonMessage(jsonData);
        if (jsonResponse == null) {
            Logger.error("Fehler beim Senden der Nachricht: jsonResponse ist null");
            return;
        }

        try {
            synchronized (session) {
                if (session.isOpen()) {
                    Logger.info("SERVER: Nachricht senden: " + jsonResponse);
                    session.sendMessage(new TextMessage(jsonResponse));
                } else {
                    System.err.println("SERVER: Versuch, eine Nachricht zu senden, aber die Session ist bereits geschlossen.");
                    throw new IOException("Session is closed");
                }
            }
        } catch (IOException e) {
            Logger.error("Fehler beim Senden der Nachricht: " + e.getMessage());
        }
    }


    public static void sendJoinedUser(WebSocketSession session, String message) {
        JsonDataDTO jsonData = createJsonDataDTO(Commands.NEW_USER, message, "username");
        send(session, jsonData);
    }

    public static void sendLeavedUser(WebSocketSession session, String message) {
        JsonDataDTO jsonData = createJsonDataDTO(Commands.LEAVE_GAME, message, "username");
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
    public static void sendAnswer(WebSocketSession session, String message) {
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
