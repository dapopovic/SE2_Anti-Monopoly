package at.aau.anti_mon.server.listener;

import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.events.CreateLobbyEvent;
import at.aau.anti_mon.server.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.server.events.UserLeftLobbyEvent;
import at.aau.anti_mon.server.game.JsonDataDTO;
import at.aau.anti_mon.server.game.Lobby;
import at.aau.anti_mon.server.game.Player;
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
import java.util.HashSet;
import java.util.List;
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
        Logger.info("Spiel erstellt mit PIN: " + newLobby.getPin());
        JsonDataManager.sendPin(event.getSession(), String.valueOf(newLobby.getPin()));
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

                HashSet<Player> players = joinedLobby.getPlayers();
                for (Player player : players) {

                    // Sende allen Spielern in der Lobby die Information, dass ein neuer Spieler beigetreten ist
                    JsonDataManager.sendJoinedUser(player.getSession(), event.getPlayer().getName());

                    // Sende dem neuen Spieler alle Spieler, die bereits in der Lobby sind
                    JsonDataManager.sendJoinedUser(event.getSession(), player.getName());
                }

                joinedLobby.addPlayer(event.getPlayer());


                // TODO: something like this
                //lobbyService.notifyPlayersInLobby(joinedLobby);


                // TODO: TEST
                JsonDataManager.sendAnswer(event.getSession(), "SUCCESS");
                JsonDataManager.sendInfo(event.getSession(), "Erfolgreich der Lobby beigetreten.");
            } else {
                JsonDataManager.sendError(event.getSession(), "Fehler: Lobby ist voll.");
            }
        } else {
            JsonDataManager.sendError(event.getSession(), "Fehler: Lobby nicht gefunden.");
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