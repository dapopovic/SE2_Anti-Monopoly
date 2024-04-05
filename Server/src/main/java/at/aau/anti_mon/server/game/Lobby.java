package at.aau.anti_mon.server.game;

import lombok.Getter;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@Getter
public class Lobby {
    private final int pin;
    private final ArrayList<Player> players;
    private static final int MAX_PLAYERS = 6;
    private final GameState gameState;

    public Lobby() {
        this.pin = (int) (Math.random() * 10000);
        this.players = new ArrayList<>();
        this.gameState = new GameState();
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
}
