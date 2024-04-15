package at.aau.anti_mon.server.listener;

import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.events.CreateLobbyEvent;
import at.aau.anti_mon.server.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.server.events.UserLeftLobbyEvent;
import at.aau.anti_mon.server.game.JsonDataDTO;
import at.aau.anti_mon.server.game.Lobby;
import at.aau.anti_mon.server.service.LobbyService;
import at.aau.anti_mon.server.websocket.manager.JsonDataManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Event-Listener for user interactions
 */
@Component
public class UserEventListener {

    /**
     * Lobby service
     */
    private final LobbyService lobbyService;

    /**
     * TODO: TEST
     */
    private final Lock lock = new ReentrantLock();

    /**
     * Konstruktor für UserEventListener
     * Dependency Injection für LobbyService
     * @param lobbyService
     */
    @Autowired
    UserEventListener(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    /**
     * Logik zum Erstellen einer Lobby
     * Der PIN der Lobby wird an den Benutzer zurückgegeben
     * @param event Ereignis
     */
    @EventListener
    public void onCreateLobby(CreateLobbyEvent event) throws JsonProcessingException {
        Lobby newLobby = lobbyService.createLobby(event.getPlayer());
        //sendResponse(event.getSession(), "Spiel erstellt mit PIN: " + newLobby.getPin());
        Logger.info("Spiel erstellt mit PIN: " + newLobby.getPin());
        sendPin(event.getSession(), String.valueOf(newLobby.getPin()));
    }

    /**
     * Fügt den Benutzer zur Lobby hinzu
     * @param event Ereignis
     */
    @EventListener
    public void onJoinLobby(UserJoinedLobbyEvent event) throws Exception {
        Optional<Lobby> lobby = lobbyService.findLobbyByPin(event.getPin());
        if (lobby.isPresent()) {
            Lobby joinedLobby = lobby.get();
            if (joinedLobby.canAddPlayer()) {
                joinedLobby.addPlayer(event.getPlayer());
                lobbyService.notifyPlayersInLobby(joinedLobby);
                sendInfo(event.getSession(), "Erfolgreich der Lobby beigetreten.");
            } else {
                sendError(event.getSession(), "Fehler: Lobby ist voll.");
            }
        } else {
            sendError(event.getSession(), "Fehler: Lobby nicht gefunden.");
        }
    }

    /**
     * Entfernt den Benutzer aus der Lobby
     * @param event Ereignis
     */
    @EventListener
    public void onLeaveLobby(UserLeftLobbyEvent event) {
        lobbyService.leaveLobby(event.getLobby().getPin(), event.getPlayer());
    }


    /** TODO: TEST TEST TEST
     * Sendet eine Nachricht an den Benutzer
     * @param session WebSocket-Sitzung
     * @param data Nachricht
     */
    public void updateSessionSafely(WebSocketSession session, String data) throws IOException {
        lock.lock();
        try {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(data));
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Sendet eine Nachricht an den Benutzer
     * @param session WebSocket-Sitzung
     * @param message Nachricht
     */
    private void sendAnswer(WebSocketSession session, String message) throws JsonProcessingException {
        JsonDataDTO jsonData = new JsonDataDTO(Commands.ANSWER, new HashMap<>());
        send(session, message, jsonData);
    }

    /**
     * Sendet eine Nachricht an den Benutzer
     * @param session WebSocket-Sitzung
     * @param message Nachricht
     */
    private void sendPin(WebSocketSession session, String message) {
        JsonDataDTO jsonData = new JsonDataDTO(Commands.PIN, new HashMap<>());
        jsonData.putData("pin", message);
        send(session, message, jsonData);
    }


    private void sendJoinedUser(WebSocketSession session, String message) {
        JsonDataDTO jsonData = new JsonDataDTO(Commands.JOIN, new HashMap<>());
        jsonData.putData("name", message);
        send(session, message, jsonData);
    }

    private void sendError(WebSocketSession session, String message) {
        JsonDataDTO jsonData = new JsonDataDTO(Commands.ERROR, new HashMap<>());
        jsonData.putData("message", message);
        send(session, message, jsonData);
    }

    private void sendInfo(WebSocketSession session, String message) {
        JsonDataDTO jsonData = new JsonDataDTO(Commands.ERROR, new HashMap<>());
        jsonData.putData("message", message);
        send(session, message, jsonData);
    }




    private void send(WebSocketSession session, String message, JsonDataDTO jsonData) {
        String jsonResponse =   JsonDataManager.createJsonMessage(jsonData);
        try {
            synchronized (session) {
                if (session.isOpen()) {
                    Logger.info("Nachricht senden: " + jsonResponse);
                    session.sendMessage(new TextMessage(jsonResponse));
                } else {
                    System.err.println("Versuch, eine Nachricht zu senden, aber die Session ist bereits geschlossen.");
                    throw new IOException("Session is closed");
                }
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Senden der Nachricht: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // OLD CODE

    /*

    @EventListener
    public void handleUserJoinedLobbyEvent(UserJoinedLobbyEvent event) {
        lobbyService.addUserToLobby(event.getPlayer().getName(), String.valueOf(event.getLobby().getPin()));
    }

    @EventListener
    public void handleUserLeftLobbyEvent(UserLeftLobbyEvent event) {
        lobbyService.removeUserFromLobby(event.getUserId(), event.getLobbyId());
    }


     */



}