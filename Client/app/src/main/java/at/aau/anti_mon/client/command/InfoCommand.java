package at.aau.anti_mon.client.command;

import javax.inject.Inject;

import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class InfoCommand implements Command{

    private final GlobalEventQueue queue;

    @Inject
    public InfoCommand(GlobalEventQueue queue) {
        this.queue = queue;
    }

    @Override
    public void execute(JsonDataDTO data) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
