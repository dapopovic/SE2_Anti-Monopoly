package at.aau.anti_mon.client.game;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

import at.aau.anti_mon.client.enums.Figures;
import at.aau.anti_mon.client.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {

    private String username;
    @JsonProperty("owner")
    private boolean isOwner;
    private boolean isReady;
    private int money;
    private Roles role;
    private Figures figure;
    private int location;
    private int sequence;

    public User(String username, boolean isOwner, boolean isReady) {
        this.username = username;
        this.isOwner = isOwner;
        this.isReady = isReady;
        this.money = 1500;
        this.role = null;
        this.figure = null;
        this.location = 1;
        this.sequence = 0;
    }

    public User(String testUser, boolean isOwner, boolean isReady, int money) {
        this.username = testUser;
        this.isOwner = isOwner;
        this.isReady = isReady;
        this.money = money;
        this.role = null;
        this.figure = null;
        this.location = 1;
        this.sequence = 0;
    }

    // Gleiche User Objekte wenn Username gleich ist.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return username.equals(user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    public void update(User user) {
        this.isOwner = user.isOwner;
        this.isReady = user.isReady;
        this.money = user.money;
        this.role = user.role;
        this.figure = user.figure;
        this.location = user.location;
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
