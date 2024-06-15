package at.aau.anti_mon.server.service;

import at.aau.anti_mon.server.exceptions.UserNotFoundException;
import at.aau.anti_mon.server.game.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handling aller Aspekte der User
 * - Erstellung, Speicherung, Verwaltung und Zuordnung von Usern
 */
@Service
public class UserService {


    /**
     * Map aller Benutzer
     * Key: UserID
     * Value: User-Objekt
     */
    private final Map<String, User> users;

    private final SessionManagementService sessionManagementService;

    @Autowired
    public UserService(SessionManagementService sessionManagementService) {
        this.users = new ConcurrentHashMap<>();
        this.sessionManagementService = sessionManagementService;
    }

    /**
     * Findet einen Benutzer anhand der UserID oder erstellt einen neuen Benutzer.
     * @param userId Die UserID des gesuchten oder zu erstellenden Benutzers.
     * @param webSocketSession Die WebSocketSession, die dem Benutzer zugeordnet wird.
     * @return Der gefundene oder erstellte Benutzer.
     */
    public User findOrCreateUser(String userId, WebSocketSession webSocketSession) {
        return users.computeIfAbsent(userId, id -> new User(id, webSocketSession));
    }

    /**
     * Findet einen Benutzer anhand des Benutzernamens.
     * @param userId Der Benutzername des gesuchten Benutzers.
     * @return Der gefundene User oder null, wenn kein Benutzer gefunden wurde.
     */
    public Optional<User> getOptionalUser(String userId) {
        return Optional.ofNullable(users.get(userId));
    }

    /**
     * Findet einen Benutzer anhand des Benutzernamens.
     * @param userID Der Benutzername des gesuchten Benutzers.
     * @return Der gefundene User oder null, wenn kein Benutzer gefunden wurde.
     * @throws UserNotFoundException Wenn kein Benutzer mit der angegebenen UserID gefunden wurde.
     */
    public User getUser(String userID) throws UserNotFoundException{
        if (!users.containsKey(userID)) {
            throw new UserNotFoundException("User mit ID " + userID + " nicht gefunden.");
        }
        return users.get(userID);
    }

    /**
     * Registriert einen neuen Benutzer und weist ihm eine Session zu.
     * @param userId Der Benutzername des neuen Benutzers.
     * @param session Die WebSocketSession, die dem Benutzer zugeordnet wird.
     */
    public User createUser(String userId, WebSocketSession session) {
        User newUser = new User(userId, session);
        if (users.putIfAbsent(userId, newUser) != null) {
            throw new IllegalStateException("User mit Username " + userId + " existiert bereits.");
        }
        sessionManagementService.registerUserWithSession(userId, session);
        return newUser;
    }

    /**
     * Entfernt einen Benutzer und seine Session.
     * @param userId Der Benutzername des zu entfernenden Benutzers.
     * @throws UserNotFoundException Wenn kein Benutzer mit der angegebenen UserID gefunden wurde.
     */
    public void removeUser(String userId) throws UserNotFoundException{
        if (!users.containsKey(userId)) {
            throw new UserNotFoundException("User mit ID " + userId + " nicht gefunden.");
        }

        users.remove(userId);
    }
}
