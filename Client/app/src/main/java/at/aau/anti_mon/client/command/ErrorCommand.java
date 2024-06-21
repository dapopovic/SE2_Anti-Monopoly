package at.aau.anti_mon.client.command;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

import at.aau.anti_mon.client.json.JsonDataDTO;

public class ErrorCommand implements Command{

    private final CommandFactory commandFactory;
    //private final GlobalEventQueue queue;

    @Inject
    public ErrorCommand() {
        this.commandFactory = CommandFactory.getInstance();
        //this.queue = queue;
    }

    @Override
    public void execute(JsonDataDTO data) {
        String errorMessage = data.getData().get("msg");
        String errorContext = data.getData().get("context");

        Log.e("ErrorCommand", "Error received: " + errorMessage + " for context: " + errorContext);

        MutableLiveData<String> liveData = commandFactory.getLiveData(errorContext);
        if (liveData != null) {
            liveData.postValue(errorMessage);
        } else {
            Log.e("ErrorCommand", "No LiveData found for context: " + errorContext);
        }
    }

    //@Override
    //public void execute(JsonDataDTO data) {
        //EventBus.getDefault().post(new TestEvent( data.getData().get("msg")));
    //}
}
