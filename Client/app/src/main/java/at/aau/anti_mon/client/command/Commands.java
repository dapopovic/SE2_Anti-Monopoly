package at.aau.anti_mon.client.command;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;



/**
 * Enum that represents the different commands that can be sent to the server
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Commands {
    ANSWER("ANSWER"),
    PIN("PIN"),
    CREATE_GAME("CREATE_GAME"),
    LEAVE_GAME("LEAVE_GAME"),
    JOIN_GAME("JOIN_GAME"),
    HEARTBEAT("HEARTBEAT"),
    NEW_USER("NEW_USER"),
    INFO("INFO"),
    ERROR("ERROR"),
    DICE_NUMBER("NUMBER");
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

