package at.aau.anti_mon.server.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GameSessionDTO {

    private String sessionId;
    private UserDTO userDetails;
    private LobbyDTO lobbyDetails;

    public GameSessionDTO(String sessionId, UserDTO userDetails){
        this.sessionId = sessionId;
        this.userDetails = userDetails;
    }

}
