package at.aau.anti_mon.client.command;

import android.util.Log;

import javax.inject.Inject;

import at.aau.anti_mon.client.events.CanDetectCheaterEvent;
import at.aau.anti_mon.client.events.ChangeBalanceEvent;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class CanDetectCheaterCommand implements Command {
    private final GlobalEventQueue queue;
    @Inject
    public CanDetectCheaterCommand(GlobalEventQueue queue) {
        this.queue = queue;
    }
    @Override
    public void execute(JsonDataDTO data) {
        String username = data.getData().get("username");
        String can_detect_cheater = data.getData().get("can_detect_cheater");
        String location = data.getData().get("location");
        Log.d("CanDetectCheaterCommand", "Can detect cheater received: " + can_detect_cheater);
        assert location != null;
        queue.enqueueEvent(new CanDetectCheaterEvent(username, Boolean.parseBoolean(can_detect_cheater), Integer.valueOf(location)));
    }
}
