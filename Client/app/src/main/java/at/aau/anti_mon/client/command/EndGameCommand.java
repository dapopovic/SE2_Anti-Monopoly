package at.aau.anti_mon.client.command;


import javax.inject.Inject;

import at.aau.anti_mon.client.events.EndGameEvent;
import at.aau.anti_mon.client.utilities.GlobalEventQueue;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class EndGameCommand implements Command{
    private final GlobalEventQueue queue;

    @Inject
    public EndGameCommand(GlobalEventQueue queue) {
        this.queue = queue;
    }

    @Override
    public void execute(JsonDataDTO data) {
        int rank = Integer.parseInt(data.getData().get("rank"));

        queue.enqueueEvent(new EndGameEvent(rank));
    }
}
