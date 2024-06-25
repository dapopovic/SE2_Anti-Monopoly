package at.aau.anti_mon.client.command;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.util.Log;

import javax.inject.Inject;

import at.aau.anti_mon.client.ui.gameboard.GameBoardViewModel;
import at.aau.anti_mon.client.events.NextPlayerEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class NextPlayerCommand implements Command{

    GameBoardViewModel viewModel;

    @Inject
    public NextPlayerCommand(GameBoardViewModel viewModel ) {
       this.viewModel = viewModel;
    }

    @Override
    public void execute(JsonDataDTO data) {
        String username = data.getData().get("username");
        Log.d(DEBUG_TAG, "NextPlayerCommand - username: " + username);


        viewModel.nextPlayerEvent(new NextPlayerEvent(username));

        // Zugriff auf die GlobalEventQueue über die Application Instanz
        //queue.enqueueEvent(new NextPlayerEvent(username));
    }
}
