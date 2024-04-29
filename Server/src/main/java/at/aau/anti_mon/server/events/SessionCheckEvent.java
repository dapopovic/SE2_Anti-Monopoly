package at.aau.anti_mon.server.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

/**
 * Event that is fired when a session is checked for still being active
 */
@Getter
@Setter
public class SessionCheckEvent extends Event{

    private final String userID;

    public SessionCheckEvent(WebSocketSession session, String userID) {
        super(session);
        this.userID = userID;
    }

}


