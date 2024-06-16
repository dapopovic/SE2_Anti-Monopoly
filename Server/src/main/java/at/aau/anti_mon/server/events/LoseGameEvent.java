package at.aau.anti_mon.server.events;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

@Getter
public class LoseGameEvent extends Event{
    private final String username;
    public LoseGameEvent(WebSocketSession session, String username){
        super(session);
        this.username = username;
    }
}
