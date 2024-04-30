package at.aau.anti_mon.server.exceptions;

public class CanNotExecuteJsonCommandException  extends RuntimeException{
    public CanNotExecuteJsonCommandException(String message) {
        super(message);
    }
}
