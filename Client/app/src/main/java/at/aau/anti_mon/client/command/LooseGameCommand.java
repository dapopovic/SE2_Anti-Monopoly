package at.aau.anti_mon.client.command;

import android.util.Log;

import javax.inject.Inject;

import at.aau.anti_mon.client.ui.gameboard.GameBoardViewModel;
import at.aau.anti_mon.client.events.LooseGameEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class LooseGameCommand implements Command{

    GameBoardViewModel viewModel;

    @Inject
    public LooseGameCommand(GameBoardViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void execute(JsonDataDTO data) {
        String username = data.getData().get("username");
        String newBalance = data.getData().get("new_balance");
        Log.d("ChangeBalanceCommand", "Change balance command received: " + newBalance);

        //assert newBalance != null;
        //queue.enqueueEvent(new ChangeBalanceEvent(username, Integer.valueOf(newBalance)));

        viewModel.looseGameEvent(new LooseGameEvent(username));
    }
}