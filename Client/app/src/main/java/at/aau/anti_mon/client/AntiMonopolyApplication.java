package at.aau.anti_mon.client;

import android.app.Application;

import javax.inject.Inject;

import at.aau.anti_mon.client.command.CommandFactory;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.manager.PreferenceManager;
import at.aau.anti_mon.client.networking.MessagingService;
import at.aau.anti_mon.client.networking.WebSocketClient;
import at.aau.anti_mon.client.dependencyinjection.AppComponent;
import at.aau.anti_mon.client.dependencyinjection.AppModule;
import at.aau.anti_mon.client.dependencyinjection.DaggerAppComponent;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;
import lombok.Getter;
import lombok.Setter;


/**
 * The AntiMonopolyApplication is the entry point of the application.
 * It is responsible for creating the Dagger AppComponent and the GlobalEventQueue.
 */
@Getter
@Setter
public class AntiMonopolyApplication extends Application implements HasAndroidInjector {

    public static final String DEBUG_TAG = "ANTI-MONOPOLY-DEBUG";
    public AppComponent appComponent;

    @Inject GlobalEventQueue globalEventQueue;
    @Inject CommandFactory commandFactory;
    @Inject PreferenceManager preferenceManager;
    @Inject WebSocketClient webSocketClient;
    @Inject MessagingService messagingService;

    @Inject DispatchingAndroidInjector<Object> androidInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.factory().create(new AppModule(this));
        appComponent.inject(this);
        globalEventQueue.setEventBusReady(true);
        webSocketClient.connectToServer();
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return androidInjector;
    }

}
