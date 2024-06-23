package at.aau.anti_mon.server.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

/**
 * Event that is fired when a session is checked for still being active
 */
@Getter
public class SessionCheckEvent extends BaseUserEvent{
    public SessionCheckEvent(WebSocketSession session, String userID) {
        super(session, userID);
    }
}


