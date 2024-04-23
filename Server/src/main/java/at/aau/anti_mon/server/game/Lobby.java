package at.aau.anti_mon.server.game;

import at.aau.anti_mon.server.enums.GameState;
import at.aau.anti_mon.server.exceptions.LobbyIsFullException;
import at.aau.anti_mon.server.exceptions.NotConnectedException;
import at.aau.anti_mon.server.exceptions.PlayerAlreadyInLobbyException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.security.SecureRandom;
import java.util.HashSet;

/**
 * Represents a lobby in the game
 */
@Getter
@Setter
public class Lobby {

    private final Integer pin;
    private final HashSet<Player> players;
    private static final int MAX_PLAYERS = 6;
    private final GameState gameState;

    public Lobby() {
        SecureRandom random = new SecureRandom();
        this.pin = random.nextInt(9000) + 1000;
        this.players = new HashSet<>();
        this.gameState = GameState.LOBBY;
    }

    public void addPlayer(Player player) throws LobbyIsFullException, PlayerAlreadyInLobbyException, NotConnectedException {
        if (players.size() >= MAX_PLAYERS) {
            throw new LobbyIsFullException();
        }
        if (players.contains(player)) {
            throw new PlayerAlreadyInLobbyException();
        }
        if (!player.getSession().isOpen()) {
            throw new NotConnectedException();
        }
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public Player getPlayerWithSession(WebSocketSession session) {
        return players.stream().filter(player -> player.getSession().getId().equals(session.getId())).findFirst().orElse(null);
    }
}
