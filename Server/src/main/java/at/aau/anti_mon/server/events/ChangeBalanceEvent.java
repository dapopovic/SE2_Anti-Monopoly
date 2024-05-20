package at.aau.anti_mon.server.events;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

public class ChangeBalanceEvent extends Event {
    @Getter
    private final String username;
    @Getter
    private final Integer newBalance;

    public ChangeBalanceEvent(WebSocketSession session, String username, Integer newBalance) {
        super(session);
        this.username = username;
        this.newBalance = newBalance;
    }
}
