package at.aau.anti_mon.server.events;


import org.springframework.web.socket.WebSocketSession;

import at.aau.anti_mon.server.dtos.UserDTO;

/**
 * Event that is fired when a player creates a lobby
 */
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

