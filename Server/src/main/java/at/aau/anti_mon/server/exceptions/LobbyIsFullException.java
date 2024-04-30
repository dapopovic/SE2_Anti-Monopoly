package at.aau.anti_mon.server.exceptions;

/**
 * Exception that is thrown when a lobby could not be created because it is full
 */
public class LobbyIsFullException extends Exception {
    public LobbyIsFullException(String message) {
        super(message);
    }

}
