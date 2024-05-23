package at.aau.anti_mon.client.command;

import android.util.Log;

import javax.inject.Inject;

import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.viewmodels.CreateGameViewModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PinCommand implements Command {
    private final CreateGameViewModel viewModel;

    @Inject
    public PinCommand(CreateGameViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void execute(JsonDataDTO data) {
        String pin = (String) data.getData().get("pin");
        Log.d("PinCommand", "Posting pin received event with pin: " + pin);

        // Update LiveData for UI-bound updates
        viewModel.createGame(pin);
    }



}
