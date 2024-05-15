package at.aau.anti_mon.client.game;

import com.fasterxml.jackson.annotation.JsonProperty;

import at.aau.anti_mon.client.enums.Figures;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class User {
    private String username;
    @JsonProperty("owner")
    private boolean isOwner;
    @Setter
    private boolean isReady;
    private int money;
    @Setter
    private Figures figure;

    public User(String username, boolean isOwner, boolean isReady) {
        this.username = username;
        this.isOwner = isOwner;
        this.isReady = isReady;
        this.money = 1500;
        this.figure = null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof User user)) {
            return false;
        }
        return username.equals(user.username) && isOwner == user.isOwner && isReady == user.isReady && money == user.money;
    }
}
