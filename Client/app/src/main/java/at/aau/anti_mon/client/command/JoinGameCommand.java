package at.aau.anti_mon.client.command;

import javax.inject.Inject;

import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.ui.lobby.LobbyViewModel;

public class JoinGameCommand implements Command {
    private final LobbyViewModel viewModel;

    @Inject
    public JoinGameCommand(LobbyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void execute(JsonDataDTO data) {

        User user = new User(data.getData().get("username"), Boolean.parseBoolean(data.getData().get("isOwner")), Boolean.parseBoolean(data.getData().get("isReady")));
        viewModel.addUser(user);
        // Update LiveData for UI-bound updates
        //viewModel.onUserJoined( data.getData().get("username"), Boolean.parseBoolean( data.getData().get("isOwner")), Boolean.parseBoolean( data.getData().get("isReady")));
    }
}
