package at.aau.anti_mon.server.exceptions;

/**
 * Exception that is thrown when a user already exists
 */
public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}