package at.aau.anti_mon.client.command;

import android.util.Log;

import javax.inject.Inject;

import at.aau.anti_mon.client.events.CheatingEvent;
import at.aau.anti_mon.client.ui.gameboard.GameBoardViewModel;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class CheatingCommand implements Command {

    private final GameBoardViewModel viewModel;

    @Inject
    public CheatingCommand(GameBoardViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void execute(JsonDataDTO data) {
        Log.d("Cheating command", "Cheating command received!");
        //queue.enqueueEvent(new CheatingEvent());


        viewModel.cheatingEvent(new CheatingEvent());
    }
}
