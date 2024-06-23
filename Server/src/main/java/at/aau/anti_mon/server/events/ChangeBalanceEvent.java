package at.aau.anti_mon.server.events;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

/**
 * Event that is fired when a player changes his balance
 */
@Getter
public class ChangeBalanceEvent extends BaseUserEvent {

    private final Integer newBalance;

    public ChangeBalanceEvent(WebSocketSession session, String username, Integer newBalance) {
        super(session,username);
        this.newBalance = newBalance;
    }
}
