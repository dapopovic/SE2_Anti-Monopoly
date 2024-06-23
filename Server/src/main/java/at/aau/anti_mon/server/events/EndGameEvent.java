package at.aau.anti_mon.server.events;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

/**
 * Event that is fired when a player ends the game
 */
@Getter
public class EndGameEvent extends BaseUserEvent{

    public EndGameEvent(WebSocketSession session, String username){
        super(session,username);
    }
}
