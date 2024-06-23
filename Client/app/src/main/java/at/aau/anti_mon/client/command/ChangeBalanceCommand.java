package at.aau.anti_mon.client.command;

import android.util.Log;

import javax.inject.Inject;

import at.aau.anti_mon.client.events.ChangeBalanceEvent;
import at.aau.anti_mon.client.utilities.GlobalEventQueue;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class ChangeBalanceCommand implements Command {
    private final GlobalEventQueue queue;
    @Inject
    public ChangeBalanceCommand(GlobalEventQueue queue) {
        this.queue = queue;
    }
    @Override
    public void execute(JsonDataDTO data) {
        String username = data.getData().get("username");
        String newBalance = data.getData().get("new_balance");
        Log.d("ChangeBalanceCommand", "Change balance command received: " + newBalance);

        assert newBalance != null;
        queue.enqueueEvent(new ChangeBalanceEvent(username, Integer.valueOf(newBalance)));
    }
}
