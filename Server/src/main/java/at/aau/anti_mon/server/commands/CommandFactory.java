package at.aau.anti_mon.server.commands;
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
        commandMap.put("TEST", new TestCommand());
        commandMap.put("HEARTBEAT", new HeartBeatCommand(eventPublisher));
        commandMap.put("JOIN_GAME", new JoinLobbyCommand(eventPublisher));
        commandMap.put("CREATE_GAME", new CreateGameCommand(eventPublisher));
        commandMap.put("LEAVE_GAME", new LeaveLobbyCommand(eventPublisher));
        commandMap.put("LOBBY_READY", new TestCommand());
        commandMap.forEach((key, value) -> Logger.debug("SERVER: Command in map: " + key));
    }

    public Command getCommand(String commandType) {
        return commandMap.get(commandType);
    }
}
