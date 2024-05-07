package at.aau.anti_mon.client.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import at.aau.anti_mon.client.game.User;
import lombok.Getter;

@Getter
public class LobbyViewModel extends ViewModel {
    private final MutableLiveData<User> userJoinedLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> userLeftLiveData = new MutableLiveData<>();

    // Methoden zum Aktualisieren der LiveData
    public void userJoined(String username, boolean isOwner, boolean isReady) {
        userJoinedLiveData.postValue(new User(username, isOwner, isReady));
    }

    public void userLeft(String username) {
        userLeftLiveData.postValue(username);
    }

}