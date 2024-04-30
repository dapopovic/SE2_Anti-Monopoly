package at.aau.anti_mon.server.exceptions;

/**
 * Exception that is thrown when a user could not be found
 */
public class UserNotFoundException extends Exception{

    public UserNotFoundException(String message) {
        super(message);
    }
}
