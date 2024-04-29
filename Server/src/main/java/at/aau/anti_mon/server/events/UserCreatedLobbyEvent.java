package at.aau.anti_mon.server.events;


import at.aau.anti_mon.server.dtos.UserDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

/**
 * Event that is fired when a player creates a lobby
 */
@Getter
@Setter
public class UserCreatedLobbyEvent extends Event {

    private final UserDTO userDTO;

    public UserCreatedLobbyEvent(WebSocketSession session, UserDTO user) {
        super(session);
        this.userDTO = user;
    }


    public String getUsername(){
        return userDTO.getUsername();
    }



}

