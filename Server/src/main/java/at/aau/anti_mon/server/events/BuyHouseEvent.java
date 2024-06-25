package at.aau.anti_mon.server.events;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

@Getter
public class BuyHouseEvent extends BaseUserEvent {

    private final Integer fieldId;

    public BuyHouseEvent(WebSocketSession session, String username, Integer fieldId) {
        super(session, username);
        this.fieldId = fieldId;
    }

}
