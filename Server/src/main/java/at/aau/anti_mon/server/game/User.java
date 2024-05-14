package at.aau.anti_mon.server.game;

import at.aau.anti_mon.server.enums.Figures;
import at.aau.anti_mon.server.enums.GameState;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

/**
 * Represents a User of the App
 */
@Getter
public class User {

    private final String name;
    private WebSocketSession session;
    @Setter
    private Lobby lobby;
    @Setter
    private boolean isReady;
    private int money;
    @Setter
    private Figures figure;
    @Setter
    private Integer location;

    public User(String name, WebSocketSession session) {
        this.name = name;
        this.session = session;
        this.isReady = false;
        this.lobby = null;
        this.money = 1500;
        this.figure = null;
        this.location = 0;
    }

    public boolean isOwner() {
        if (lobby == null) {
            return false;
        }
        return lobby.getOwner().equals(this);
    }
}
