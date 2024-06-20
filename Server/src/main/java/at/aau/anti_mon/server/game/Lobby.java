package at.aau.anti_mon.server.game;

import at.aau.anti_mon.server.enums.GameStateEnum;
import at.aau.anti_mon.server.enums.Roles;
import at.aau.anti_mon.server.exceptions.LobbyIsFullException;
import at.aau.anti_mon.server.exceptions.UserAlreadyExistsException;
import at.aau.anti_mon.server.exceptions.UserNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Represents a lobby in the game
 */
@Getter
@Setter
public class Lobby {

    private final Integer pin;
    private final Set<User> users;
    private User owner;
    private static final int MAX_USERS = 6;
    private GameStateEnum gameState;
    private final SecureRandom random;

    public Lobby() {
        this(null);
    }

    public Lobby(User user) {
        random = new SecureRandom();
        this.pin = random.nextInt(9000) + 1000;
        this.users = new CopyOnWriteArraySet<>(); // Thread-sicheres Set
        this.gameState = GameStateEnum.LOBBY;

        if (user != null) {
            user.setReady(true);
            this.users.add(user);
            this.owner = user;
        }
    }

    public synchronized void addUser(User user) throws LobbyIsFullException {
        if (users.size() >= MAX_USERS) {
            throw new LobbyIsFullException("Lobby is full. Cannot add more players.");
        } else if (users.contains(user)) {
            throw new UserAlreadyExistsException("User already exists");
        }
        users.add(user);
    }

    public synchronized void toggleReady(User user) {
        user.setReady(!user.isReady());
    }

    public synchronized void removeUser(User user) throws UserNotFoundException {
        if (users.contains(user)) {
            if (user.equals(owner) && users.size() > 1) {
                for (User next : users) {
                    if (!next.equals(owner)) {

                        // If the next user is not ready, set them to ready -> because he is the new owner
                        if (!next.isReady()){
                            toggleReady(next);
                        }
                        setOwner(next);
                        break;
                    }
                }
            }
            if (user.isReady()){
                toggleReady(user);
            }
            user.setLocation(1);
            user.setRole(null);
            users.remove(user);
        } else {
            throw new UserNotFoundException("User not found in lobby");
        }
    }

    public synchronized void nextOwner(User user){
        owner = users.stream().filter(u -> !u.equals(user)).findFirst().orElse(null);
        if (owner != null && !owner.isReady()) {
            toggleReady(owner);
        }
    }

    /**
     * @param session The session to search for
     * @return The player with the given session
     */
    public synchronized Optional<User> getUserWithSession(WebSocketSession session) {
        return users.stream()
                .filter(user -> user.getSession().getId().equals(session.getId()))
                .findFirst();
    }

    public synchronized boolean isPlayerInLobby(User user) {
        return users.contains(user);
    }

    public synchronized boolean canAddPlayer() {
        return this.users.size() < MAX_USERS;
    }

    public synchronized void startGame() {
        //setRandomRoles();
        this.gameState = GameStateEnum.INGAME;
    }

    // Backup
    // Method -> to LobbyService
    public synchronized void setRandomRoles(){
        List<User> shuffledUsers = new ArrayList<>(users);
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

    public synchronized boolean isEveryoneReady() {
        return users.stream().allMatch(User::isReady);
    }

    public synchronized boolean isFull() {
        return users.size() == MAX_USERS;
    }

    public synchronized void clear() {
        users.clear();
        owner = null;
        gameState = GameStateEnum.LOBBY;
    }

    public synchronized boolean hasUser(String username) {
        return users.stream().anyMatch(user -> user.getName().equals(username));
    }
}
