package at.aau.anti_mon.server.events;

import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

/**
 * Event that is fired when a player joins a lobby
 */
@Getter
public class UserJoinedLobbyEvent extends BaseUserEvent {

    private final Integer lobbyPIN;

    public UserJoinedLobbyEvent(WebSocketSession session, Integer lobbyPIN, String userName) {
        super(session, userName);
        this.lobbyPIN = lobbyPIN;
    }
}

