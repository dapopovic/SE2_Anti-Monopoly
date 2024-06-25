package at.aau.anti_mon.server.events;

import org.springframework.web.socket.WebSocketSession;

public class PayRentEvent extends BaseUserEvent{


    protected PayRentEvent(WebSocketSession session, String username, Integer fieldId) {
        super(session, username);
    }
}
