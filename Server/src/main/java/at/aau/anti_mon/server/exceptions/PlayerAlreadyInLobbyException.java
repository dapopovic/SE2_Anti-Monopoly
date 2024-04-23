package at.aau.anti_mon.server.exceptions;

public class PlayerAlreadyInLobbyException extends Exception {
    public PlayerAlreadyInLobbyException() {
        super("Spieler ist bereits in der Lobby!");
    }
}