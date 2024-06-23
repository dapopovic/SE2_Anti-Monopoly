package at.aau.anti_mon.client.ui.base;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;

import at.aau.anti_mon.client.utilities.GlobalEventQueue;
import at.aau.anti_mon.client.utilities.MessagingUtility;
import at.aau.anti_mon.client.utilities.PreferenceManager;
import lombok.Getter;

@Getter
public abstract class BaseViewModel extends AndroidViewModel {

    protected final PreferenceManager preferenceManager;
    protected final GlobalEventQueue globalEventQueue;
    protected final MessagingUtility messagingUtility;

    public BaseViewModel(Application application) {
        super(application);
        this.preferenceManager = PreferenceManager.getInstance(application);
        this.messagingUtility = MessagingUtility.getInstance();
        this.globalEventQueue = GlobalEventQueue.getInstance();
    }

    public void clearObservers(LifecycleOwner lifecycleOwner) {
        // Default implementation to clear observers if needed
    }

}