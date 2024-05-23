package at.aau.anti_mon.client.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.game.GameState;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.WebSocketClient;
import lombok.Getter;
import lombok.Setter;


/**
 * Model-View-ViewModel for the game field.
 * Provides a clear separation of concerns between the UI and the game logic.
 * The ViewModel is responsible for handling the game state and the game logic.
 */
@Getter
@Setter
public class GameFieldViewModel extends ViewModel {
    private ArrayList<User> users;
    private User currentUser;
    private String pin;

    /**
     * The current game state.
     */
    private MutableLiveData<GameState> gameState = new MutableLiveData<>(GameState.START_TURN);

    @Inject WebSocketClient webSocketClient;

    public void setUsers(User[] users) {
        this.users = new ArrayList<>();
        Collections.addAll(this.users, users);
    }

    public void setGameState(GameState state) {
        gameState.setValue(state);
    }

    public void leaveGame() {
        JsonDataManager.createUserMessage(currentUser.getUsername(),pin, Commands.LEAVE_GAME).sendMessage();
    }
}
