package at.aau.anti_mon.client.command;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.viewmodels.LobbyViewModel;

public class JoinGameCommand implements Command {

    private final GlobalEventQueue queue;
    private final LobbyViewModel viewModel;

    @Inject
    public JoinGameCommand(GlobalEventQueue queue, LobbyViewModel viewModel) {
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
