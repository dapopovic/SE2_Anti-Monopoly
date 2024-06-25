package at.aau.anti_mon.client.dependencyinjection;

import androidx.lifecycle.MutableLiveData;

import java.util.Map;

import javax.inject.Singleton;

import at.aau.anti_mon.client.command.ChangeBalanceCommand;
import at.aau.anti_mon.client.command.CheatingCommand;
import at.aau.anti_mon.client.command.Command;
import at.aau.anti_mon.client.command.CommandFactory;
import at.aau.anti_mon.client.command.CreateGameCommand;
import at.aau.anti_mon.client.command.DiceNumberCommand;
import at.aau.anti_mon.client.command.EndGameCommand;
import at.aau.anti_mon.client.command.ErrorCommand;
import at.aau.anti_mon.client.command.HeartBeatCommand;
import at.aau.anti_mon.client.command.InfoCommand;
import at.aau.anti_mon.client.command.JoinGameCommand;
import at.aau.anti_mon.client.command.LeaveGameCommand;
import at.aau.anti_mon.client.command.LooseGameCommand;
import at.aau.anti_mon.client.command.NewUserCommand;
import at.aau.anti_mon.client.command.NextPlayerCommand;
import at.aau.anti_mon.client.command.OnReadyCommand;
import at.aau.anti_mon.client.command.PinCommand;
import at.aau.anti_mon.client.command.StartGameCommand;
import at.aau.anti_mon.client.command.WinGameCommand;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;

@Module
public abstract class CommandModule {


    @Provides
    @Singleton
    static CommandFactory provideCommandFactory(Map<String, Command> commands, Map<String, MutableLiveData<String>> liveDataMap) {
        CommandFactory commandFactory = CommandFactory.getInstance();
        commandFactory.initialize(commands, liveDataMap);
        return commandFactory;
    }

    @Provides
    @IntoMap
    @StringKey("PIN")
    static Command providePinCommand(PinCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("DICENUMBER")
    static Command provideDiceNumberCommand(DiceNumberCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("DICE")
    static Command provideDiceCommand(DiceNumberCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("NEW_USER")
    static Command provideNewUserCommand(NewUserCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("CREATE_GAME")
    static Command provideCreateGameCommand(CreateGameCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("JOIN")
    static Command provideJoinGameCommand(JoinGameCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("LEAVE_GAME")
    static Command provideLeaveGameCommand(LeaveGameCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("READY")
    static Command provideReadyCommand(OnReadyCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("START_GAME")
    static Command provideStartGameCommand(StartGameCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("INFO")
    static Command provideInfoCommand(InfoCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("ERROR")
    static Command provideErrorCommand(ErrorCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("HEARTBEAT")
    static Command provideHeartBeatCommand(HeartBeatCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("CHANGE_BALANCE")
    static Command provideChangeBalanceCommand(ChangeBalanceCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("NEXT_PLAYER")
    static Command provideNextPlayerCommand(NextPlayerCommand command) { return command; }

    @Provides
    @IntoMap
    @StringKey("CHEATING")
    static Command cheatingCommand(CheatingCommand command) {
        return command;
    }

    @Provides
    @IntoMap
    @StringKey("LOSE_GAME")
    static Command provideLoseGameCommand(LooseGameCommand command) { return command; }

    @Provides
    @IntoMap
    @StringKey("WIN_GAME")
    static Command provideWinGameCommand(WinGameCommand command) { return command; }

    @Provides
    @IntoMap
    @StringKey("END_GAME")
    static Command provideEndGameCommand(EndGameCommand command) { return command; }

}
