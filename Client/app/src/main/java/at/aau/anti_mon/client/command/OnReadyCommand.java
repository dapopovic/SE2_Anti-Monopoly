package at.aau.anti_mon.client.command;

import android.util.Log;

import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.OnReadyEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class OnReadyCommand implements Command {
    private final GlobalEventQueue queue;

    @Override
    public void execute(JsonDataDTO data) {
        Log.d("OnReadyCommand", "User is ready");
        queue.enqueueEvent(new OnReadyEvent(data.getData().get("username")));
    }
}
