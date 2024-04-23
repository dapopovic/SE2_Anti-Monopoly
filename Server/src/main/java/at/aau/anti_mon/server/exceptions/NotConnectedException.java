package at.aau.anti_mon.server.exceptions;

public class NotConnectedException extends Exception {
    public NotConnectedException() {
        super("Spieler ist nicht mehr verbunden!");
    }
}