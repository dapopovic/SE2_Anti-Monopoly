package at.aau.anti_mon.client.game;

import androidx.lifecycle.LifecycleOwner;

import javax.inject.Inject;

import at.aau.anti_mon.client.events.DiceNumberReceivedEvent;
import at.aau.anti_mon.client.ui.gameboard.GameBoardViewModel;


public class GameEventHandler {

    private final GameBoardViewModel gameBoardViewModel;

    @Inject
    public GameEventHandler(GameBoardViewModel gameBoardViewModel) {
        this.gameBoardViewModel = gameBoardViewModel;
    }

    /**
     * Observer für die LiveData-Objekte.
     */
    public void setupObservers(LifecycleOwner lifecycleOwner) {
        /*gameBoardViewModel.getGameState().observe(lifecycleOwner, this::handleGameStateChange);

        gameBoardViewModel.getCurrentPlayer().observe(lifecycleOwner, player -> {
            // Handle current player change
        });

        gameBoardViewModel.getDiceRoll().observe(lifecycleOwner, diceRoll -> {
            // Handle dice roll
        });

        gameBoardViewModel.getShowDialogEvent().observe(lifecycleOwner, message -> {
            // Show dialog with message
        });

        gameBoardViewModel.getDiceNumberData().observe(lifecycleOwner, this::onDiceNumberReceivedEvent);
        
         */
    }

    private void handleGameStateChange(GameState gameState) {
        //TODO: Logik für die verschiedenen Spielzustände
        switch (gameState) {
            case INITIALIZED:
                // Start turn logic
                break;
            case ROLL_DICE:
                // Throw dice logic
                break;
            case PLAYER_TURN:
                // End turn logic
                break;
            case NEXT_PLAYER:
                // Winning logic
                break;
        }
    }

    private void onDiceNumberReceivedEvent(DiceNumberReceivedEvent event) {
        // Handle dice number received event
    }

}
