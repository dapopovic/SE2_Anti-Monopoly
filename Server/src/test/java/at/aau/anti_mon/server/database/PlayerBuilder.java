package at.aau.anti_mon.server.database;

import at.aau.anti_mon.server.entities.Player;

/**
 * Builder for Player objects
 */
public class PlayerBuilder {

    private String name = "TestPlayer";
    private int balance = 1000;
    private int position = 4;

    public PlayerBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public PlayerBuilder withBalance(int balance) {
        this.balance = balance;
        return this;
    }

    public PlayerBuilder withPosition(int position) {
        this.position = position;
        return this;
    }

    public Player build() {
        Player player = new Player();
        player.setName(name);
        player.setBalance(balance);
        player.setPosition(position);
        return player;
    }
}
