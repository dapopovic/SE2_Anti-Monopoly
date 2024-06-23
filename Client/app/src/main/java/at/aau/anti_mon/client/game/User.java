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
@Setter
public class User {

    private String username;
    @JsonProperty("owner")
    private boolean isOwner;
    private boolean isReady;
    private int money;
    private Roles role;
    private Figures figure;
    private boolean currentPlayer;
    private boolean lostGame;

    int playerLocation;

    public User(String username, boolean isOwner, boolean isReady) {
        this.username = username;
        this.isOwner = isOwner;
        this.isReady = isReady;
        this.money = 1500;
        this.role = null;
        this.figure = null;
        this.currentPlayer=false;
        this.lostGame = false;
        this.playerLocation = 0;
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

    public int getLocation() {
        return playerLocation;
    }
}
