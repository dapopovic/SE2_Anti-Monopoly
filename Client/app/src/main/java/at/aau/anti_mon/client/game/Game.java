package at.aau.anti_mon.client.game;

import java.util.ArrayList;
import java.util.Stack;

import lombok.Getter;

@Getter
public class Game {

    private final ArrayList<GameComponent> gameStreets;
    private final ArrayList<GameComponent> gamePlayers;
    private final Stack<GameComponent> chanceCards;

    public Game() {
        gameStreets = new ArrayList<>(40);
        gamePlayers = new ArrayList<>();
        chanceCards = new Stack<>();
    }

    public void addStreet(GameComponent street) {
        gameStreets.add(street);
    }

    public void addPlayer(GameComponent player) {
        gamePlayers.add(player);
    }

    public void removeStreet(GameComponent street) {
        gameStreets.remove(street);
    }

    public void removePlayer(GameComponent player) {
        gamePlayers.remove(player);
    }

    public void movePlayer(GameComponent player, int steps) {
        // move player
    }

}
