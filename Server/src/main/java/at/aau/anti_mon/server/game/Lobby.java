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

/**
 * Represents a lobby in the game
 */
@Getter
@Setter
public class Lobby {

    private final Integer pin;
    private final HashSet<User> users;
    private User owner;
    private static final int MAX_USERS = 6;
    private GameStateEnum gameState;

    private Random random;

    public Lobby() {
        SecureRandom secureRandom = new SecureRandom();
        this.pin = secureRandom.nextInt(9000) + 1000;
        this.users = new HashSet<>();
        this.gameState = GameStateEnum.LOBBY;
        this.owner = null;
    }

    public Lobby(User user) {
        SecureRandom secureRandom = new SecureRandom();
        this.pin = secureRandom.nextInt(9000) + 1000;
        this.users = new HashSet<>();
        this.gameState = GameStateEnum.LOBBY;
        user.setReady(true);
        this.users.add(user);
        this.owner = user;
    }

    public void addUser(User user) throws LobbyIsFullException {
        if (users.size() >= MAX_USERS) {
            throw new LobbyIsFullException("Lobby is full. Cannot add more players.");
        } else if (users.contains(user)) {
            throw new UserAlreadyExistsException("User already exists");
        }
        users.add(user);
    }

    public void readyUser(User user) {
        user.setReady(!user.isReady());
    }

    public void removeUser(User user) throws UserNotFoundException {
        if (!users.contains(user)) throw new UserNotFoundException("User not found in lobby");
        if (user.equals(owner) && users.size() > 1) {
            for (User next : users) {
                if (!next.equals(owner)) {

                    // If the next user is not ready, set them to ready -> because he is the new owner
                    if (!next.isReady()){
                        readyUser(next);
                    }

                    setOwner(next);
                    break;
                }
            }
        }
        if (user.isReady()){
            readyUser(user);
        }
        user.setLocation(1);
        user.setRole(null);
        users.remove(user);
    }

    /**
     * 
     * @param session The session to search for
     * @return The player with the given session
     */
    public User getUserWithSession(WebSocketSession session) {
        return users.stream().filter(player -> player.getSession().getId().equals(session.getId())).findFirst()
                .orElse(null);
    }

    public boolean isPlayerInLobby(User user) {
        return users.contains(user);
    }

    public boolean isFull() {
        return this.users.size() < MAX_USERS;
    }

    public void startGame() {
        ArrayList<User> userList = new ArrayList<>(users);
        random = new Random();
        Collections.shuffle(userList, random);
        users.forEach(user ->
            user.setRole(userList.indexOf(user) < userList.size() / 2 ? Roles.MONOPOLIST : Roles.COMPETITOR)
        );
        this.gameState = GameStateEnum.INGAME;
    }

    public boolean isEveryoneReady() {
        return users.stream().allMatch(User::isReady);
    }

    public boolean hasUser(String username) {
        return users.stream().anyMatch(user -> user.getName().equals(username));
    }
}
