package at.aau.anti_mon.client.command;

import android.util.Log;

import javax.inject.Inject;

import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.PinReceivedEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class PinCommand implements Command {


    private final GlobalEventQueue queue;

    @Inject
    public PinCommand(GlobalEventQueue queue) {
        this.queue = queue;
    }


    @Override
    public void execute(JsonDataDTO data) {
        String pin = data.getData().get("pin");
        Log.d("PinCommand", "Posting pin received event with pin: " + pin);
        // Zugriff auf die GlobalEventQueue über die Application Instanz
        queue.enqueueEvent(new PinReceivedEvent(pin));
    }

}
