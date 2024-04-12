package at.aau.anti_mon.client.command;

import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
    private Map<String, Command> commandMap;

    public CommandFactory() {
        commandMap = new HashMap<>();
        commandMap.put("ANSWER", new AnswerCommand());
        commandMap.put("Test", new JoinGameCommand());
        commandMap.put("HEARTBEAT", new HeartBeatCommand());
        // weitere Commands hinzufügen
    }

    public Command getCommand(String commandType) {
        return commandMap.get(commandType);
    }
}
