package at.aau.anti_mon.server.events;


import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

import at.aau.anti_mon.server.dtos.UserDTO;

/**
 * Event that is fired when a player creates a lobby
 */
@Getter
public class UserCreatedLobbyEvent extends BaseUserEvent {

    public UserCreatedLobbyEvent(WebSocketSession session, String userName) {
        super(session,userName);
    }
}

