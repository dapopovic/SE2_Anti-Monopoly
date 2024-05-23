package at.aau.anti_mon.client.game;

import javax.annotation.Signed;
import javax.inject.Inject;
import javax.inject.Singleton;

import at.aau.anti_mon.client.viewmodels.GameFieldViewModel;
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

    private GameFieldViewModel gameFieldViewModel;

    public GameController() {
        this.isPaused = false;
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

    public void throwDice(){
        gameFieldViewModel.setGameState(GameState.THROW_DICE);
    }


    @Inject
    public void setGameFieldViewModel(GameFieldViewModel gameFieldViewModel) {
        this.gameFieldViewModel = gameFieldViewModel;
    }



}
