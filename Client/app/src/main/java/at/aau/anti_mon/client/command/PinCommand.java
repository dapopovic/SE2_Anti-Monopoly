package at.aau.anti_mon.client.command;

import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import at.aau.anti_mon.client.events.CreatedGameEvent;
import at.aau.anti_mon.client.events.PinReceivedEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class PinCommand implements Command {


    @Override
    public void execute(JsonDataDTO data) {
        String pin = data.getData().get("pin");
        Log.d("PinCommand", "Posting pin received event with pin: " + pin);
        EventBus.getDefault().post(new PinReceivedEvent(pin));
    }

}
