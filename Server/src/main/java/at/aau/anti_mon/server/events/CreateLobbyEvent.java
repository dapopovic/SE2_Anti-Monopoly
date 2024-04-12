package at.aau.anti_mon.server.events;


import at.aau.anti_mon.server.game.Player;
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
    private final Player player;

    public CreateLobbyEvent(WebSocketSession session, Player player) {
        this.session = session;
        this.player = player;
    }
}

