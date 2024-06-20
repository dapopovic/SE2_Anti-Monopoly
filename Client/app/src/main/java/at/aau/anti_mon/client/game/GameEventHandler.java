package at.aau.anti_mon.client.game;

import androidx.lifecycle.LifecycleOwner;

import javax.inject.Inject;

import at.aau.anti_mon.client.events.DiceNumberReceivedEvent;
import at.aau.anti_mon.client.ui.gamefield.GameFieldViewModel;

public class GameEventHandler {

    private final GameFieldViewModel gameFieldViewModel;

    @Inject
    public GameEventHandler(GameFieldViewModel gameFieldViewModel) {
        this.gameFieldViewModel = gameFieldViewModel;
    }

    /**
     * Setzt die Observer für die LiveData-Objekte.
     */
    public void setupObservers(LifecycleOwner lifecycleOwner) {
        gameFieldViewModel.getGameState().observe(lifecycleOwner, this::handleGameStateChange);

        gameFieldViewModel.getCurrentPlayer().observe(lifecycleOwner, player -> {
            // Handle current player change
        });

        gameFieldViewModel.getDiceRoll().observe(lifecycleOwner, diceRoll -> {
            // Handle dice roll
        });

        gameFieldViewModel.getShowDialogEvent().observe(lifecycleOwner, message -> {
            // Show dialog with message
        });

        gameFieldViewModel.getDiceNumberData().observe(lifecycleOwner, this::onDiceNumberReceivedEvent);
    }

    private void handleGameStateChange(GameState gameState) {
        //TODO: Implementiere die Logik für die verschiedenen Spielzustände
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
