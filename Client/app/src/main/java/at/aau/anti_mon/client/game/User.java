package at.aau.anti_mon.client.game;

import com.fasterxml.jackson.annotation.JsonProperty;

import at.aau.anti_mon.client.enums.Figures;
import at.aau.anti_mon.client.enums.Roles;
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
    @Setter
    private int money;
    @Setter
    private Roles role;
    @Setter
    private Figures figure;
    @Setter
    private boolean currentPlayer;
    @Setter
    private boolean lostGame;

    public User(String username, boolean isOwner, boolean isReady) {
        this.username = username;
        this.isOwner = isOwner;
        this.isReady = isReady;
        this.money = 1500;
        this.role = null;
        this.figure = null;
        this.currentPlayer=false;
        this.lostGame = false;
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
