package at.aau.anti_mon.server.events;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

@Getter
public class FirstPlayerEvent extends Event{

    private final String username;
    public FirstPlayerEvent(WebSocketSession session, String username){
        super(session);
        this.username = username;
        Logger.info("Wir sind in FirstPlayerEvent.");
    }
}
