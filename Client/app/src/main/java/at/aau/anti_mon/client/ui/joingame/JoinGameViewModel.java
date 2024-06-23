package at.aau.anti_mon.client.ui.joingame;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import at.aau.anti_mon.client.ui.base.BaseViewModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinGameViewModel extends BaseViewModel {

    MutableLiveData<String> infoLiveData = new MutableLiveData<>();
    MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public JoinGameViewModel(Application application) {
        super(application);
    }


}
