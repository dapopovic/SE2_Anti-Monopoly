package at.aau.anti_mon.client.dependencyinjection;

import static at.aau.anti_mon.client.AntiMonopolyApplication.DEBUG_TAG;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import java.util.Map;

import javax.inject.Provider;
import javax.inject.Singleton;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.networking.WebSocketClient;
import at.aau.anti_mon.client.ui.creategame.CreateGameViewModel;
import at.aau.anti_mon.client.ui.gameboard.GameBoardViewModel;
import at.aau.anti_mon.client.ui.joingame.JoinGameViewModel;
import at.aau.anti_mon.client.ui.lobby.LobbyViewModel;
import at.aau.anti_mon.client.ui.startmenu.StartMenuViewModel;
import at.aau.anti_mon.client.utilities.GlobalEventQueue;
import at.aau.anti_mon.client.utilities.MessagingUtility;
import at.aau.anti_mon.client.utilities.PreferenceManager;
import at.aau.anti_mon.client.utilities.UserManager;
import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module.
 * "@Provides" : This annotation is used to tell Dagger that the method provides the return type if the object is requested.
 * "@IntoMap"  : This annotation tells Dagger that the method provides a value that should be put into a map.
 * "@StringKey": This annotation is used to provide a key for the map.
 */
@Module
public class AppModule {

    private final AntiMonopolyApplication application;

    public AppModule(AntiMonopolyApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    WebSocketClient provideWebSocketClient() {
        Log.d(DEBUG_TAG, "provideWebSocketClient: WebSocketClient created");
        return new WebSocketClient();
    }

    @Provides
    @Singleton
    MessagingUtility provideMessagingService(WebSocketClient webSocketClient) {
        MessagingUtility messagingService = MessagingUtility.getInstance();
        messagingService.initialize(webSocketClient);
        return messagingService;
    }

    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application application) {
        return application.getSharedPreferences("preferences", Context.MODE_PRIVATE);
    }


    @Provides
    @Singleton
    GlobalEventQueue provideGlobalEventQueue() {
        Log.d(DEBUG_TAG, "provideGlobalEventQueue: GlobalEventQueue created");
        return GlobalEventQueue.getInstance();
    }

    @Provides
    @Singleton
    PreferenceManager providePreferenceManager(Application application) {
        return PreferenceManager.getInstance(application);
    }

    @Provides
    @Singleton
    UserManager provideUserManager() {
        return UserManager.getInstance();
    }

    /////////////////////////////////////////////////////////////////////////// ViewModels

    @Provides
    @Singleton
    ViewModelFactory provideViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> creators) {
        return new ViewModelFactory(creators);
    }

    @Provides
    @Singleton
    LobbyViewModel provideLobbyViewModel(Application application) {
        return new LobbyViewModel(application);
    }

    @Provides
    @Singleton
    CreateGameViewModel provideCreateGameViewModel(Application application) {
        return new CreateGameViewModel(application);
    }

    @Provides
    @Singleton
    JoinGameViewModel provideJoinGameViewModel(Application application) {
        return new JoinGameViewModel(application);
    }

    @Provides
    @Singleton
    GameBoardViewModel provideGameFieldViewModel(Application application) {
        return new GameBoardViewModel(application);
    }

    @Provides
    @Singleton
    StartMenuViewModel provideStartMenuViewModel(Application application) {
        return new StartMenuViewModel(application);
    }

}
