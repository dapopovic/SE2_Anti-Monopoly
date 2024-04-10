package at.aau.anti_mon.server.events;


import at.aau.anti_mon.server.game.Lobby;
import at.aau.anti_mon.server.game.Player;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

@Getter
@Setter
public class UserJoinedLobbyEvent {

    private final Lobby lobby;
    private final Player player;
    private final WebSocketSession session;


    public UserJoinedLobbyEvent(WebSocketSession session, Lobby lobby, Player player) {
        this.session = session;
        this.lobby = lobby;
        this.player = player;
    }

    public int getPin(){
        return lobby.getPin();
    }

}

