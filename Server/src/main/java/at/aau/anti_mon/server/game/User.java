package at.aau.anti_mon.server.game;

import at.aau.anti_mon.server.enums.Roles;
import at.aau.anti_mon.server.enums.Figures;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.util.Random;

/**
 * Represents a User of the App
 */
@Getter
@Setter
public class User {

    private final String name;
    private WebSocketSession session;
    private Lobby lobby;
    private boolean isReady;
    private int money;
    private static Roles role;
    private Figures figure;
    private int location;
    private int sequence;

    public User(String name, WebSocketSession session) {
        this.name = name;
        this.session = session;
        this.isReady = false;
        this.lobby = null;
        this.money = 1500;
        assignRandomRole();
        this.figure = null;
        this.location = 1;
        this.sequence = 0;
    }

    public boolean isOwner() {
        if (lobby == null) {
            return false;
        }
        return lobby.getOwner().equals(this);
    }

    public void clear() {
        this.lobby = null;
        this.isReady = false;
        this.money = 1500;
        this.role = null;
        this.figure = null;
        this.location = 1;
        this.sequence = 0;
    }

    public void assignRandomRole() {
        Random random = new Random();
        Roles[] roles = Roles.values();
        this.role = roles[random.nextInt(roles.length)];
    }

}
