package at.aau.anti_mon.server.events;

import org.springframework.web.socket.WebSocketSession;

public class BuyPropertyEvent extends BaseUserEvent{
    protected BuyPropertyEvent(WebSocketSession session, String username) {
        super(session, username);
    }
}
