package at.aau.anti_mon.server.commands;
import at.aau.anti_mon.server.enums.Commands;
import org.springframework.context.ApplicationEventPublisher;
import org.tinylog.Logger;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class to create commands
 */
public class CommandFactory {

    private final Map<String, Command> commandMap;

    public CommandFactory( ApplicationEventPublisher eventPublisher) {
        this.commandMap = new HashMap<>();
        Logger.info("SERVER: CommandFactory created");
        commandMap.put("TEST", new TestCommand());
        commandMap.put(Commands.HEARTBEAT.getCommand(), new HeartBeatCommand(eventPublisher));
        commandMap.put(Commands.JOIN.getCommand(), new JoinLobbyCommand(eventPublisher));
        commandMap.put(Commands.CREATE_GAME.getCommand(), new CreateGameCommand(eventPublisher));
        commandMap.put(Commands.LEAVE_GAME.getCommand(), new LeaveLobbyCommand(eventPublisher));
        commandMap.put(Commands.READY.getCommand(), new LobbyReadyCommand(eventPublisher));
        commandMap.put(Commands.START_GAME.getCommand(), new StartGameCommand(eventPublisher));
        commandMap.forEach((key, value) -> Logger.debug("SERVER: Command in map: " + key));
    }

    public Command getCommand(String commandType) {
        return commandMap.get(commandType);
    }
}
