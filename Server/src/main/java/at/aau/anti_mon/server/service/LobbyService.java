package at.aau.anti_mon.server.service;

import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.game.JsonDataDTO;
import at.aau.anti_mon.server.game.Lobby;
import at.aau.anti_mon.server.game.Player;
import at.aau.anti_mon.server.websocket.manager.JsonDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Service-Class for handling Lobby-Operations
 */
@Service
public class LobbyService {

    private final SimpMessagingTemplate messagingTemplate;

    private final Map<Integer, Lobby> lobbies = new ConcurrentHashMap<>();

    @Autowired
    public LobbyService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Erstellt eine neue Lobby und fügt den Spieler hinzu
     * @param player Spieler
     */
    public Lobby createLobby(Player player) {
        Lobby newLobby = new Lobby();
        Lobby existing = lobbies.putIfAbsent(newLobby.getPin(), newLobby);
        if (existing != null) {
            throw new IllegalStateException("Lobby mit PIN " + newLobby.getPin() + " existiert bereits.");
        }
        newLobby.addPlayer(player);
        return newLobby;
    }

    public void joinLobby(Integer lobbyPin, Player player) {
        Lobby lobby = lobbies.get(lobbyPin);
        if (lobby != null) {
            lobby.removePlayer(player);
        }
    }

    public void leaveLobby(Integer lobbyPin, Player player) {
        Lobby lobby = lobbies.get(lobbyPin);
        if (lobby != null) {
            lobby.removePlayer(player);
        }
    }

    /**
     * Erstellt eine Liste mit Spielern in der Lobby und sendet diese an alle Spieler in der Lobby
     * @param lobby Lobby
     * @throws Exception wenn das Senden der Nachricht fehlschlägt
     */
    public void notifyPlayersInLobby(Lobby lobby){
        List<String> playerNames = lobby.getPlayers().stream()
                .map(Player::getName)
                .toList();

        // TODO List to JSON -> LOBBY_PLAYERS Command

        for (Player player : lobby.getPlayers()) {
            if (player.getSession().isOpen()) {
                JsonDataManager.sendJoinedUser(player.getSession(), player.getName());
            }
        }
    }



    /**
     * Durchsuche die Liste der Lobbies nach der gegebenen PIN und gib die entsprechende Lobby zurück.
     * @param pin PIN der Lobby
     * @return Lobby oder null, wenn keine Lobby mit der gegebenen PIN gefunden wurde
     */
    public Optional<Lobby> findLobbyByPin(int pin) {
        for (Lobby lobby : lobbies.values()) {
            if (lobby.getPin() == pin) {
                return Optional.of(lobby);
            }
        }
        return Optional.empty();
    }

    /**
     * Konvertiert Nachrichtenobjekt in JSON und sendet es an alle Clients im Lobby-Channel
     * @param lobbyId ID der Lobby
     * @param message Nachrichtenobjekt
     */
    public void sendToLobby(String lobbyId, Object message) {
        String destination = "/topic/lobby." + lobbyId;
        messagingTemplate.convertAndSend(destination, message);
    }

    /**
     * Konvertiert Nachrichtenobjekt in JSON und sendet es an einem bestimmten User
     * @param message Nachrichtenobjekt
     */
    public void sendToUser(String username, Object message) {
        String destination = "/user/" + username + "/queue/notifications";
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", message);
    }
}