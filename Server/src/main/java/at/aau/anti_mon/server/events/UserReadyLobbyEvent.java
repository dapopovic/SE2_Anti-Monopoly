package at.aau.anti_mon.server.events;

import at.aau.anti_mon.server.dtos.LobbyDTO;
import at.aau.anti_mon.server.dtos.UserDTO;
import org.springframework.web.socket.WebSocketSession;

/**
 * Event that is fired when a player is ready in a lobby
 */
public class UserReadyLobbyEvent extends Event {

        private final LobbyDTO lobbyDTO;
        private final UserDTO userDTO;

        public UserReadyLobbyEvent(WebSocketSession session, LobbyDTO lobby, UserDTO user) {
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
