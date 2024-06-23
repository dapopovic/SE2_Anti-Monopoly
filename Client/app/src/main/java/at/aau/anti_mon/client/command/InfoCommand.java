package at.aau.anti_mon.client.command;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

import at.aau.anti_mon.client.utilities.GlobalEventQueue;
import at.aau.anti_mon.client.events.TestEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;

public class InfoCommand implements Command{

    private final CommandFactory commandFactory;
    //private final GlobalEventQueue queue;

    @Inject
    public InfoCommand() {
        this.commandFactory = CommandFactory.getInstance();
        //this.queue = queue;
    }

    @Override
    public void execute(JsonDataDTO data) {
        String infoMessage = data.getData().get("msg");
        String infoContext = data.getData().get("context");

        Log.e("InfoCommand", "Info received: " + infoMessage + " for context: " + infoContext);

        MutableLiveData<String> liveData = commandFactory.getLiveData(infoContext);
        if (liveData != null) {
            liveData.postValue(infoMessage);
        } else {
            Log.e("InfoCommand", "No LiveData found for context: " + infoContext);
        }
    }

    //@Override
    //public void execute(JsonDataDTO data) {
    //EventBus.getDefault().post(new TestEvent( data.getData().get("msg")));
    //}
}
