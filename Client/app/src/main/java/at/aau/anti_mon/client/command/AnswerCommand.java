package at.aau.anti_mon.client.command;

import org.greenrobot.eventbus.EventBus;

import at.aau.anti_mon.client.events.CreatedGameEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class AnswerCommand implements Command {
    @Override
    public void execute(JsonDataDTO data) {
        EventBus.getDefault().post(new CreatedGameEvent(data.getData().get("pin")));
    }
}
