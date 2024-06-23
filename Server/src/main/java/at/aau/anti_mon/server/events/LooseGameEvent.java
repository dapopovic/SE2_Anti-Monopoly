package at.aau.anti_mon.server.events;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

/**
 * Event that is fired when a player looses the game
 */
@Getter
public class LooseGameEvent extends BaseUserEvent{
    public LooseGameEvent(WebSocketSession session, String username){
        super(session, username);
    }
}
