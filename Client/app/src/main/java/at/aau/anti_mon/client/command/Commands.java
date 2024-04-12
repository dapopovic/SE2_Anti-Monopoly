package at.aau.anti_mon.client.command;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;



/**
 * Enum that represents the different commands that can be sent to the server
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Commands {
    ANSWER("ANSWER"),
    CREATE_GAME("CREATE_GAME"),
    JOIN_GAME("JOIN_GAME"),
    HEARTBEAT("HEARTBEAT");
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

