package at.aau.anti_mon.server.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

/**
 * Event that is fired when a player disconnects to the server
 */
@Getter
@Setter
public class SessionDisconnectEvent extends Event {

    public SessionDisconnectEvent( WebSocketSession session
    ) {
        super(session);
    }

}