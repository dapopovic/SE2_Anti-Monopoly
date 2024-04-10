package at.aau.anti_mon.server.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

@Getter
@Setter
public class SessionDisconnectEvent {

    private final WebSocketSession session;

    public SessionDisconnectEvent(WebSocketSession session) {
        this.session = session;
    }

}