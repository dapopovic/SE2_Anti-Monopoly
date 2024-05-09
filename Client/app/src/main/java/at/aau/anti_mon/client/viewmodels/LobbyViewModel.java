package at.aau.anti_mon.client.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Collection;

import at.aau.anti_mon.client.game.User;
import lombok.Getter;

@Getter
public class LobbyViewModel extends ViewModel {
    private final MutableLiveData<User> userJoinedLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> userLeftLiveData = new MutableLiveData<>();
    private final MutableLiveData<User> readyUpLiveData = new MutableLiveData<>();
    private final MutableLiveData<Collection<User>> startGameLiveData = new MutableLiveData<>();

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