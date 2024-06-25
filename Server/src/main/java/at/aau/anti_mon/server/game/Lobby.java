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
    private static final Integer MAX_USERS = 6;
    private GameStateEnum gameState;
    private SecureRandom secureRandom;

    public Lobby() {
        this(null);
    }

    public Lobby(User user) {
        secureRandom = new SecureRandom();
        this.pin = secureRandom.nextInt(9000) + 1000;
        this.users = new CopyOnWriteArraySet<>(); // Thread-sicheres Set
        this.gameState = GameStateEnum.LOBBY;

        if (user != null) {
            user.setReady(true);
            this.users.add(user);
            this.owner = user;
            user.setOwner(true);
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

    public synchronized void readyUser(User user) {
        user.setReady(!user.isReady());
    }

    public synchronized void removeUser(User user) throws UserNotFoundException {
        if (!users.contains(user)) throw new UserNotFoundException("User not found in lobby");
        if (user.equals(owner) && users.size() > 1) {
            for (User next : users) {
                if (!next.equals(owner)) {

                    // If the next user is not ready, set them to ready -> because he is the new owner
                    if (!next.isReady()){
                        readyUser(next);
                    }

                    setOwner(next);
                    next.setOwner(true);
                    break;
                }
            }
        }
        if (user.isReady()){
            readyUser(user);
        }
        user.clear();
        users.remove(user);
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

    public synchronized boolean isFull() {
        return this.users.size() == MAX_USERS;
    }

    public synchronized void startGame() {
        ArrayList<User> userList = new ArrayList<>(users);
        Collections.shuffle(userList, secureRandom);
        users.forEach(user ->
            user.setRole(userList.indexOf(user) < userList.size() / 2 ? Roles.MONOPOLIST : Roles.COMPETITOR)
        );
        this.gameState = GameStateEnum.INGAME;
    }

    public synchronized boolean isEveryoneReady() {
        return users.stream().allMatch(User::isReady);
    }

    public synchronized boolean hasUser(String username) {
        return users.stream().anyMatch(user -> user.getUserName().equals(username));
    }

    public synchronized void clear() {
        users.clear();
        owner = null;
        gameState = GameStateEnum.LOBBY;
    }

}
