package at.aau.anti_mon.server.game;

import jakarta.websocket.Session;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

@Getter
public class Player {
    private final String name;
    private final WebSocketSession session;

    @Setter
    private boolean isReady;

    public Player(String name, WebSocketSession session) {
        this.name = name;
        this.session = session;
        this.isReady = false;
    }
}
