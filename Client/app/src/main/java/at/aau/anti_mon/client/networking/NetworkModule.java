package at.aau.anti_mon.client.networking;

import android.app.Application;

import java.util.Map;

import javax.inject.Singleton;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.command.AnswerCommand;
import at.aau.anti_mon.client.command.Command;
import at.aau.anti_mon.client.command.CommandFactory;
import at.aau.anti_mon.client.command.CreateGameCommand;
import at.aau.anti_mon.client.command.ErrorCommand;
import at.aau.anti_mon.client.command.HeartBeatCommand;
import at.aau.anti_mon.client.command.InfoCommand;
import at.aau.anti_mon.client.command.JoinGameCommand;
import at.aau.anti_mon.client.command.NewUserCommand;
import at.aau.anti_mon.client.command.PinCommand;
import at.aau.anti_mon.client.command.TestCommand;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import okhttp3.OkHttpClient;

/**
 * This is a Dagger module. We use this to pass in the View
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
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                // Konfiguriere den Client nach Bedarf
                .build();
    }



    @Provides
    @Singleton
    WebSocketClient provideWebSocketClient(OkHttpClient client, CommandFactory commandFactory) {
        return new WebSocketClient(client, commandFactory);
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

    @Provides
    @IntoMap
    @StringKey("ANSWER")
    Command provideAnswerCommand(AnswerCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("PIN")
    Command providePinCommand(GlobalEventQueue queue) {
        return new PinCommand(queue);
    }

    @Provides
    @IntoMap
    @StringKey("JOIN")
    Command provideJoinGameCommand(JoinGameCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("TEST")
    Command provideTestCommand(TestCommand command) {
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
    @StringKey("CREATE_GAME")
    Command provideCreateGameCommand(CreateGameCommand command) {
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
    @StringKey("INFO")
    Command provideInfoCommand(InfoCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("ERROR")
    Command provideErrorCommand(ErrorCommand command) {
        return command;
    }


}
