package at.aau.anti_mon.client.command;

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
        // Verwenden von EventBus für nicht-UI-bezogene globale Ereignisse
        queue.enqueueEvent(new UserJoinedLobbyEvent(data.getData().get("username")));

        // Update LiveData for UI-bound updates
        viewModel.userJoined(data.getData().get("username"));
    }
}
