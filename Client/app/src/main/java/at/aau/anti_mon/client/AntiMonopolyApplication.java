package at.aau.anti_mon.client;

import android.app.Application;

import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.networking.NetworkModule;
import lombok.Getter;
import lombok.Setter;


/**
 * The AntiMonopolyApplication is the entry point of the application.
 * It is responsible for creating the Dagger AppComponent and the GlobalEventQueue.
 */
@Getter
public class AntiMonopolyApplication extends Application {

    public static final String DEBUG_TAG = "ANTI-MONOPOLY-DEBUG";
    private AppComponent appComponent;
    @Setter
    private GlobalEventQueue globalEventQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .networkModule(new NetworkModule(this))
                .build();
        globalEventQueue = new GlobalEventQueue();
        globalEventQueue.setEventBusReady(true);
    }

}
