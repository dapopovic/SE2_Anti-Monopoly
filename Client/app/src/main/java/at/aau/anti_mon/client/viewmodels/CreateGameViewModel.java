package at.aau.anti_mon.client.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import lombok.Getter;

@Getter
public class CreateGameViewModel extends ViewModel {
    private final MutableLiveData<String> pinLiveData = new MutableLiveData<>();

    public void createGame(String pin) {
        pinLiveData.postValue(pin);
    }
}
