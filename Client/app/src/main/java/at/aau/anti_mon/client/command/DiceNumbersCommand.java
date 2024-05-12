package at.aau.anti_mon.client.command;

import javax.inject.Inject;

import at.aau.anti_mon.client.events.DiceNumbersEvent;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class DiceNumbersCommand implements Command {
    private final GlobalEventQueue queue;

    @Inject
    public DiceNumbersCommand(GlobalEventQueue queue) {
        this.queue = queue;
    }

    @Override
    public void execute(JsonDataDTO data) {
        //queue.enqueueEvent(new DiceNumbersEvent(data.getData().get("number")));
    }
}
