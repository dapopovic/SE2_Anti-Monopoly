package at.aau.anti_mon.server.game;

import at.aau.anti_mon.server.enums.Roles;
import at.aau.anti_mon.server.enums.Figures;
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
    @Setter
    private int money;
    @Setter
    private Roles role;
    @Setter
    private Figures figure;
    @Setter
    private int location;
    @Setter
    private int sequence;
    @Setter
    private int unavailableRounds;
    @Setter
    private boolean hasPlayed;
    @Setter
    private boolean isCheating;

    public User(String name, WebSocketSession session) {
        this.name = name;
        this.session = session;
        this.isReady = false;
        this.lobby = null;
        this.money = 1500;
        this.role = null;
        this.figure = null;
        this.location = 1;
        this.sequence = 0;
        this.unavailableRounds = 0;
        this.hasPlayed = false;
        this.isCheating = false;
    }

    public boolean isOwner() {
        if (lobby == null) {
            return false;
        }
        return lobby.getOwner().equals(this);
    }
}
