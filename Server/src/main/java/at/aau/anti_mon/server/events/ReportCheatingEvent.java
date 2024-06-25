package at.aau.anti_mon.server.events;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

public class ReportCheatingEvent extends Event {
    @Getter
    private final String username;
    @Getter
    private final String cheating_username;
    public ReportCheatingEvent(WebSocketSession session, String username, String cheating_username) {
        super(session);
        this.username = username;
        this.cheating_username = cheating_username;
    }
}
