package at.aau.anti_mon.client.command;

import android.util.Log;

import javax.inject.Inject;

import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.ui.lobby.LobbyViewModel;

/**
 * Command to start the game containing the users
 */
public class StartGameCommand implements Command {
    LobbyViewModel lobbyViewModel;
    @Inject
    public StartGameCommand(LobbyViewModel lobbyViewModel) {
        this.lobbyViewModel = lobbyViewModel;
    }
    @Override
    public void execute(JsonDataDTO data) {
        Log.d("StartGameCommand", "Game started " + data.getData().get("users"));

        User[] users = JsonDataManager.parseJsonMessage( data.getData().get("users"), User[].class);
        lobbyViewModel.onStartGameEvent(users);
    }
}
