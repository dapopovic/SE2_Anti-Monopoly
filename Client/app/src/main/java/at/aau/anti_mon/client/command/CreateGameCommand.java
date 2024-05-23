package at.aau.anti_mon.client.command;

import android.util.Log;

import java.util.Objects;

import javax.inject.Inject;

import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.viewmodels.LobbyViewModel;

public class CreateGameCommand implements Command{
    private final LobbyViewModel viewModel;

    @Inject
    public CreateGameCommand(LobbyViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void execute(JsonDataDTO data) {
        Log.d("CreateGameCommand", Objects.requireNonNull(data.getData().get("pin")));
        // Update LiveData for UI-bound updates
        viewModel.userJoined( data.getData().get("username"), Boolean.parseBoolean( data.getData().get("isOwner")), Boolean.parseBoolean( data.getData().get("isReady")));
    }
}
