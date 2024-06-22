package at.aau.anti_mon.client.ui.joingame;

import android.app.Application;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

import at.aau.anti_mon.client.data.SingleLiveEventData;
import at.aau.anti_mon.client.databinding.ActivityJoinGameBinding;
import at.aau.anti_mon.client.enums.Commands;
import at.aau.anti_mon.client.networking.MessagingService;
import at.aau.anti_mon.client.ui.base.BaseViewModel;
import lombok.Getter;

/**
 * ViewModel for the CreateGameActivity
 */
@Getter
public class JoinGameViewModel extends BaseViewModel {

    private final MutableLiveData<String> username = new MutableLiveData<>();
    private final MutableLiveData<String> pin = new MutableLiveData<>();
    private final SingleLiveEventData<String> errorLiveData = new SingleLiveEventData<>();
    private final SingleLiveEventData<String> infoLiveData = new SingleLiveEventData<>();
    private final MediatorLiveData<Boolean> canStartLobby = new MediatorLiveData<>();

    @Inject
    public JoinGameViewModel(Application application){
        super(application);
        canStartLobby.addSource(pin, value -> checkCanJoinLobby());
        canStartLobby.addSource(username, value -> checkCanJoinLobby());
        pin.setValue(null);
    }

    public void setUsername(String username) {
        preferenceManager.setCurrentUsername(username);
        this.username.setValue(username);
    }

    public void clearObservers(LifecycleOwner owner) {
        pin.removeObservers(owner);
        errorLiveData.removeObservers(owner);
        username.removeObservers(owner);
        canStartLobby.removeObservers(owner);
    }

    public void onJoinGameClicked(ActivityJoinGameBinding binding) {
        String textFieldUsername = binding.editTextJoinGameName.getText().toString();
        String textFieldPin = binding.editTextJoinGameLobbyPin.getText().toString();

        // Regular Expression for username: only alphanumeric characters
        String usernamePattern = "^[a-zA-Z0-9]+$";
        // Regular Expression for PIN: only digits
        String pinPattern = "^[0-9]+$";

        if (textFieldUsername.isEmpty()) {
            Toast.makeText(binding.getRoot().getContext(), "Please enter a username", Toast.LENGTH_SHORT).show();
        } else if (!textFieldUsername.matches(usernamePattern)) {
            Toast.makeText(binding.getRoot().getContext(), "Username must not contain special characters", Toast.LENGTH_SHORT).show();
        } else if (textFieldPin.isEmpty()) {
            Toast.makeText(binding.getRoot().getContext(), "Please enter a PIN", Toast.LENGTH_SHORT).show();
        } else if (!textFieldPin.matches(pinPattern)) {
            Toast.makeText(binding.getRoot().getContext(), "PIN must contain only numbers", Toast.LENGTH_SHORT).show();
        }else {
            preferenceManager.setCurrentUsername(textFieldUsername);
            preferenceManager.setCurrentPIN(textFieldPin);
            username.postValue(textFieldUsername);
            pin.postValue(textFieldPin);
            MessagingService.connectToServerWithUserID(textFieldUsername);
            MessagingService.createUserMessage(textFieldUsername, textFieldPin, Commands.JOIN).sendMessage();
        }
    }

    private void checkCanJoinLobby() {
        String pinValue = pin.getValue();
        String usernameValue = username.getValue();
        canStartLobby.setValue(pinValue != null && usernameValue != null);
    }
}
