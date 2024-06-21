package at.aau.anti_mon.client.ui.popups;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import at.aau.anti_mon.client.game.Player;
import at.aau.anti_mon.client.ui.base.BaseViewModel;

public class PopUpActivityObjectsViewModel extends BaseViewModel {

    private final MutableLiveData<Player> player = new MutableLiveData<>();

    public PopUpActivityObjectsViewModel(Application application) {
        super(application);
    }

    public LiveData<Player> getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player.setValue(player);
    }
}