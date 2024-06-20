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
    private int sequenceNumber = 1;


    @Autowired
    public LobbyService(UserService userService
    ) {
        this.userService = userService;
        this.lobbies = new ConcurrentHashMap<>();
        this.userLobbyMap = new ConcurrentHashMap<>();
    }

    public void addUserToLobby(String userId, int lobbyId) {
        userLobbyMap.put(userId, lobbyId);
    }

    public Optional<Integer> getLobbyIDForUserID(String userId) {
        return Optional.ofNullable(userLobbyMap.get(userId));
    }

    public void removeUserFromLobby(String userId) {
        userLobbyMap.remove(userId);
    }

    /**
     * Erstellt eine neue Lobby und fügt den Ersteller (User) hinzu
     * TODO: Verbesserung des Handling bei bereits bestehendem PIN!
     *
     * @param user User
     */
    public Lobby createLobby(User user) {
        Lobby newLobby = new Lobby(user);
        userLobbyMap.put(user.getName(), newLobby.getPin());
        if (lobbies.putIfAbsent(newLobby.getPin(), newLobby) != null) {
            throw new IllegalStateException("Lobby mit PIN " + newLobby.getPin() + " existiert bereits.");
        }
        user.setLobby(newLobby);
        Logger.info("SERVER: Lobby " + newLobby.getPin() + " wurde erstellt.");
        return newLobby;
    }

    /**
     * Fügt einen Benutzer zu einer Lobby hinzu
     *
     * @param lobbyPin PIN der Lobby
     * @param userName Name des Benutzers
     * @throws UserNotFoundException wenn der Benutzer nicht gefunden wird
     */
    public void joinLobby(Integer lobbyPin, String userName) throws UserNotFoundException, LobbyIsFullException, LobbyNotFoundException {
        Lobby lobby = findLobbyByPin(lobbyPin).orElseThrow(() -> new LobbyNotFoundException("Lobby mit PIN " + lobbyPin + " nicht gefunden."));
        User user = userService.getUser(userName);
        lobby.addUser(user);
        user.setLobby(lobby);
        addUserToLobby(userName, lobbyPin);
        Logger.info("SERVER: Spieler " + userName + " ist der Lobby " + lobby.getPin() + " beigetreten.");
    }

    /**
     * Entfernt einen Benutzer aus einer Lobby
     * @param lobbyPin PIN der Lobby
     * @param userName Name des Benutzers
     * @throws UserNotFoundException wenn der Benutzer nicht gefunden wird
     */
    public void leaveLobby(Integer lobbyPin, String userName) throws UserNotFoundException, LobbyNotFoundException {
        Lobby lobby = lobbies.get(lobbyPin);
        if (lobby == null) {
            throw new LobbyNotFoundException("Lobby mit PIN " + lobbyPin + " nicht gefunden.");
        }
        User user = userService.getUser(userName);
        lobby.removeUser(user);
        removeUserFromLobby(userName);
        Logger.info("SERVER: Spieler " + userName + " hat die Lobby  " + lobby.getPin() + "  verlassen.");
    }

    /**
     * Setzt den Status des Benutzers auf bereit
     * @param lobbyPin PIN der Lobby
     * @param userName Name des Benutzers
     * @throws UserNotFoundException wenn der Benutzer nicht gefunden wird
     */
    public void readyUser(Integer lobbyPin, String userName) throws UserNotFoundException, LobbyNotFoundException {
        Lobby lobby = lobbies.get(lobbyPin);
        if (lobby == null) {
            throw new LobbyNotFoundException("Lobby mit PIN " + lobbyPin + " nicht gefunden.");
        }
        lobby.toggleReady(userService.getUser(userName));
        Logger.info("SERVER: Spieler " + userName + " ist bereit.");
    }

    /**
     * Durchsuche die Liste der Lobbies nach der gegebenen PIN und gib die entsprechende Lobby zurück.
     *
     * @param pin PIN der Lobby
     * @return Lobby oder Exception, wenn keine Lobby mit der gegebenen PIN gefunden wurde
     */
    public Optional<Lobby> findLobbyByPin(Integer pin) throws LobbyNotFoundException {
        //return Optional.ofNullable(lobbies.get(pin));
        Lobby lobby = lobbies.get(pin);
        if (lobby == null) {
            throw new LobbyNotFoundException("Lobby mit PIN " + pin + " nicht gefunden.");
        }
        return Optional.of(lobby);
    }

    /**
     * Durchsuche die Liste der Lobbies nach der gegebenen PIN und gib die entsprechende Lobby zurück.
     *
     * @param pin PIN der Lobby
     * @return Lobby oder null, wenn keine Lobby mit der gegebenen PIN gefunden wurde
     */
    public Optional<Lobby> findOptionalLobbyByPin(Integer pin) {
        /*for (Lobby lobby : lobbies.values()) {
            if (Objects.equals(lobby.getPin(), pin)) {
                return Optional.of(lobby);
            }
        }
         */
        return Optional.ofNullable(lobbies.get(pin));
    }
    //public Optional<Lobby> findOptionalLobbyByPin(int pin) {
    //    return Optional.ofNullable(lobbies.get(pin));
    //}

    /**
     * Durchsuche die Liste der Lobbies nach dem gegebenen Usernamen und den Benutzer zurück.
     * @param userName Name des Users
     * @return Player oder null, wenn kein Spieler mit dem gegebenen Namen gefunden wurde
     */
    public User findUserInAllLobbies(String userName) throws UserNotFoundException {
        return lobbies.values().stream()
                .flatMap(lobby -> lobby.getUsers().stream())
                .filter(user -> user.getName().equals(userName))
                .findFirst()
                .orElseThrow(() -> new UserNotFoundException("User mit Name " + userName + " nicht gefunden."));
    }

    /**
     * Startet das Spiel in der Lobby
     * @param pin PIN der Lobby
     * @param username Name des Owner
     */
    public void startGame(Integer pin, String username) {
        Optional<Lobby> optionalLobby = findOptionalLobbyByPin(pin);
        optionalLobby.ifPresentOrElse(lobby -> {

            // check if command sender is the owner
            if (!isOwner(lobby, username)) {
                Logger.error("SERVER: User " + username + " is not the owner of the lobby.");
                return;
            }
            // check if everyone is ready
            if (!lobby.isEveryoneReady()) {
                Logger.error("SERVER: Not everyone is ready.");
                return;
            }

            assignFiguresToUsers(lobby.getUsers());
            resetSequenceNumber();
            setRandomRoles(lobby);
            lobby.startGame();
        }, () -> Logger.error("SERVER: Lobby with PIN " + pin + " not found."));
    }

    public void setRandomRoles(Lobby lobby){
        List<User> shuffledUsers = new ArrayList<>(lobby.getUsers());
        Collections.shuffle(shuffledUsers, random);

        // Berechne die Anzahl der Monopolisten
        int monopolistCount = (int) Math.floor(shuffledUsers.size() / 2.0);

        // Setze die Rollen basierend auf der Position in der gemischten Liste
        for (int i = 0; i < shuffledUsers.size(); i++) {
            User user = shuffledUsers.get(i);
            if (i < monopolistCount) {
                user.setRole(Roles.MONOPOLIST);
            } else {
                user.setRole(Roles.COMPETITOR);
            }
        }
    }

    private boolean isOwner(Lobby lobby, String username) {
        return lobby.getOwner().getName().equals(username);
    }

    private void assignFiguresToUsers(Set<User> users) {
        Set<Figures> assignedFigures = new HashSet<>();
        users.forEach(user -> {
            Figures randomFigure = getRandomFigure(assignedFigures);
            assignedFigures.add(randomFigure);
            user.setFigure(randomFigure);
            user.setSequence(sequenceNumber++);
            user.setLocation(1);
        });
    }

    private Figures getRandomFigure(Set<Figures> assignedFigures) {
        Figures randomFigure;
        do {
            randomFigure = Figures.values()[random.nextInt(Figures.values().length)];
        } while (assignedFigures.contains(randomFigure));
        return randomFigure;
    }

    private void resetSequenceNumber() {
        sequenceNumber = 1;
    }

}