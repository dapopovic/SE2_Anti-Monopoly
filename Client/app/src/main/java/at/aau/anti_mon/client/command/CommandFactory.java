package at.aau.anti_mon.client.command;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;


public class CommandFactory {
    private final Map<String, Command> commandMap;

    public CommandFactory(Map<String, Command> commandMap) {
        this.commandMap = new HashMap<>(commandMap);
    }

    public Command getCommand(String commandType) {
        return commandMap.get(commandType);
    }
}