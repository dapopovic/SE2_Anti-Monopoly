package at.aau.anti_mon.server.events;


import at.aau.anti_mon.server.dtos.GameSessionDTO;
import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.game.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

/**
 * Event that is fired when a player creates a lobby
 */
@Getter
@Setter
public class CreateLobbyEvent {

    private final WebSocketSession session;
    private final UserDTO userDTO;

    public CreateLobbyEvent(WebSocketSession session, UserDTO user) {
        this.session = session;
        this.userDTO = user;
    }


    public String getUsername(){
        return userDTO.getUsername();
    }

    public String getUserSessionID(){
        return session.getId();
    }

}

