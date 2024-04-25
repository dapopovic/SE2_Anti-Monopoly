package at.aau.anti_mon.server.service;

import at.aau.anti_mon.server.exceptions.LobbyIsFullException;
import at.aau.anti_mon.server.exceptions.LobbyNotFoundException;
import at.aau.anti_mon.server.exceptions.UserNotFoundException;
import at.aau.anti_mon.server.game.Lobby;
import at.aau.anti_mon.server.game.User;
import at.aau.anti_mon.server.websocket.manager.JsonDataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tinylog.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Lobbyservice kapselt die Logik für das Erstellen und Verwalten von Lobbys
 * und kann von verschiedenen Teilen des Systems verwendet werden.
 */
@Service
public class LobbyService {

    /**
     * Map aller Lobbys
     * K : PIN der Lobby
     * V : Lobby
     */
    private final Map<Integer, Lobby> lobbies;

    /**
     * Map aller Benutzer und ihrer Lobby-Zugehörigkeit
     * K : Benutzername
     * V : PIN der Lobby
     */
    private final Map<String, Integer> userLobbyMap;


    private final UserService userService;

    @Autowired
    public LobbyService(UserService userService
            //SimpMessagingTemplate messagingTemplate
    ) {
        //this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.lobbies = new ConcurrentHashMap<>();
        this.userLobbyMap= new ConcurrentHashMap<>();
    }

    public void addUserToLobby(String userId, int lobbyId) {
        userLobbyMap.put(userId, lobbyId);
    }

    public Integer getLobbyIDForUserID(String userId) {
        return userLobbyMap.get(userId);
    }

    public void removeUserFromLobby(String userId) {
        userLobbyMap.remove(userId);
    }

    /**
     * Erstellt eine neue Lobby und fügt den Ersteller (User) hinzu
     * TODO: Verbesserung des Handling bei bereits bestehendem PIN!
     * @param user User
     */
    public Lobby createLobby(User user) {
        Lobby newLobby = new Lobby(user);
        userLobbyMap.put(user.getName(), newLobby.getPin());

        Lobby existing = lobbies.putIfAbsent(newLobby.getPin(), newLobby);
        if (existing != null) {
            throw new IllegalStateException("Lobby mit PIN " + newLobby.getPin() + " existiert bereits.");
        }
        return newLobby;
    }

    /**
     * Fügt einen Benutzer zu einer Lobby hinzu
     * @param lobbyPin PIN der Lobby
     * @param userName Name des Benutzers
     * @throws UserNotFoundException wenn der Benutzer nicht gefunden wird
     */
    public void joinLobby(int lobbyPin, String userName) throws UserNotFoundException, LobbyIsFullException, LobbyNotFoundException {
        Lobby lobby = findLobbyByPin(lobbyPin);
        lobby.addUser(userService.getUser(userName));
        addUserToLobby(userName, lobbyPin);
        Logger.info("SERVER: Spieler " +userName + " ist der Lobby " + lobby.getPin() + " beigetreten.");
    }

    public void leaveLobby(int lobbyPin, String userName) throws UserNotFoundException {
        Lobby lobby = lobbies.get(lobbyPin);
        lobby.removeUser(userService.getUser(userName));
        removeUserFromLobby(userName);
        Logger.info("SERVER: Spieler " +userName + " hat die Lobby  " + lobby.getPin() + "  verlassen.");
    }


    /**
     * Durchsuche die Liste der Lobbies nach der gegebenen PIN und gib die entsprechende Lobby zurück.
     *
     * @param pin PIN der Lobby
     * @return Lobby oder Exception, wenn keine Lobby mit der gegebenen PIN gefunden wurde
     */
    public Lobby findLobbyByPin(int pin) throws LobbyNotFoundException {
        for (Lobby lobby : lobbies.values()) {
            if (lobby.getPin() == pin) {
                return lobby;
            }
        }
        throw new LobbyNotFoundException("Lobby mit PIN " + pin + " nicht gefunden.");
    }

    /**
     * Durchsuche die Liste der Lobbies nach der gegebenen PIN und gib die entsprechende Lobby zurück.
     * @param pin PIN der Lobby
     * @return Lobby oder null, wenn keine Lobby mit der gegebenen PIN gefunden wurde
     */
    public Optional<Lobby> findOptionalLobbyByPin(int pin) {
        for (Lobby lobby : lobbies.values()) {
            if (lobby.getPin() == pin) {
                return Optional.of(lobby);
            }
        }
        return Optional.empty();
    }

    /**
     * Durchsuche die Liste der Lobbies nach dem gegebenen Usernamen und gib die entsprechende Lobby zurück.
     * @param name Name des Users
     * @return Player oder null, wenn kein Spieler mit dem gegebenen Namen gefunden wurde
     */
    public User findLobbyByUsername(String name) throws UserNotFoundException{
        for (Lobby lobby : lobbies.values()) {
            for (User user : lobby.getUsers()) {
                if (user.getName().equals(name)) {
                    return user;
                }
            }
        }
        throw new UserNotFoundException("User mit Name " + name + " nicht gefunden.");
    }

    /**
     * Konvertiert Nachrichtenobjekt in JSON und sendet es an alle Clients im Lobby-Channel
     * @param lobbyId ID der Lobby
     * @param message Nachrichtenobjekt
     */
    /*public void sendToLobby(String lobbyId, Object message) {
        String destination = "/topic/lobby." + lobbyId;
        messagingTemplate.convertAndSend(destination, message);
    }

     */

}