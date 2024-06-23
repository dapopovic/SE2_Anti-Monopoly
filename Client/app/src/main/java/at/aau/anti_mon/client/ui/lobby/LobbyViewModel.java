package at.aau.anti_mon.client.ui.lobby;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Collection;

import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.utilities.SingleLiveEvent;
import lombok.Getter;

@Getter
public class LobbyViewModel extends ViewModel {
    private final SingleLiveEvent<User> userJoinedLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<String> userLeftLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<User> readyUpLiveData = new SingleLiveEvent<>();
    private final SingleLiveEvent<Collection<User>> startGameLiveData = new SingleLiveEvent<>();
    MutableLiveData<String> infoLiveData = new MutableLiveData<>();
    MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public LobbyViewModel(Application application) {
        super();
    }


    // Methoden zum Aktualisieren der LiveData
    public void userJoined(String username, boolean isOwner, boolean isReady) {
        userJoinedLiveData.postValue(new User(username, isOwner, isReady));
    }

    public void userLeft(String username) {
        userLeftLiveData.postValue(username);
    }
    public void readyUp(String username, boolean isReady) {
        Log.d("LobbyViewModel", "User is ready " + username);
        readyUpLiveData.postValue(new User(username, false, isReady));
    }
    public void startGame(Collection<User> users) {
        startGameLiveData.postValue(users);
    }
}