package at.aau.anti_mon.client.command;

import android.util.Log;

import javax.inject.Inject;

import at.aau.anti_mon.client.events.CheatingEvent;
import at.aau.anti_mon.client.utilities.GlobalEventQueue;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class CheatingCommand implements Command {
    private final GlobalEventQueue queue;

    @Inject
    public CheatingCommand(GlobalEventQueue queue) {
        this.queue = queue;
    }

    @Override
    public void execute(JsonDataDTO data) {
        Log.d("Cheating command", "Cheating command received!");
        queue.enqueueEvent(new CheatingEvent());
    }
}
