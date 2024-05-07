package at.aau.anti_mon.client.command;

import android.util.Log;

import javax.inject.Inject;

import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.OnReadyEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.viewmodels.LobbyViewModel;
import lombok.AllArgsConstructor;

public class OnReadyCommand implements Command {
    private final GlobalEventQueue queue;
    private final LobbyViewModel viewModel;
    @Inject
    public OnReadyCommand(GlobalEventQueue queue, LobbyViewModel viewModel) {
        this.queue = queue;
        this.viewModel = viewModel;
    }
    @Override
    public void execute(JsonDataDTO data) {
        Log.d("OnReadyCommand", "User is ready");
        viewModel.readyUp(data.getData().get("username"), Boolean.parseBoolean(data.getData().get("isReady")));
    }
}
