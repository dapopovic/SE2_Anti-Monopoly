package at.aau.anti_mon.server.game;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

/**
 * Represents a User of the App
 * TODO: Rename User?
 */
@Getter
@Setter
public class Player {

    private final String name;
    private WebSocketSession session;
    private boolean isReady;

    public Player(String name, WebSocketSession session) {
        this.name = name;
        this.session = session;
        this.isReady = false;
    }
}
