package at.aau.anti_mon.client.command;

import android.util.Log;

import javax.inject.Inject;

import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.ui.lobby.LobbyViewModel;

public class NewUserCommand implements Command{
    private final LobbyViewModel viewModel;

    @Inject
    public NewUserCommand(LobbyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    // TODO: Deserialize User-Object from JsonDataDTO and update LiveData
    @Override
    public void execute(JsonDataDTO data) {
        boolean isOwner = Boolean.parseBoolean( data.getData().get("isOwner"));
        String username =  data.getData().get("username");
        boolean isReady = Boolean.parseBoolean( data.getData().get("isReady"));

        Log.d("NewUserCommand", "New user joined: " + username + " isOwner: " + isOwner + " isReady: " + isReady);
        // Update LiveData for UI-bound updates

        User user = new User(username, isOwner, isReady);
        viewModel.addUser(user);

        //viewModel.onUserJoined(username, isOwner, isReady);
    }
}
