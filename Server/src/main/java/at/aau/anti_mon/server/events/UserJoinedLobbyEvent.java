package at.aau.anti_mon.server.events;


import at.aau.anti_mon.server.game.Player;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

/**
 * Event that is fired when a player joins a lobby

 */
@Getter
@Setter
public class UserJoinedLobbyEvent {

    private final int pin;
    private final Player player;
    private final WebSocketSession session;


    public UserJoinedLobbyEvent(WebSocketSession session, int pin, Player player) {
        this.session = session;
        this.pin = pin;
        this.player = player;
    }

}

