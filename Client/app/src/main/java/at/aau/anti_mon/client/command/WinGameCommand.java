package at.aau.anti_mon.client.command;

import android.util.Log;

import javax.inject.Inject;

import at.aau.anti_mon.client.utilities.GlobalEventQueue;
import at.aau.anti_mon.client.events.WinGameEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class WinGameCommand implements Command{
    private final GlobalEventQueue queue;

    @Inject
    public WinGameCommand(GlobalEventQueue queue) {
        this.queue = queue;
    }

    @Override
    public void execute(JsonDataDTO data) {
        String username = data.getData().get("username");
        Log.d("WinGameCommand", "We are in WinGameCommand");
        Log.d("WinGameCommand", "Get name: " + username);

        // Zugriff auf die GlobalEventQueue über die Application Instanz
        queue.enqueueEvent(new WinGameEvent(username));
    }
}