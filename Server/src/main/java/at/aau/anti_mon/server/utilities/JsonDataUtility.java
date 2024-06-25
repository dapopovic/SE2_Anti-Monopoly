package at.aau.anti_mon.server.utilities;

import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.enums.Figures;
import at.aau.anti_mon.server.dtos.JsonDataDTO;
import at.aau.anti_mon.server.game.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
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

    static String usernamestring = "username";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonDataUtility() {
    }

    public static String createStringFromJsonMessage(Commands command, Map<String, String> data) {
        try {
            JsonDataDTO jsonDataDTO = new JsonDataDTO(command, data);
            return MAPPER.writeValueAsString(jsonDataDTO);
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
        Logger.debug("SERVER: JSON-Nachricht empfangen - parse : " + json);
        try {
            T result = MAPPER.readValue(json, clazz);
            Logger.debug("SERVER: JSON erfolgreich geparst: " + result);
            return result;
        } catch (InvalidFormatException e) {
            Logger.error("Fehler beim Parsen der JSON-Nachricht: Ungültiges Format - " + e.getMessage());
            throw e;
        } catch (JsonProcessingException e) {
            Logger.error("Fehler beim Parsen der JSON-Nachricht: " + e.getMessage());
            throw e;
        }
    }

    public static String createStringFromJsonMessage(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
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
                    Logger.error("SERVER: Versuch, eine Nachricht zu senden, aber die Session ist bereits geschlossen.");
                    throw new IOException("Session is closed");
                }
            }
        } catch (IOException e) {
            Logger.error("Fehler beim Senden der Nachricht: " + e.getMessage());
        }
    }


    public static void sendJoinedUser(WebSocketSession session, User user) {
        JsonDataDTO jsonData = createJsonDataDTO(Commands.NEW_USER, user.getUserName(), usernamestring);
        jsonData.putData("isOwner", String.valueOf(user.isOwner()));
        jsonData.putData("isReady", String.valueOf(user.isReady()));
        send(session, jsonData);
    }

    public static void sendReadyUser(WebSocketSession session, String message, boolean isReady) {
        JsonDataDTO jsonData = createJsonDataDTO(Commands.READY, message, usernamestring);
        jsonData.putData("isReady", String.valueOf(isReady));
        send(session, jsonData);
    }

    public static void sendLeavedUser(WebSocketSession session, String message) {
        JsonDataDTO jsonData = createJsonDataDTO(Commands.LEAVE_GAME, message, usernamestring);
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
        jsonData.putData(usernamestring,username);
        jsonData.putData("dicenumber", String.valueOf(dicenumber));
        jsonData.putData("figure", String.valueOf(figure));
        jsonData.putData("location", String.valueOf(location));

        send(session,jsonData);
    }

    public static void sendNewBalance(WebSocketSession session, String username, Integer newBalance) {
        JsonDataDTO jsonData = new JsonDataDTO(Commands.CHANGE_BALANCE, new HashMap<>());
        jsonData.putData(usernamestring,username);
        jsonData.putData("new_balance", String.valueOf(newBalance));
        send(session, jsonData);
    }

    public static void sendCheating(WebSocketSession session) {
        JsonDataDTO jsonData = new JsonDataDTO(Commands.CHEATING, new HashMap<>());
        send(session, jsonData);
    }

    public static void sendNextPlayer(WebSocketSession session, String username){
        JsonDataDTO jsonData = new JsonDataDTO(Commands.NEXT_PLAYER, new HashMap<>());
        jsonData.putData(usernamestring,username);
        send(session,jsonData);
    }
    public static void sendFirstPlayer(WebSocketSession session, String username){
        JsonDataDTO jsonData = new JsonDataDTO(Commands.NEXT_PLAYER, new HashMap<>());
        jsonData.putData(usernamestring,username);
        Logger.info("Wir senden den First Player:"+username);
        send(session,jsonData);
    }
    public static void sendLoseGame(WebSocketSession session, String username){
        JsonDataDTO jsonData = new JsonDataDTO(Commands.LOSE_GAME, new HashMap<>());
        jsonData.putData(usernamestring,username);
        Logger.info("Wir senden den Spieler der verloren hat:"+username);
        send(session,jsonData);
    }
    public static void sendWinGame(WebSocketSession session, String username){
        JsonDataDTO jsonData = new JsonDataDTO(Commands.WIN_GAME, new HashMap<>());
        jsonData.putData(usernamestring,username);
        Logger.info("Wir senden den Spieler der verloren hat:"+username);
        send(session,jsonData);
    }
    public static void sendEndGame(WebSocketSession session, int rank){
        JsonDataDTO jsonData = new JsonDataDTO(Commands.END_GAME, new HashMap<>());
        jsonData.putData("rank", String.valueOf(rank));
        send(session,jsonData);
    }
}
