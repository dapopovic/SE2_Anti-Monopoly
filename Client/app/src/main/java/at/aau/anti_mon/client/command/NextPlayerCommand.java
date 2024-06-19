package at.aau.anti_mon.client.command;

import android.util.Log;

import javax.inject.Inject;

import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.NextPlayerEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class NextPlayerCommand implements Command{
    private final GlobalEventQueue queue;

    @Inject
    public NextPlayerCommand(GlobalEventQueue queue) {
        this.queue = queue;
    }

    @Override
    public void execute(JsonDataDTO data) {
        String username = data.getData().get("username");
        Log.d("NextPlayerCommand", "We are in NextPlayerCommand");
        Log.d("NextPlayerCommand", "Get name: " + username);

        // Zugriff auf die GlobalEventQueue über die Application Instanz
        queue.enqueueEvent(new NextPlayerEvent(username));
    }
}
