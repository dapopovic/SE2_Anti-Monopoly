package at.aau.anti_mon.server.events;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

/**
 * Event that is fired when a player leaves a lobby
 */
@Getter
public class UserLeftLobbyEvent extends Event{

    String username;
    Integer pin;

    public UserLeftLobbyEvent(WebSocketSession session, Integer pin, String username) {
        super(session);
        this.pin = pin;
        this.username = username;
    }
}