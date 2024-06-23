package at.aau.anti_mon.server.commands;
import at.aau.anti_mon.server.enums.Commands;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class that creates command enums and maps them to the corresponding command classes
 */
@Getter
@Component
public class CommandFactory {

    private final Map<String, Command> commandMap;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    private CommandFactory(ApplicationEventPublisher eventPublisher) {
        this.commandMap = new HashMap<>();
        this.eventPublisher = eventPublisher;
    }

    /**
     * Initialize the CommandFactory with all commands
     * Prevents that a command is not found while building the server
     */
    @PostConstruct
    public void init() {
        Logger.info("SERVER: CommandFactory created");
        commandMap.put("TEST", new TestCommand());
        commandMap.put(Commands.ANSWER.getCommand(), new TestCommand());
        commandMap.put(Commands.ERROR.getCommand(), new ErrorCommand());
        commandMap.put(Commands.INFO.getCommand(), new InfoCommand());
        commandMap.put(Commands.PIN.getCommand(), null );
        commandMap.put(Commands.NEW_USER.getCommand(), null);
        commandMap.put(Commands.SELECT_ROLE.getCommand(), null);
        commandMap.put(Commands.FINISHED_ROUND.getCommand(), null);
        commandMap.put(Commands.LOBBY_PLAYERS.getCommand(), null);
        commandMap.put(Commands.HEARTBEAT.getCommand(), new HeartBeatCommand(eventPublisher));
        commandMap.put(Commands.JOIN.getCommand(), new JoinLobbyCommand(eventPublisher));
        commandMap.put(Commands.JOIN_GAME.getCommand(), new JoinLobbyCommand(eventPublisher));
        commandMap.put(Commands.CREATE_GAME.getCommand(), new CreateGameCommand(eventPublisher));
        commandMap.put(Commands.LEAVE_GAME.getCommand(), new LeaveLobbyCommand(eventPublisher));
        commandMap.put(Commands.READY.getCommand(), new LobbyReadyCommand(eventPublisher));
        commandMap.put(Commands.START_GAME.getCommand(), new StartGameCommand(eventPublisher));
        commandMap.put(Commands.DICENUMBER.getCommand(), new DiceNumberCommand(eventPublisher));
        commandMap.put(Commands.CHANGE_BALANCE.getCommand(), new ChangeBalanceCommand(eventPublisher));
        commandMap.put(Commands.NEXT_PLAYER.getCommand(), new NextPlayerCommand(eventPublisher));
        commandMap.put(Commands.FIRST_PLAYER.getCommand(), new FirstPlayerCommand(eventPublisher));
        commandMap.put(Commands.LOSE_GAME.getCommand(), new LoseGameCommand(eventPublisher));
        commandMap.put(Commands.END_GAME.getCommand(),  new EndGameCommand(eventPublisher));

        // Logging all Commands:
        commandMap.forEach((key, value) -> Logger.debug("SERVER: Command in map: " + key));
    }


    /**
     * Get the command by the command type
     * -> Commands.START_GAME = StartGameCommand
     * @param commandType The command type
     * @return The command
     */
    public Command getCommand(String commandType) {
        Logger.info("SERVER: CommandFactory getCommand: " + commandType);
        Command command = commandMap.get(commandType);
        if (command == null) {
            Logger.error("SERVER: Command not found: " + commandType);
        }
        return command;
    }

}