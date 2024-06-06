package at.aau.anti_mon.client;

import javax.inject.Singleton;

import at.aau.anti_mon.client.activities.ActivityGameField;
import at.aau.anti_mon.client.activities.JoinGameActivity;
import at.aau.anti_mon.client.activities.LobbyActivity;
import at.aau.anti_mon.client.activities.MainActivity;
import at.aau.anti_mon.client.activities.StartNewGameActivity;
import at.aau.anti_mon.client.game.GameController;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.NetworkModule;
import at.aau.anti_mon.client.networking.WebSocketClient;
import at.aau.anti_mon.client.viewmodels.GameFieldViewModel;
import dagger.BindsInstance;
import dagger.Component;

/**
 * This is a Dagger component. Refer to {@link AntiMonopolyApplication} for the list of Dagger components
 */
@Singleton
@Component(modules = {NetworkModule.class})
public interface AppComponent {
    void inject(MainActivity mainActivity);

    // This tells Dagger that StartNewGameActivity requests injection so the graph needs to
    // satisfy all the dependencies of the fields that StartNewGameActivity is injecting.
    void inject(StartNewGameActivity startNewGameActivity);

    void inject(AntiMonopolyApplication antiMonopolyApplication);

    void inject (LobbyActivity lobbyActivity);

    void inject (JoinGameActivity joinGameActivity);

    void inject(ActivityGameField activityGameField);

    void inject(GameFieldViewModel gameFieldViewModel);

    void inject(GameController gameController);

    void inject(JsonDataManager jsonDataManager);

    WebSocketClient getWebSocketClient();

    //JsonDataManager getJsonDataManager();

}