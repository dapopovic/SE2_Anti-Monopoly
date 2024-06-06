package at.aau.anti_mon.client.command;
import android.util.Log;

import java.util.Objects;

import javax.inject.Inject;

import at.aau.anti_mon.client.events.DiceNumberReceivedEvent;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.json.JsonDataDTO;
public class DiceNumberCommand implements Command{

    private final GlobalEventQueue queue;

    @Inject
    public DiceNumberCommand(GlobalEventQueue queue) {
        this.queue = queue;
    }

    @Override
    public void execute(JsonDataDTO data) {
        Integer dicenumber = Integer.valueOf(data.getData().get("dicenumber"));
        String name = data.getData().get("username");
        Log.d("DiceNumberCommand", "Posting Dice received event with dice: " + dicenumber);
        Log.d("DiceNumberCommand", "Posting Dice received event with name: " + name);
        // Zugriff auf die GlobalEventQueue über die Application Instanz
        queue.enqueueEvent(new DiceNumberReceivedEvent(dicenumber,name));
    }
}
