package at.aau.anti_mon.server.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum that represents the different commands that can be sent to the server
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Commands {


    /**
     * Command to send a answer, like other players after login into lobby -> success
     * format: {"command": "ANSWER", "user": "TestUser"}
     */
    ANSWER("ANSWER"),

    RANDOM_DICE("RANDOM_DICE"),

    /**
     * Command to get a PIN ? TODO: What is this, same as CREATE_GAME? -> fix!
     * format: {"command": "REGISTER", "user": "TestUser"}
     */
    PIN("PIN"),

    /**
     * Command to register a new user???? TODO: fix
     * format: {"command": "NEW_USER", "user": "TestUser"}
     */
    NEW_USER("NEW_USER"),

    /**
     * Command to create a new game TODO: Better name: CREATE_LOBBY ?
     * format: {"command": "CREATE_GAME", "user": "TestUser"}
     */
    CREATE_GAME("CREATE_GAME"),

    /**
     * Command to join a game
     * format: {"command": "JOIN_GAME", "user": "TestUser", "pin": "1234"}
     */
    JOIN_GAME("JOIN_GAME"),

    /**
     * Command to join a game
     * format: {"command": "LEAVE_GAME", "user": "TestUser", "pin": "1234"}
     */
    JOIN("JOIN"),

    LEAVE_GAME("LEAVE_GAME"),

    /**
     * Command to send a message
     * format: {"command": "HEARTBEAT", "msg": "Hello World!"}
     */
    HEARTBEAT("HEARTBEAT"),

    /**
     * Command for debugging
     * format: {"command": "INFO", "msg": "Hello World!"}
     */
    INFO("INFO"),

    /**
     * Command to get all players in the lobby
     * format: {"command": "LOBBY_PLAYERS"} TODO: What is this?
     */
    LOBBY_PLAYERS("LOBBY_PLAYERS"),

    /**
     * Command for debugging
     * format: {"command": "ERROR", "msg": "Hello World!"}
     */
    ERROR("ERROR"),

    ///////////////////////////////////////////////// NEW COMMANDS //////////////////////////////////////////////////

    /**
     * Send by client to finish the round
     * format: {"command": "FINISHED_ROUND", "user": "TestUser", "pin": "1234"}
     * TODO: better attributes?
     */
    FINISHED_ROUND("FINISHED_ROUND"),

    /**
     * Send by client choose a role
     * format: {"command": "SELECT_ROLE", "user" : "TestUser", "pin": "1234" ,role": "TestRole"}
     */
    SELECT_ROLE("SELECT_ROLE"),


	/**
	*
	* Old Commands -> merge issues
	*
	*/
    READY("READY"),
    START_GAME("START_GAME"),
    DICENUMBER("DICENUMBER");

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
