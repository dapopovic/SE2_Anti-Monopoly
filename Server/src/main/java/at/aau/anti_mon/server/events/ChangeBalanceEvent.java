package at.aau.anti_mon.server.events;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

public class ChangeBalanceEvent extends Event {
    @Getter
    private final String username;
    @Getter
    private final Integer new_balance;

    public ChangeBalanceEvent(WebSocketSession session, String username, Integer new_balance) {
        super(session);
        this.username = username;
        this.new_balance = new_balance;
    }
}
