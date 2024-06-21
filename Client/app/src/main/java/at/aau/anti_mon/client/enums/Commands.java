package at.aau.anti_mon.client.enums;

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

    /**
     * Command to get a PIN ? TODO: What is this, same as CREATE_GAME? -> fix!
     * format: {"command": "REGISTER", "user": "TestUser"}
     */
    PIN("PIN"),


    /**
     * Command to create a new game TODO: Better name: CREATE_LOBBY ?
     * format: {"command": "CREATE_GAME", "user": "TestUser"}
     */
    CREATE_GAME("CREATE_GAME"),

    /**
     * Command to leave a game
     * format: {"command": "LEAVE_GAME", "user": "TestUser", "pin": "1234"}
     */
    LEAVE_GAME("LEAVE_GAME"),

    /**
     * Command to join a game
     * format: {"command": "JOIN_GAME", "user": "TestUser", "pin": "1234"}
     */
    JOIN("JOIN"),

    /**
     * Command to join a game
     * format: {"command": "JOIN_GAME", "user": "TestUser", "pin": "1234"}
     */
    JOIN_GAME("JOIN_GAME"),

    /**
     * Heartbeat-Command to keep the connection alive
     * format: {"command": "HEARTBEAT", "msg": "Hello World!"}
     */
    HEARTBEAT("HEARTBEAT"),

    /**
     * Command to send a new user in the lobby to clients
     * format: {"command": "NEW_USER", "user": "TestUser"}
     */
    NEW_USER("NEW_USER"),

    /**
     * Command for debugging
     * format: {"command": "INFO", "msg": "Hello World!"}
     */
    INFO("INFO"),

    /**
     * Command for debugging
     * format: {"command": "ERROR", "msg": "Hello World!"}
     */
    ERROR("ERROR"),

    /**
     * Send by client to finish the round
     * format: {"command": "FINISHED_ROUND", "user": "TestUser", "pin": "1234"}
     * TODO: better attributes?
     */
    FINISHED_ROUND("FINISHED_ROUND"),

    /**
     * Command to get all players in the lobby
     * format: {"command": "LOBBY_PLAYERS"}
     */
    LOBBY_PLAYERS("LOBBY_PLAYERS"),

    /**
     * TODO: TESTING
     */
    RANDOM_DICE("RANDOM_DICE"),

    /**
     * Send by client to set the dice number
     * format: {"command": "DICE", "user": "TestUser", "pin": "1234", "dicenumber": 5}
     */
    DICE("DICE"),

    /**
     * Send by client to set the dice number
     * format: {"command": "DICENUMBER", "user": "TestUser", "pin": "1234", "dicenumber": 5}
     */
    DICENUMBER("DICENUMBER"),

    /**
     * Send by client to set the ready state
     * format: {"command": "READY", "user": "TestUser", "pin": "1234", "isReady": true}
     */
    READY("READY"),

    /**
     * Send by client to start the game
     */
    START_GAME("START_GAME"),

    /**
     * Send by client choose a role
     * format: {"command": "SELECT_ROLE", "user" : "TestUser", "pin": "1234" ,role": "TestRole"}
     */
    SELECT_ROLE("SELECT_ROLE"),

    FIRST_PLAYER("FIRST_PLAYER"),

    /**
     * Send by client to change the balance
     * format: {"command": "CHANGE_BALANCE", "user" : "TestUser", "pin": "1234" ,balance": 100}
     */
    CHANGE_BALANCE("CHANGE_BALANCE");



    private final String command;

    Commands(String command) {
        this.command = command;
    }

    @JsonValue
    public String getCommand() {
        return command;
    }
}

