package at.aau.anti_mon.server.game;

import at.aau.anti_mon.server.enums.GameState;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * Represents a lobby in the game
 */
@Getter
@Setter
public class Lobby {

    private final Integer pin;
    private final ArrayList<Player> players;
    private static final int MAX_PLAYERS = 6;
    private final GameState gameState;

    public Lobby() {
        SecureRandom random = new SecureRandom();
        this.pin = random.nextInt(9000) + 1000;
        this.players = new ArrayList<>();
        this.gameState = GameState.LOBBY;
    }

    public void addPlayer(Player player) {
        if (players.size() >= MAX_PLAYERS) {
            return;
        }
        for (Player p : players) {
            if (p.equals(player)) {
                return;
            }
        }
        if (!player.getSession().isOpen()) {
            return;
        }
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public Player getPlayerWithSession(WebSocketSession session) {
        return players.stream().filter(player -> player.getSession().getId().equals(session.getId())).findFirst().orElse(null);
    }

    public boolean canAddPlayer() {
        return this.players.size() < MAX_PLAYERS;
    }
}
