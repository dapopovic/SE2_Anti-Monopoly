package at.aau.anti_mon.client.command;

import android.util.Log;

import javax.inject.Inject;

import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.ui.gameboard.GameBoardViewModel;

public class FirstPlayerCommand implements Command{
    private final GameBoardViewModel viewModel;

    @Inject
    public FirstPlayerCommand(GameBoardViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void execute(JsonDataDTO data) {
        String username =  data.getData().get("username");

        Log.d("NewUserCommand", "New user joined: " + username );
        // Update LiveData for UI-bound updates
        viewModel.setFirstPlayer(username);
    }
}
