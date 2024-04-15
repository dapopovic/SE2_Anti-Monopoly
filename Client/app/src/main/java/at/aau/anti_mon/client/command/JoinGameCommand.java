package at.aau.anti_mon.client.command;

import org.greenrobot.eventbus.EventBus;

import at.aau.anti_mon.client.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class JoinGameCommand implements Command{
    @Override
    public void execute(JsonDataDTO data) {
        EventBus.getDefault().post(new UserJoinedLobbyEvent(data.getData().get("name")));
    }
}
