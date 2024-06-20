package at.aau.anti_mon.client.ui.creategame;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.data.SingleLiveEventData;
import at.aau.anti_mon.client.databinding.ActivityStartNewGameBinding;
import at.aau.anti_mon.client.networking.MessagingService;
import at.aau.anti_mon.client.ui.base.BaseViewModel;
import lombok.Getter;

/**
 * ViewModel for the CreateGameActivity
 */
@Getter
public class CreateGameViewModel extends BaseViewModel {

    private final MutableLiveData<String> username = new MutableLiveData<>();
    private final SingleLiveEventData<String> pin = new SingleLiveEventData<>();
    private final SingleLiveEventData<String> errorLiveData = new SingleLiveEventData<>();
    private final SingleLiveEventData<String> infoLiveData = new SingleLiveEventData<>();

    @Inject
    public CreateGameViewModel(Application application){
        super(application);
        pin.setValue(null);
    }

    public void setUsername(String username) {
        preferenceManager.setCurrentUsername(username);
        this.username.setValue(username);
    }

    public void resetPin() {
        pin.setValue(null);
    }

    public void clearObservers(LifecycleOwner owner) {
        pin.removeObservers(owner);
        errorLiveData.removeObservers(owner);
        username.removeObservers(owner);
        infoLiveData.removeObservers(owner);
    }

    /**
     * Method called by the PinCommand to update the pin LiveData
     * @param receivedPin the pin received from the server
     */
    public void onPinReceived(String receivedPin) {
        pin.postValue(receivedPin);
        Log.d(DEBUG_TAG, "Pin received: " + receivedPin);

        String username = getUsername().getValue();
        if (username == null || username.isEmpty()) {
            Log.d(DEBUG_TAG, "Username is not set. Cannot start lobby.");
        }
    }

    public void onCreateGameClicked(ActivityStartNewGameBinding binding) {
        String textFieldUsername = binding.username.getText().toString();

        // Regular Expression for username: only alphanumeric characters
        String usernamePattern = "^[a-zA-Z0-9]+$";

        if (textFieldUsername.isEmpty()) {
            Toast.makeText(binding.getRoot().getContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
        } else if (!textFieldUsername.matches(usernamePattern)) {
            Toast.makeText(binding.getRoot().getContext(), "Username must not contain special characters", Toast.LENGTH_SHORT).show();
        } else {

            //Speichern des Nutzernamens in den SharedPreferences
            preferenceManager.setCurrentUsername(textFieldUsername);

            username.postValue(textFieldUsername);
            MessagingService.connectToServerWithUserID(textFieldUsername);
            MessagingService.createUserMessage(textFieldUsername, Commands.CREATE_GAME).sendMessage();
            preferenceManager.setCurrentUsername(textFieldUsername);
        }
    }

}
