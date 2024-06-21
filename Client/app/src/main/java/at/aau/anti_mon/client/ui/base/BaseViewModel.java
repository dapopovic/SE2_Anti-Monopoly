package at.aau.anti_mon.client.ui.base;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;

import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.manager.PreferenceManager;
import at.aau.anti_mon.client.networking.MessagingService;
import lombok.Getter;

@Getter
public abstract class BaseViewModel extends AndroidViewModel {

    protected final PreferenceManager preferenceManager;
    protected final GlobalEventQueue globalEventQueue;
    protected final MessagingService messagingService;

    public BaseViewModel(Application application) {
        super(application);
        this.preferenceManager = PreferenceManager.getInstance(application);
        this.messagingService = MessagingService.getInstance();
        this.globalEventQueue = GlobalEventQueue.getInstance();
    }

    public void clearObservers(LifecycleOwner lifecycleOwner) {
        // Default implementation to clear observers if needed
    }

}