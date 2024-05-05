package at.aau.anti_mon.server.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum that represents the different commands that can be sent to the server
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Commands {
    ANSWER("ANSWER"),
    PIN("PIN"),
    NEW_USER("NEW_USER"),
    CREATE_GAME("CREATE_GAME"),
    JOIN("JOIN"),
    LEAVE_GAME("LEAVE_GAME"),
    HEARTBEAT("HEARTBEAT"),
    INFO("INFO"),
    LOBBY_PLAYERS("LOBBY_PLAYERS"),
    READY("READY"),
    ERROR("ERROR");

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
