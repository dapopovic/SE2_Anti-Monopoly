package at.aau.anti_mon.client.command;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import at.aau.anti_mon.client.events.CreatedGameEvent;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class CreateGameCommand implements Command{

    private final GlobalEventQueue queue;

    @Inject
    public CreateGameCommand(GlobalEventQueue queue) {
        this.queue = queue;
    }

    @Override
    public void execute(JsonDataDTO data) {
        EventBus.getDefault().post(new CreatedGameEvent(data.getData().get("pin")));
    }
}
