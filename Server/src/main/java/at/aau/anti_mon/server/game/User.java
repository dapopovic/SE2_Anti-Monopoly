package at.aau.anti_mon.server.game;

import at.aau.anti_mon.server.enums.Roles;
import at.aau.anti_mon.server.enums.Figures;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.util.Objects;
import java.util.Random;

/**
 * Represents a User of the App
 */
@Getter
@Setter
public class User {

    private final String userName;
    private WebSocketSession session;
    private Lobby lobby;
    private boolean isReady;
    private int money;
    private Roles role;
    private Figures figure;
    private int playerLocation;
    private int sequence;
    private int unavailableRounds;
    private boolean hasPlayed;
    private boolean isOwner;

    public User(String userName, WebSocketSession session) {
        this.userName = userName;
        this.session = session;
        this.isReady = false;
        this.lobby = null;
        this.money = 1500;
        this.role = null;
        this.figure = null;
        this.playerLocation = 1;
        this.sequence = 0;
        this.unavailableRounds = 0;
        this.hasPlayed = false;
        this.isOwner = false;
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
        this.playerLocation = 1;
        this.sequence = 0;
        this.unavailableRounds = 0;
        this.hasPlayed = false;
    }

    public void assignRandomRole() {
        Random random = new Random();
        Roles[] roles = Roles.values();
        this.role = roles[random.nextInt(roles.length)];
    }

    // Gleiche User Objekte wenn Username gleich ist.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userName.equals(user.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName);
    }

    public void update(User user) {
        this.isOwner = user.isOwner;
        this.isReady = user.isReady;
        this.money = user.money;
        this.role = user.role;
        this.figure = user.figure;
        this.playerLocation = user.playerLocation;
        this.sequence = user.sequence;
    }

    /* // Gleiche User Objekte wenn alle Variablen gleich sind.
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        User user = (User) obj;
        return isOwner == user.isOwner &&
                isReady == user.isReady &&
                money == user.money &&
                username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, isOwner, isReady, money);
    }
     */



}
