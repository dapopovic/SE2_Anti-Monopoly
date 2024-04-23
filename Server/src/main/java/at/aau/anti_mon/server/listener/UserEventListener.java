package at.aau.anti_mon.server.listener;

import at.aau.anti_mon.server.events.CreateLobbyEvent;
import at.aau.anti_mon.server.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.server.events.UserLeftLobbyEvent;
import at.aau.anti_mon.server.exceptions.NotConnectedException;
import at.aau.anti_mon.server.game.Lobby;
import at.aau.anti_mon.server.service.LobbyService;
import at.aau.anti_mon.server.websocket.manager.JsonDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

import java.io.IOException;
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
    public void onCreateLobby(CreateLobbyEvent event) {
        try {
            Lobby newLobby = lobbyService.createLobby(event.getPlayer());
            Logger.info("Spiel erstellt mit PIN: " + newLobby.getPin());
            JsonDataManager.sendPin(event.getSession(), String.valueOf(newLobby.getPin()));
        } catch (NotConnectedException e) {
            Logger.error("Fehler beim Erstellen der Lobby: " + e.getMessage());
        } catch (Exception e) {
            Logger.error("Fehler beim Erstellen der Lobby: " + e.getMessage());
            JsonDataManager.sendError(event.getSession(), e.getMessage());
        }
    }

    /**
     * Fügt den Benutzer zur Lobby hinzu
     * @param event Ereignis
     */
    @EventListener
    public void onJoinLobby(UserJoinedLobbyEvent event) {
        try {
            lobbyService.joinLobby(event.getPin(), event.getPlayer());
            JsonDataManager.sendAnswer(event.getSession(), "SUCCESS");
            JsonDataManager.sendInfo(event.getSession(), "Erfolgreich der Lobby beigetreten.");
        } catch (NotConnectedException e) {
            Logger.error("Fehler beim Beitreten der Lobby: " + e.getMessage());
        } catch (Exception e) {
            Logger.error("Fehler beim Beitreten der Lobby: " + e.getMessage());
            JsonDataManager.sendError(event.getSession(), e.getMessage());
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

}