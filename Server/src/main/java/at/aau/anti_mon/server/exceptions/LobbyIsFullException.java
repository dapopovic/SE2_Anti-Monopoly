package at.aau.anti_mon.server.exceptions;

public class LobbyIsFullException extends Exception {
    public LobbyIsFullException() {
        super("Lobby ist voll!");
    }
}