package at.aau.anti_mon.client;

import android.app.Application;

import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.networking.NetworkModule;
import lombok.Getter;

@Getter
public class AntiMonopolyApplication extends Application {

    private AppComponent appComponent;
    private static AntiMonopolyApplication instance;
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
