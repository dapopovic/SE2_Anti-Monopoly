package at.aau.anti_mon.client.command;

import android.util.Log;

import javax.inject.Inject;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.ReportCheatingEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class ReportCheatingCommand implements Command {
    private final GlobalEventQueue queue;
    @Inject
    public ReportCheatingCommand(GlobalEventQueue queue) {
        this.queue = queue;
    }
    @Override
    public void execute(JsonDataDTO data) {
        String username = data.getData().get("username");
        String reporterName = data.getData().get("reporter_name");
        Boolean isCheater = Boolean.valueOf(data.getData().get("is_cheater"));

        queue.enqueueEvent(new ReportCheatingEvent(username, reporterName, isCheater));
    }
}
