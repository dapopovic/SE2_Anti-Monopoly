package at.aau.anti_mon.server.service;

import at.aau.anti_mon.server.game.Lobby;
import at.aau.anti_mon.server.game.Player;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


/**
 * Service-Klasse für die Verwaltung von Lobbies
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

    /**
    public void joinLobby(String lobbyPin, Player player) {
        Lobby lobby = lobbies.get(lobbyPin);
        if (lobby == null) {
            throw new IllegalStateException("Lobby mit PIN " + lobbyPin + " existiert nicht.");
        }
        if (lobby.canAddPlayer()) {
            lobby.addPlayer(player);
        } else {
            throw new IllegalStateException("Lobby mit PIN " + lobbyPin + " ist voll.");
        }
    }
     */


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
    public void notifyPlayersInLobby(Lobby lobby) throws Exception {
        List<String> playerNames = lobby.getPlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        String message = new Gson().toJson(playerNames);
        for (Player player : lobby.getPlayers()) {
            if (player.getSession().isOpen()) {
                player.getSession().sendMessage(new TextMessage(message));
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