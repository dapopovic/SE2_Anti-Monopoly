package at.aau.anti_mon.client.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import lombok.Getter;

@Getter
public class LobbyViewModel extends ViewModel {
    private final MutableLiveData<String> userJoinedLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> userLeftLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> gameCreatedLiveData = new MutableLiveData<>();


    // Methoden zum Aktualisieren der LiveData
    public void userJoined(String username) {
        userJoinedLiveData.postValue(username);
    }

    public void userLeft(String username) {
        userLeftLiveData.postValue(username);
    }

    public void createGame(String pin) {
        gameCreatedLiveData.postValue(pin);
    }


}