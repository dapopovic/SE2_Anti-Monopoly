package at.aau.anti_mon.server.events;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

/**
 * Event that is fired when a player is the first player
 */
@Getter
public class FirstPlayerEvent extends BaseUserEvent{

    public FirstPlayerEvent(WebSocketSession session, String username){
        super(session, username);
    }
}
