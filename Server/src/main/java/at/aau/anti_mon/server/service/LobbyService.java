package at.aau.anti_mon.server.service;

import at.aau.anti_mon.server.enums.Figures;
import at.aau.anti_mon.server.enums.Roles;
import at.aau.anti_mon.server.exceptions.LobbyIsFullException;
import at.aau.anti_mon.server.exceptions.LobbyNotFoundException;
import at.aau.anti_mon.server.exceptions.UserNotFoundException;
import at.aau.anti_mon.server.game.Lobby;
import at.aau.anti_mon.server.game.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tinylog.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


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

    private final Random random = new Random();
    private static final String SERVER_PLAYER = "SERVER: Spieler ";
    int sequenceNumber = 0;
    @Autowired
    public LobbyService(UserService userService
                        //SimpMessagingTemplate messagingTemplate
    ) {
        this.userService = userService;
        this.lobbies = new ConcurrentHashMap<>();
        this.userLobbyMap = new ConcurrentHashMap<>();
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
     *
     * @param user User
     */
    public Lobby createLobby(User user) {
        Lobby newLobby = new Lobby(user);
        userLobbyMap.put(user.getName(), newLobby.getPin());

        Lobby existing = lobbies.putIfAbsent(newLobby.getPin(), newLobby);
        if (existing != null) {
            throw new IllegalStateException("Lobby mit PIN " + newLobby.getPin() + " existiert bereits.");
        }
        user.setLobby(newLobby);
        return newLobby;
    }

    /**
     * Fügt einen Benutzer zu einer Lobby hinzu
     *
     * @param lobbyPin PIN der Lobby
     * @param userName Name des Benutzers
     * @throws UserNotFoundException wenn der Benutzer nicht gefunden wird
     */
    public void joinLobby(int lobbyPin, String userName) throws UserNotFoundException, LobbyIsFullException, LobbyNotFoundException {
        Lobby lobby = findLobbyByPin(lobbyPin);
        User user = userService.getUser(userName);
        lobby.addUser(user);
        user.setLobby(lobby);
        addUserToLobby(userName, lobbyPin);
        Logger.info(SERVER_PLAYER + userName + " ist der Lobby " + lobby.getPin() + " beigetreten.");
    }

    public void leaveLobby(int lobbyPin, String userName) throws UserNotFoundException {
        Lobby lobby = lobbies.get(lobbyPin);
        lobby.removeUser(userService.getUser(userName));
        removeUserFromLobby(userName);
        Logger.info(SERVER_PLAYER + userName + " hat die Lobby  " + lobby.getPin() + "  verlassen.");
    }

    public void readyUser(int lobbyPin, String userName) throws UserNotFoundException {
        Lobby lobby = lobbies.get(lobbyPin);
        lobby.readyUser(userService.getUser(userName));
        Logger.info(SERVER_PLAYER + userName + " ist bereit.");
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
     *
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
     * Durchsuche die Liste der Lobbies nach dem gegebenen Usernamen und den Benutzer zurück.
     *
     * @param usrID Name des Users
     * @return Player oder null, wenn kein Spieler mit dem gegebenen Namen gefunden wurde
     */
    public User findUserInAllLobbies(String usrID) throws UserNotFoundException {
        for (Lobby lobby : lobbies.values()) {
            for (User user : lobby.getUsers()) {
                if (user.getName().equals(usrID)) {
                    return user;
                }
            }
        }
        throw new UserNotFoundException("User mit Name " + usrID + " nicht gefunden.");
    }

    public void startGame(Integer pin, String username) {
        Lobby lobby = lobbies.get(pin);
        if (!lobby.getOwner().getName().equals(username)) {
            Logger.error("SERVER: User " + username + " is not the owner of the lobby.");
            return;
        }
        // check if everyone is ready
        if (!lobby.isEveryoneReady()) {
            Logger.error("SERVER: Not everyone is ready.");
            return;
        }
        HashSet<User> users = lobby.getUsers();
        HashSet<Figures> assignedFigures = new HashSet<>();
        sequenceNumber = 0;
        lobby.startGame();

        users.forEach(user -> {
            Figures randomFigure;
            String color = "BLUE";
            if(Roles.COMPETITOR == user.getRole()){
                color = "GREEN";
            }
            Logger.error("Color is:"+color+" Role is: "+user.getRole());

            do {
                String finalColor = color;
                List<Figures> filteredFigures = Arrays.stream(Figures.values())
                        .filter(f -> f.name().startsWith(finalColor))
                        .toList();
                randomFigure = filteredFigures.get(random.nextInt(filteredFigures.size()));
            } while(assignedFigures.contains(randomFigure));

            assignedFigures.add(randomFigure);
            user.setFigure(randomFigure);
            user.setSequence(sequenceNumber);
            sequenceNumber++;
            user.setLocation(1);
        });
    }
}