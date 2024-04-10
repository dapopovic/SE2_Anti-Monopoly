package at.aau.anti_mon.server.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Commands {
    ANSWER("ANSWER"),
    CREATE_GAME("CREATE_GAME"),
    JOIN_GAME("JOIN_GAME");
    // usw


    private final String command;

    Commands(String command) {
        this.command = command;
    }

    @JsonValue
    public String getCommand() {
        return command;
    }
}
