package at.aau.anti_mon.server.events;


import at.aau.anti_mon.server.dtos.LobbyDTO;
import at.aau.anti_mon.server.dtos.UserDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

/**
 * Event that is fired when a player joins a lobby
 */
@Getter
@Setter
public class UserJoinedLobbyEvent {


    private final LobbyDTO lobbyDTO;
    private final UserDTO userDTO;
    private final WebSocketSession session;

    public UserJoinedLobbyEvent(WebSocketSession session, LobbyDTO lobby, UserDTO user) {
        this.session = session;
        this.lobbyDTO = lobby;
        this.userDTO = user;
    }

    public String getUsername(){
        return userDTO.getUsername();
    }

    public String getUserSessionID(){
        return session.getId();
    }

    public Integer getPin(){
        return lobbyDTO.getPin();
    }


}

