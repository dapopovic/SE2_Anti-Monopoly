package at.aau.anti_mon.client.command;

import javax.inject.Inject;

import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.viewmodels.LobbyViewModel;

public class JoinGameCommand implements Command {
    private final LobbyViewModel viewModel;

    @Inject
    public JoinGameCommand(LobbyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void execute(JsonDataDTO data) {
        // Update LiveData for UI-bound updates
        viewModel.userJoined( data.getData().get("username"), Boolean.parseBoolean( data.getData().get("isOwner")), Boolean.parseBoolean( data.getData().get("isReady")));
    }
}
