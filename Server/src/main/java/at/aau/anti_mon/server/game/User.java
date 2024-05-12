package at.aau.anti_mon.server.game;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

/**
 * Represents a User of the App
 */
@Getter
public class User {

    private final String name;
    private WebSocketSession session;
    private Lobby lobby;
    private boolean isReady;
    private boolean role;               // 0-Monopolist, 1-Wettbewerber
    private boolean onTurn;             // 0-Spieler ist nicht am Zug, 1-Spielr ist am Zug

    public User(String name, WebSocketSession session) {
        this.name = name;
        this.session = session;
        this.isReady = false;
        this.lobby = null;
    }

    public void setRole(boolean role) {
        this.role = role;
    }

    public void setOnTurn(boolean onTurn) {
        this.onTurn = onTurn;
    }

    public boolean isRole() {
        return role;
    }

    public boolean isOnTurn() {
        return onTurn;
    }
}
