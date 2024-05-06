package at.aau.anti_mon.client.command;

import android.util.Log;

import javax.inject.Inject;

import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.viewmodels.LobbyViewModel;

public class NewUserCommand implements Command{

    private final GlobalEventQueue queue;
    private final LobbyViewModel viewModel;

    @Inject
    public NewUserCommand(GlobalEventQueue queue, LobbyViewModel viewModel) {
        this.queue = queue;
        this.viewModel = viewModel;
    }

    @Override
    public void execute(JsonDataDTO data) {
        boolean isOwner = Boolean.parseBoolean(data.getData().get("isOwner"));
        String username = data.getData().get("username");

//        Log.d("NewUserCommand", "Posting user joined lobby event with username: " + data.getData().get("username") + " and isOwner: " + data.getData().get("isOwner") + " " + Boolean.getBoolean(data.getData().get("isOwner")));

        // Update LiveData for UI-bound updates
        viewModel.userJoined(username, isOwner);
    }
}
