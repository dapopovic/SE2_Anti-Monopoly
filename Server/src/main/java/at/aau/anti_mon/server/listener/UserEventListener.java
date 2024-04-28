package at.aau.anti_mon.server.listener;

import at.aau.anti_mon.server.events.UserCreatedLobbyEvent;
import at.aau.anti_mon.server.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.server.events.UserLeftLobbyEvent;
import at.aau.anti_mon.server.exceptions.LobbyIsFullException;
import at.aau.anti_mon.server.exceptions.LobbyNotFoundException;
import at.aau.anti_mon.server.exceptions.UserNotFoundException;
import at.aau.anti_mon.server.game.Lobby;
import at.aau.anti_mon.server.game.User;
import at.aau.anti_mon.server.service.LobbyService;
import at.aau.anti_mon.server.service.SessionManagementService;
import at.aau.anti_mon.server.service.UserService;
import at.aau.anti_mon.server.websocket.manager.JsonDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;

import java.util.HashSet;

/**
 * Event-Listener for user interactions
 */
@Component
public class UserEventListener {

    private final LobbyService lobbyService;
    private final SessionManagementService sessionManagementService;
    private final UserService userService;

    /**
     * Konstruktor für UserEventListener
     * Dependency Injection für LobbyService
     * @param lobbyService
     */
    @Autowired
    UserEventListener(LobbyService lobbyService,
                      SessionManagementService sessionManagementService,
                      UserService userService
    ) {
        this.lobbyService = lobbyService;
        this.sessionManagementService = sessionManagementService;
        this.userService = userService;
    }

    /**
     * Logik zum Erstellen einer Lobby
     * Der PIN der Lobby wird an den Benutzer zurückgegeben
     * @param event Ereignis
     */
    @EventListener
    public void onCreateLobbyEvent(UserCreatedLobbyEvent event){
        // create User
        User user = userService.findOrCreateUser(event.getUsername(), event.getSession());

        // create Lobby
        Lobby newLobby = lobbyService.createLobby(user);


        Logger.info("SERVER: Spiel erstellt mit PIN: " + newLobby.getPin());
        JsonDataManager.sendPin(event.getSession(), String.valueOf(newLobby.getPin()));
    }

    /**
     * Fügt den Benutzer zur Lobby hinzu
     * @param event Ereignis
     */
    @EventListener
    public void onUserJoinedLobbyEvent(UserJoinedLobbyEvent event) throws UserNotFoundException, LobbyNotFoundException, LobbyIsFullException {

        sessionManagementService.registerUserWithSession(event.getUsername(), event.getSession());

        // create User
        User joinedUser = userService.findOrCreateUser(event.getUsername(), event.getSession());

        Lobby joinedLobby = lobbyService.findLobbyByPin(event.getPin());
            if (joinedLobby.canAddPlayer()) {
                HashSet<User> users = joinedLobby.getUsers();
                for (User user : users) {

                    // Sende allen Spielern in der Lobby die Information, dass ein neuer Spieler beitretet
                    JsonDataManager.sendJoinedUser(sessionManagementService.getSessionForUser(user.getName()), joinedUser.getName());

                    // Sende dem neuen Spieler alle Spieler, die bereits in der Lobby sind
                    JsonDataManager.sendJoinedUser(event.getSession(), user.getName());
                }

                // Füge den joinedUser zur Lobby hinzu
                lobbyService.joinLobby(event.getPin(), joinedUser.getName());

                // DEBUG:
                JsonDataManager.sendAnswer(sessionManagementService.getSessionForUser(event.getUsername()), "SUCCESS");
                JsonDataManager.sendInfo(sessionManagementService.getSessionForUser(event.getUsername()), "Erfolgreich der Lobby beigetreten.");
            } else {
            JsonDataManager.sendError(sessionManagementService.getSessionForUser(event.getUsername()), "Fehler: Lobby nicht gefunden.");
        }
    }

    /**
     * Entfernt den Benutzer aus der Lobby
     * @param event Ereignis
     */
    @EventListener
    public void onLeaveLobbyEvent(UserLeftLobbyEvent event) throws UserNotFoundException, LobbyNotFoundException {

        sessionManagementService.registerUserWithSession(event.getUsername(), event.getSession());

        lobbyService.leaveLobby(event.getPin(), event.getUsername());
        Logger.info("Spieler " + event.getUsername() + " hat die Lobby verlassen.");

        HashSet<User> users = lobbyService.findLobbyByPin(event.getPin()).getUsers();
        for (User user : users) {

            // Sende allen Spielern in der Lobby die Information, dass der Spieler die Lobby verlassen hat
            JsonDataManager.sendLeavedUser(sessionManagementService.getSessionForUser(user.getName()), event.getUsername());

            // Sende dem neuen Spieler Bestätigung des Verlassens
            JsonDataManager.sendAnswer(sessionManagementService.getSessionForUser(event.getUsername()), "SUCCESS");
            JsonDataManager.sendInfo(sessionManagementService.getSessionForUser(event.getUsername()), "Erfolgreich die Lobby verlassen.");

        }
    }

}