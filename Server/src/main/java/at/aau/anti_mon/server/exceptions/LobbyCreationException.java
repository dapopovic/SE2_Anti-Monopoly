package at.aau.anti_mon.server.exceptions;

/**
 * Exception that is thrown when a lobby could not be created
 */
public class LobbyCreationException extends Exception{
    public LobbyCreationException(String message) {
        super(message);
    }
}
