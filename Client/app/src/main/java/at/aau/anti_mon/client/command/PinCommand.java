package at.aau.anti_mon.client.command;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.util.Log;

import javax.inject.Inject;

import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.ui.creategame.CreateGameViewModel;

public class PinCommand implements Command {
    private final CreateGameViewModel viewModel;

    @Inject
    public PinCommand(CreateGameViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void execute(JsonDataDTO data) {
        String pin = data.getData().get("pin");
        Log.d(DEBUG_TAG, "Posting pin received event with pin: " + pin);

        // Update LiveData for UI-bound updates
        viewModel.onPinReceived(pin);
    }

}
