package at.aau.anti_mon.client;

import android.app.Application;

import at.aau.anti_mon.client.networking.NetworkModule;


public class AntiMonopolyApplication extends Application {

    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .networkModule(new NetworkModule())
                .build();


    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
