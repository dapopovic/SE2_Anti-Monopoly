package at.aau.anti_mon.client.command;

import android.util.Log;

import javax.inject.Inject;

import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.LoseGameEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class LoseGameCommand implements Command{
    private final GlobalEventQueue queue;

    @Inject
    public LoseGameCommand(GlobalEventQueue queue) {
        this.queue = queue;
    }

    @Override
    public void execute(JsonDataDTO data) {
        String username = data.getData().get("username");
        Log.d("LoseGameCommand", "Get name: " + username);

        // Zugriff auf die GlobalEventQueue über die Application Instanz
        queue.enqueueEvent(new LoseGameEvent(username));
    }
}