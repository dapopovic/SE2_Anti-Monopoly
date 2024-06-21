package at.aau.anti_mon.server.events;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

@Getter
public class EndGameEvent extends Event{

    private final String username;
    public EndGameEvent(WebSocketSession session, String username){
        super(session);
        this.username = username;
        Logger.info("Wir sind in EndGameEvent.");
    }
}
