package at.aau.anti_mon.server.events;

import at.aau.anti_mon.server.dtos.LobbyDTO;
import at.aau.anti_mon.server.dtos.UserDTO;
import org.springframework.web.socket.WebSocketSession;

public class UserStartedGameEvent extends Event{
    private final LobbyDTO lobbyDTO;
    private final UserDTO userDTO;

    public UserStartedGameEvent(WebSocketSession session, LobbyDTO lobby, UserDTO user) {
        super(session);
        this.lobbyDTO = lobby;
        this.userDTO = user;
    }

    public String getUsername(){
        return userDTO.getUsername();
    }
    public Integer getPin(){
        return lobbyDTO.getPin();
    }
}
