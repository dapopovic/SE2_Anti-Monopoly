package at.aau.anti_mon.server.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

/**
 * Event that is fired when a player connects to the server
 */
@Getter
public class SessionConnectEvent extends Event{

    public SessionConnectEvent(WebSocketSession session) {
        super(session);
    }
}