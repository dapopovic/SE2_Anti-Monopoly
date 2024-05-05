package at.aau.anti_mon.client.command;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.Objects;

import javax.inject.Inject;

import at.aau.anti_mon.client.events.CreatedGameEvent;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.viewmodels.LobbyViewModel;

public class CreateGameCommand implements Command{

    private final GlobalEventQueue queue;
    private final LobbyViewModel viewModel;

    @Inject
    public CreateGameCommand(GlobalEventQueue queue, LobbyViewModel viewModel) {
        this.queue = queue;
        this.viewModel = viewModel;
    }

    @Override
    public void execute(JsonDataDTO data) {
        // Verwenden von EventBus für nicht-UI-bezogene globale Ereignisse
        Log.d("CreateGameCommand", Objects.requireNonNull(data.getData().get("pin")));
        queue.enqueueEvent(new CreatedGameEvent(data.getData().get("pin")));

        // Update LiveData for UI-bound updates
        viewModel.userJoined(data.getData().get("username"), Boolean.getBoolean(data.getData().get("isOwner")));

    }
}
