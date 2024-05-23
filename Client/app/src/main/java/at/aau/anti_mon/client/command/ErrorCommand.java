package at.aau.anti_mon.client.command;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.TestEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class ErrorCommand implements Command{

    private final GlobalEventQueue queue;

    @Inject
    public ErrorCommand(GlobalEventQueue queue) {
        this.queue = queue;
    }

    @Override
    public void execute(JsonDataDTO data) {
        EventBus.getDefault().post(new TestEvent( data.getData().get("msg")));
    }
}
