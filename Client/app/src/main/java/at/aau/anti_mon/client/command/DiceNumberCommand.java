package at.aau.anti_mon.client.command;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.util.Log;

import javax.inject.Inject;

import at.aau.anti_mon.client.events.DiceNumberReceivedEvent;
import at.aau.anti_mon.client.ui.gameboard.GameBoardViewModel;
import at.aau.anti_mon.client.utilities.GlobalEventQueue;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class DiceNumberCommand implements Command{

private final GameBoardViewModel viewModel;


    @Inject
    public DiceNumberCommand(GameBoardViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void execute(JsonDataDTO data) {
        Integer dicenumber = Integer.valueOf(data.getData().get("dicenumber"));
        String username = data.getData().get("username");
        String figure = data.getData().get("figure");
        Integer location = Integer.valueOf(data.getData().get("location"));

        Log.d(DEBUG_TAG, "DiceNumberCommand - dicenumber: " + dicenumber + " username: " + username + " figure: " + figure + " location: " + location);

        // Zugriff auf die GlobalEventQueue über die Application Instanz
        //queue.enqueueEvent(new DiceNumberReceivedEvent(dicenumber,username,figure,location));
        viewModel.diceNumberReceivedEvent(new DiceNumberReceivedEvent(dicenumber,username,figure,location));
    }
}
