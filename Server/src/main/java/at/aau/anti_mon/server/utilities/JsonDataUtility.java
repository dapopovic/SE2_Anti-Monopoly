package at.aau.anti_mon.server.utilities;

import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.enums.Figures;
import at.aau.anti_mon.server.dtos.JsonDataDTO;
import at.aau.anti_mon.server.game.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * This class is a utility and responsible for creating and parsing JSON messages.
 */
public class JsonDataUtility {

    private JsonDataUtility() {
    }

    public static String createStringFromJsonMessage(Commands command, Map<String, String> data) {
        try {
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

    public static <T> T parseJsonMessage(String json, Class<T> clazz) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, clazz);
    }

    public static String createStringFromJsonMessage(Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(object);
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


    public static void sendJoinedUser(WebSocketSession session, User user) {
        JsonDataDTO jsonData = createJsonDataDTO(Commands.NEW_USER, user.getName(), "username");
        jsonData.putData("isOwner", String.valueOf(user.isOwner()));
        jsonData.putData("isReady", String.valueOf(user.isReady()));
        send(session, jsonData);
    }

    public static void sendReadyUser(WebSocketSession session, String message, boolean isReady) {
        JsonDataDTO jsonData = createJsonDataDTO(Commands.READY, message, "username");
        jsonData.putData("isReady", String.valueOf(isReady));
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
     *
     * @param session WebSocket-Sitzung
     * @param message Nachricht
     */
    public static void sendAnswer(WebSocketSession session, String message) {
        JsonDataDTO jsonData = createJsonDataDTO(Commands.ANSWER, message, "answer");
        send(session, jsonData);
    }

    /**
     * Sendet eine Nachricht an den Benutzer
     *
     * @param session WebSocket-Sitzung
     * @param message Nachricht
     */
    public static void sendPin(WebSocketSession session, String message) {
        JsonDataDTO jsonData = createJsonDataDTO(Commands.PIN, message, "pin");
        send(session, jsonData);
    }


    public static void sendStartGame(WebSocketSession sessionForUser, Collection<UserDTO> users) {
        JsonDataDTO jsonData = new JsonDataDTO(Commands.START_GAME, new HashMap<>());
        // create a list of all users in the lobby
        StringBuilder usersString = new StringBuilder();
        for (UserDTO user : users) {
            usersString.append(JsonDataUtility.createStringFromJsonMessage(user)).append(",");
        }
        usersString.deleteCharAt(usersString.length() - 1);
        jsonData.putData("users", "[" + usersString + "]");
        send(sessionForUser, jsonData);
    }

    public static void sendDiceNumber(WebSocketSession session, String username, Integer dicenumber, Figures figure, Integer location){
        JsonDataDTO jsonData = new JsonDataDTO(Commands.DICENUMBER, new HashMap<>());
        jsonData.putData("username",username);
        jsonData.putData("dicenumber", String.valueOf(dicenumber));
        jsonData.putData("figure", String.valueOf(figure));
        jsonData.putData("location", String.valueOf(location));

        send(session,jsonData);
    }
}
