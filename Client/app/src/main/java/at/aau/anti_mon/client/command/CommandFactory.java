package at.aau.anti_mon.client.command;

import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;


/**
 * Factory for creating commands
 * Constructor injection is used to inject all available commands from the Dagger graph (NetworkModule)
 */
public class CommandFactory {

    private static CommandFactory instance;
    private final Map<String, Command> commandMap;
    private final Map<String, MutableLiveData<String>> liveDataMap;

    private CommandFactory() {
        commandMap = new HashMap<>();
        liveDataMap = new HashMap<>();
    }

    private CommandFactory(Map<String, Command> commandMap, Map<String, MutableLiveData<String>> liveDataMap) {
        this.commandMap = new HashMap<>(commandMap);
        this.liveDataMap = new HashMap<>(liveDataMap);
    }

    public static synchronized CommandFactory getInstance(Map<String, Command> commands, Map<String, MutableLiveData<String>> errorLiveDataMap) {
        if (instance == null) {
            synchronized (CommandFactory.class) {
                if (instance == null) {
                    instance = new CommandFactory(commands,errorLiveDataMap);
                }
            }
        }
        return instance;
    }

    public static synchronized CommandFactory getInstance() {
        if (instance == null) {
            instance = new CommandFactory();
        }
        return instance;
    }

    @Inject
    public void initialize(Map<String, Command> commands, Map<String, MutableLiveData<String>> errorLiveDataMap) {
        this.commandMap.putAll(commands);
        this.liveDataMap.putAll(errorLiveDataMap);
    }

    public Command getCommand(String commandType) {
        return commandMap.get(commandType);
    }

    public MutableLiveData<String> getLiveData(String context) {
        return liveDataMap.get(context);
    }
}