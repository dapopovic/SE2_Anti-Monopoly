package at.aau.anti_mon.client.command;


import javax.inject.Inject;

import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.ui.lobby.LobbyViewModel;

public class LeaveGameCommand implements Command {
    private final LobbyViewModel viewModel;

    @Inject
    public LeaveGameCommand(LobbyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void execute(JsonDataDTO data) {
        // Update LiveData for UI-bound updates
        viewModel.userLeft(data.getData().get("username"));
    }
}
