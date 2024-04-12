package at.aau.anti_mon.client.command;

import android.util.Log;

import at.aau.anti_mon.client.json.JsonDataDTO;

public class HeartBeatCommand implements Command{
    @Override
    public void execute(JsonDataDTO data) {
        Log.println(Log.INFO,"Network", "CLIENT : HeartBeat empfangen: ");

    }
}
