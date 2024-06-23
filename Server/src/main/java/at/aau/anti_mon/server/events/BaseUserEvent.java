package at.aau.anti_mon.server.events;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

@Getter
public abstract class BaseUserEvent extends Event {
    private final String username;

    protected BaseUserEvent(WebSocketSession session, String username) {
        super(session);
        this.username = username;
    }

}
