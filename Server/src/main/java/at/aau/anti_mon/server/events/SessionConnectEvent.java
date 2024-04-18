package at.aau.anti_mon.server.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

/**
 * Event that is fired when a player connects to the server

 */
@Getter
@Setter
public class SessionConnectEvent {

    private final WebSocketSession session;

    public SessionConnectEvent(WebSocketSession session) {
        this.session = session;
    }

}