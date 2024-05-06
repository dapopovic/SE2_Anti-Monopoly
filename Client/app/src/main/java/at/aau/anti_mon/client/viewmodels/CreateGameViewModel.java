package at.aau.anti_mon.client.viewmodels;

import androidx.lifecycle.ViewModel;

import lombok.Getter;

@Getter
public class CreateGameViewModel extends ViewModel {
    private final SingleLiveEvent<String> pinLiveData = new SingleLiveEvent<>();

    public void createGame(String pin) {
        pinLiveData.postValue(pin);
    }
}
