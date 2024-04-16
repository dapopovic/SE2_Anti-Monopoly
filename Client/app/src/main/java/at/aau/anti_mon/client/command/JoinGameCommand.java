package at.aau.anti_mon.client.command;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class JoinGameCommand implements Command{

    private GlobalEventQueue queue;

    @Inject
    public JoinGameCommand(GlobalEventQueue queue) {
        this.queue = queue;
    }

    @Override
    public void execute(JsonDataDTO data) {
        //EventBus.getDefault().post(new UserJoinedLobbyEvent(data.getData().get("name")));
        queue.enqueueEvent(new UserJoinedLobbyEvent(data.getData().get("username")));
    }
}
