package at.aau.anti_mon.client;

import android.app.Application;

import javax.inject.Inject;

import at.aau.anti_mon.client.command.CommandFactory;
import at.aau.anti_mon.client.dependencyinjection.AppComponent;
import at.aau.anti_mon.client.dependencyinjection.AppModule;
import at.aau.anti_mon.client.dependencyinjection.DaggerAppComponent;
import at.aau.anti_mon.client.networking.WebSocketClient;
import at.aau.anti_mon.client.utilities.GlobalEventQueue;
import at.aau.anti_mon.client.utilities.MessagingUtility;
import at.aau.anti_mon.client.utilities.PreferenceManager;
import lombok.Getter;


/**
 * The AntiMonopolyApplication is the entry point of the application.
 * It is responsible for creating the Dagger AppComponent and the GlobalEventQueue.
 */
@Getter
public class AntiMonopolyApplication extends Application {

    public static final String DEBUG_TAG = "ANTI-MONOPOLY-DEBUG";
    public AppComponent appComponent;

    @Inject GlobalEventQueue globalEventQueue;
    @Inject CommandFactory commandFactory;
    @Inject PreferenceManager preferenceManager;
    @Inject WebSocketClient webSocketClient;
    @Inject MessagingUtility messagingService;


    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.factory().create(new AppModule(this));
        appComponent.inject(this);
        globalEventQueue.setEventBusReady(true);
        webSocketClient.connectToServer();
    }

}
