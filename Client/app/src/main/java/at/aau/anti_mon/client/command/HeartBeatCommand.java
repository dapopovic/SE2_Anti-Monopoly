package at.aau.anti_mon.client.command;

import android.util.Log;

import javax.inject.Inject;

import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.HeartBeatEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class HeartBeatCommand implements Command{

    private final GlobalEventQueue queue;

    @Inject
    public HeartBeatCommand(GlobalEventQueue queue) {
        this.queue = queue;
    }


    @Override
    public void execute(JsonDataDTO data) {
        Log.println(Log.INFO,"Network", "CLIENT : HeartBeat empfangen: "+data.getData().get("msg"));
        queue.enqueueEvent(new HeartBeatEvent(data.getData().get("msg")));
    }
}
