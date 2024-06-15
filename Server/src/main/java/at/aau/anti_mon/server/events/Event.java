package at.aau.anti_mon.server.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

@Getter
@Setter
public abstract class Event {

    private final WebSocketSession session;

    protected Event(WebSocketSession session) {
        this.session = session;
    }

    public String getUserSessionID(){
        return session.getId();
    }

}
