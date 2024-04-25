package at.aau.anti_mon.server.exceptions;

/**
 * Exception that is thrown when a lobby could not be found
 */
public class LobbyNotFoundException extends Exception{
    public LobbyNotFoundException(String message) {
        super(message);
    }
}
