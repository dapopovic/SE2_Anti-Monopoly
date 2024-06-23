package at.aau.anti_mon.server.events;

import at.aau.anti_mon.server.dtos.LobbyDTO;
import at.aau.anti_mon.server.dtos.UserDTO;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

/**
 * Event that is fired when a player leaves a lobby
 */
@Getter
public class UserLeftLobbyEvent extends BaseUserEvent{

    Integer lobbyPIN;

    public UserLeftLobbyEvent(WebSocketSession session, Integer lobby, String user) {
        super(session, user);
        this.lobbyPIN = lobby;
    }

}