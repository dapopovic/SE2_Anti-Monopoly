package at.aau.anti_mon.client.command;

import android.util.Log;

import javax.inject.Inject;

import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.ui.lobby.LobbyViewModel;

public class OnReadyCommand implements Command {
    private final LobbyViewModel viewModel;
    @Inject
    public OnReadyCommand(LobbyViewModel viewModel) {
        this.viewModel = viewModel;
    }
    @Override
    public void execute(JsonDataDTO data) {
        Log.d("OnReadyCommand", "User is ready");


        User user = new User.UserBuilder(data.getData().get("username"), Boolean.parseBoolean(data.getData().get("isOwner")), Boolean.parseBoolean(data.getData().get("isReady"))).build();
        viewModel.onReadyEvent(user);
    }
}
