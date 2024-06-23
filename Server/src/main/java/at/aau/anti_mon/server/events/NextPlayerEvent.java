package at.aau.anti_mon.server.events;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

/**
 * Event that is fired when the next player is on turn
 */
@Getter
public class NextPlayerEvent extends BaseUserEvent{
    public NextPlayerEvent(WebSocketSession session, String username){
        super(session, username);
    }
}
