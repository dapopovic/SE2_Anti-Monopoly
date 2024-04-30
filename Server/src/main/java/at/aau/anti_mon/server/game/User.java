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

    public User(String name, WebSocketSession session) {
        this.name = name;
        this.session = session;
        this.isReady = false;
        this.lobby = null;
    }
}
