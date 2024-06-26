package at.aau.anti_mon.server.events;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

public class ReportCheatingEvent extends Event {
    @Getter
    private final String username;
    @Getter
    private final String cheatingUsername;
    public ReportCheatingEvent(WebSocketSession session, String username, String cheatingUsername) {
        super(session);
        this.username = username;
        this.cheatingUsername = cheatingUsername;
    }
}
