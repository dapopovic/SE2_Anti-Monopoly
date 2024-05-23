package at.aau.anti_mon.client.networking;

import android.app.Application;

import com.itkacher.okprofiler.BuildConfig;
import com.localebro.okhttpprofiler.OkHttpProfilerInterceptor;

import java.util.Map;

import javax.inject.Singleton;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.command.Command;
import at.aau.anti_mon.client.command.CommandFactory;
import at.aau.anti_mon.client.command.CreateGameCommand;
import at.aau.anti_mon.client.command.ErrorCommand;
import at.aau.anti_mon.client.command.HeartBeatCommand;
import at.aau.anti_mon.client.command.JoinGameCommand;
import at.aau.anti_mon.client.command.LeaveGameCommand;
import at.aau.anti_mon.client.command.NewUserCommand;
import at.aau.anti_mon.client.command.OnReadyCommand;
import at.aau.anti_mon.client.command.PinCommand;
import at.aau.anti_mon.client.command.StartGameCommand;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.game.GameController;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.viewmodels.CreateGameViewModel;
import at.aau.anti_mon.client.viewmodels.GameFieldViewModel;
import at.aau.anti_mon.client.viewmodels.LobbyViewModel;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import okhttp3.OkHttpClient;

/**
 * This is a Dagger module.
 */
@Module
public class NetworkModule {


    private final AntiMonopolyApplication application;

    public NetworkModule(AntiMonopolyApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }

    @Provides
    @Singleton
    AntiMonopolyApplication provideAntiMonopolyApplication() {
        return application;
    }

    @Provides
    @Singleton
    JsonDataManager provideJsonDataManager(WebSocketClient webSocketClient) {
        return new JsonDataManager(webSocketClient);
    }

    @Provides
    @Singleton
    public static OkHttpClient provideOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(new OkHttpProfilerInterceptor());
        }
        return builder.build();
    }

    @Provides
    @Singleton
    WebSocketClient provideWebSocketClient(OkHttpClient client, CommandFactory commandFactory) {
        return new WebSocketClient(client, commandFactory);
    }

    @Provides
    @Singleton
    GameController provideGameController() {
        return new GameController();
    }

    @Provides
    @Singleton
    GlobalEventQueue provideGlobalEventQueue(Application application) {
        return ((AntiMonopolyApplication) application).getGlobalEventQueue();
    }

    @Provides
    @Singleton
    CommandFactory provideCommandFactory(Map<String, Command> commands) {
        return new CommandFactory(commands);
    }

    /////////////////////////////////////////////////////////////////////////// ViewModels

    @Provides
    @Singleton
    GameFieldViewModel provideGameFieldViewModel() {
        return new GameFieldViewModel();
    }

    @Provides
    @Singleton
    LobbyViewModel provideLobbyViewModel() {
        return new LobbyViewModel();
    }

    @Provides
    @Singleton
    CreateGameViewModel provideCreateGameViewModel() {
        return new CreateGameViewModel();
    }


    /////////////////////////////////////////////////////////////////////////// Commands

    @Provides
    @IntoMap
    @StringKey("PIN")
    Command providePinCommand(PinCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("ERROR")
    Command provideErrorCommand(ErrorCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("HEARTBEAT")
    Command provideHeartBeatCommand(HeartBeatCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("NEW_USER")
    Command provideNewUserCommand(NewUserCommand command) {
        return command;
    }


    @Provides
    @IntoMap
    @StringKey("CREATE_GAME")
    Command provideCreateGameCommand(CreateGameCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("JOIN_GAME")
    Command provideJoinGameCommand(JoinGameCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("LEAVE_GAME")
    Command provideLeaveGameCommand(LeaveGameCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("READY")
    Command provideReadyCommand(OnReadyCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("START_GAME")
    Command provideStartGameCommand(StartGameCommand command) {
        return command;
    }


}
