package at.aau.anti_mon.client.integrationtests;

import static org.mockito.Mockito.mock;

import android.app.Application;

import androidx.lifecycle.ViewModel;

import java.util.Map;

import javax.annotation.Nullable;
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
import at.aau.anti_mon.client.command.LeaveGameCommand;
import at.aau.anti_mon.client.command.NewUserCommand;
import at.aau.anti_mon.client.command.PinCommand;
import at.aau.anti_mon.client.command.TestCommand;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.networking.NetworkModule;
import at.aau.anti_mon.client.viewmodels.LobbyViewModel;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;


@Module
public class FakeNetworkModule extends NetworkModule {
    public FakeNetworkModule(AntiMonopolyApplication application) {
        super(application);
    }

    @Provides
    GlobalEventQueue provideGlobalEventQueue() {
        return mock(GlobalEventQueue.class);
    }

//    @Provides
//    @Singleton
//    CommandFactory provideCommandFactory(Map<String, Command> commands) {
//        return new CommandFactory(commands);
//    }
//
//    @Provides
//    @IntoMap
//    @StringKey("ANSWER")
//    Command provideAnswerCommand(AnswerCommand command) {
//        return command;
//    }
//
//    @Provides
//    @IntoMap
//    @StringKey("PIN")
//    Command providePinCommand(GlobalEventQueue queue) {
//        return new PinCommand(queue);
//    }
//
//
//    @Provides
//    @IntoMap
//    @StringKey("INFO")
//    Command provideInfoCommand(InfoCommand command) {
//        return command;
//    }
//
//    @Provides
//    @IntoMap
//    @StringKey("ERROR")
//    Command provideErrorCommand(ErrorCommand command) {
//        return command;
//    }
//
//    @Provides
//    @IntoMap
//    @StringKey("TEST")
//    Command provideTestCommand(TestCommand command) {
//        return command;
//    }
//
//    @Provides
//    @IntoMap
//    @StringKey("HEARTBEAT")
//    Command provideHeartBeatCommand(HeartBeatCommand command) {
//        return command;
//    }
//
//    @Provides
//    @IntoMap
//    @StringKey("NEW_USER")
//    Command provideNewUserCommand(NewUserCommand command) {
//        return command;
//    }
//
//
//    @Provides
//    @IntoMap
//    @StringKey("CREATE_GAME")
//    Command provideCreateGameCommand(CreateGameCommand command) {
//        return command;
//    }
//
//    @Provides
//    @IntoMap
//    @StringKey("JOIN_GAME")
//    Command provideJoinGameCommand(JoinGameCommand command) {
//        return command;
//    }
//
//    @Provides
//    @IntoMap
//    @StringKey("LEAVE_GAME")
//    Command provideLeaveGameCommand(LeaveGameCommand command) {
//        return command;
//    }
//    @Provides
//    @Singleton
//    LobbyViewModel provideLobbyViewModel() {
//        return mock(LobbyViewModel.class);
//    }

}
