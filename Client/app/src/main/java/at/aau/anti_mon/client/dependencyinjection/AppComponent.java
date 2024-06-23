package at.aau.anti_mon.client.dependencyinjection;

import javax.inject.Singleton;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.command.CommandFactory;
import at.aau.anti_mon.client.networking.WebSocketClient;
import at.aau.anti_mon.client.ui.creategame.CreateGameActivity;
import at.aau.anti_mon.client.ui.gameboard.GameBoardActivity;
import at.aau.anti_mon.client.ui.joingame.JoinGameActivity;
import at.aau.anti_mon.client.ui.lobby.LobbyActivity;
import at.aau.anti_mon.client.ui.startmenu.StartMenuActivity;
import at.aau.anti_mon.client.utilities.GlobalEventQueue;
import at.aau.anti_mon.client.utilities.MessagingUtility;
import at.aau.anti_mon.client.utilities.PreferenceManager;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * This is a Dagger component. Refer to {@link AntiMonopolyApplication} for the list of Dagger components
 */
@Singleton
@Component(
        modules = {
                AppModule.class,
                ViewModelModule.class,
                CommandModule.class,
                LiveDataModule.class,
                ActivityBindingModule.class,
                AndroidSupportInjectionModule.class
        })
public interface AppComponent {


    @Component.Factory
    interface Factory {
        AppComponent create(AppModule module);
    }

    ActivityComponent.Factory activityComponentFactory();

    void inject(AntiMonopolyApplication antiMonopolyApplication);
    void inject(JoinGameActivity joinGameActivity);
    void inject(GameBoardActivity gameBoardActivity);
    void inject(LobbyActivity lobbyActivity);
    void inject(StartMenuActivity startMenuActivity);
    void inject(CreateGameActivity createGameActivity);

    WebSocketClient getWebSocketClient();
    PreferenceManager getPreferenceManager();
    GlobalEventQueue getGlobalEventQueue();
    MessagingUtility getMessagingService();
    CommandFactory getCommandFactory();

}