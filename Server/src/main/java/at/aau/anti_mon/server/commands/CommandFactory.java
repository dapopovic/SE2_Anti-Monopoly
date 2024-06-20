package at.aau.anti_mon.server.commands;
import at.aau.anti_mon.server.enums.Commands;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class to create commands
 */
@Component
public class CommandFactory {

    private final Map<String, Command> commandMap;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public CommandFactory( ApplicationEventPublisher eventPublisher) {
        this.commandMap = new HashMap<>();
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void init() {
        Logger.info("SERVER: CommandFactory created");
        commandMap.put("TEST", new TestCommand());
        commandMap.put(Commands.ANSWER.getCommand(), new TestCommand());
        commandMap.put(Commands.ERROR.getCommand(), new TestCommand());
        commandMap.put(Commands.INFO.getCommand(), new TestCommand());
        commandMap.put(Commands.PIN.getCommand(), new TestCommand());
        commandMap.put(Commands.NEW_USER.getCommand(), new TestCommand());
        commandMap.put(Commands.SELECT_ROLE.getCommand(), new TestCommand());
        commandMap.put(Commands.FINISHED_ROUND.getCommand(), new TestCommand());
        commandMap.put(Commands.LOBBY_PLAYERS.getCommand(), new TestCommand());
        commandMap.put(Commands.HEARTBEAT.getCommand(), new HeartBeatCommand(eventPublisher));
        commandMap.put(Commands.JOIN.getCommand(), new JoinLobbyCommand(eventPublisher));
        commandMap.put(Commands.JOIN_GAME.getCommand(), new JoinLobbyCommand(eventPublisher));
        commandMap.put(Commands.CREATE_GAME.getCommand(), new CreateGameCommand(eventPublisher));
        commandMap.put(Commands.LEAVE_GAME.getCommand(), new LeaveLobbyCommand(eventPublisher));
        commandMap.put(Commands.READY.getCommand(), new LobbyReadyCommand(eventPublisher));
        commandMap.put(Commands.START_GAME.getCommand(), new StartGameCommand(eventPublisher));
        commandMap.put(Commands.DICENUMBER.getCommand(), new DiceNumberCommand(eventPublisher));
        commandMap.put(Commands.RANDOM_DICE.getCommand(), new DiceNumberCommand(eventPublisher));
        commandMap.put(Commands.DICE.getCommand(), new DiceNumberCommand(eventPublisher));
        commandMap.put(Commands.CHANGE_BALANCE.getCommand(), new ChangeBalanceCommand(eventPublisher));
        commandMap.put(Commands.NEXT_PLAYER.getCommand(), new NextPlayerCommand(eventPublisher));
        commandMap.put(Commands.FIRST_PLAYER.getCommand(), new FirstPlayerCommand(eventPublisher));

        // Logging all Commands:
        commandMap.forEach((key, value) -> Logger.debug("SERVER: Command in map: " + key));
    }

    public Command getCommand(String commandType) {
        Logger.info("SERVER: CommandFactory getCommand: " + commandType);
        Command command = commandMap.get(commandType);
        if (command == null) {
            Logger.error("SERVER: Command not found: " + commandType);
        }
        return command;
    }
}
