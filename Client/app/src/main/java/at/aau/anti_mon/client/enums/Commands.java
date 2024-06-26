package at.aau.anti_mon.client.enums;

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
    JOIN_GAME("JOIN"),
    HEARTBEAT("HEARTBEAT"),
    NEW_USER("NEW_USER"),
    INFO("INFO"),
    ERROR("ERROR"),
    DICENUMBER("DICENUMBER"),
    READY("READY"), START_GAME("START_GAME"),
    CHANGE_BALANCE("CHANGE_BALANCE"),
    NEXT_PLAYER("NEXT_PLAYER"),
    FIRST_PLAYER("FIRST_PLAYER"),
    CHEATING("CHEATING"),
    WIN_GAME("WIN_GAME"),
    LOSE_GAME("LOSE_GAME"),
    END_GAME("END_GAME"),
    REPORT_CHEATING("REPORT_CHEATING");
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

