package at.aau.anti_mon.client.game;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import lombok.Getter;
import lombok.Setter;

/**
 * Verwaltung und Steuerung des Spielablaufs
 */
@Getter
@Setter
@Singleton
public class GameController {
    private boolean isPaused;

    private List<Player> playerList;
    private GameState gameState;
    int currentPlayerIndex;

    public GameController() {
        playerList = new ArrayList<>();
        this.isPaused = false;
        this.gameState = GameState.INITIALIZED;
    }

    public Player getCurrentPlayer() {
        return playerList.get(currentPlayerIndex);
    }

    public void addPlayer(Player player) {
        playerList.add(player);
    }

    public void initializeGame() {

    }

    public void startGame() {

    }

    public void endGame() {

    }

    public void pauseGame() {
        isPaused = true;
    }

    public void resumeGame() {
        isPaused = false;
    }

    public void saveGame() {

    }

    public void loadGame() {

    }

    public void getGameStatus() {

    }

    public void updateGameStatus() {

    }

    public void updateGameField() {

    }

    public void getPlayerStatus() {

    }

    public void updatePlayerStatus() {

    }

    public void getPlayerPosition() {

    }

    public void updatePlayerPosition() {

    }

    public void getPlayerMoney() {

    }

    public void updatePlayerMoney() {

    }

    public void drawCard() {

    }

    public void nextPlayer() {

    }

    public boolean isOver() {
        return false;
    }




}
