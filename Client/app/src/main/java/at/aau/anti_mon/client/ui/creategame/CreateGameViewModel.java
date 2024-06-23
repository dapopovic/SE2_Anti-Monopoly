package at.aau.anti_mon.client.ui.creategame;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import at.aau.anti_mon.client.ui.base.BaseViewModel;
import at.aau.anti_mon.client.utilities.SingleLiveEvent;
import lombok.Getter;

@Getter
public class CreateGameViewModel extends BaseViewModel {
    private final SingleLiveEvent<String> pinLiveData = new SingleLiveEvent<>();
    MutableLiveData<String> infoLiveData = new MutableLiveData<>();
    MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public CreateGameViewModel(Application application) {
        super(application);
    }

    public void createGame(String pin) {
        pinLiveData.postValue(pin);
    }

}
