package at.aau.anti_mon.server.events;

import org.springframework.web.socket.WebSocketSession;

public class CardActionEvent extends BaseUserEvent{

    protected CardActionEvent(WebSocketSession session, String username) {
        super(session, username);
    }

}
