package at.aau.anti_mon.server.game;

import at.aau.anti_mon.server.enums.GameStateEnum;
import at.aau.anti_mon.server.exceptions.LobbyIsFullException;
import at.aau.anti_mon.server.exceptions.UserAlreadyExistsException;
import at.aau.anti_mon.server.exceptions.UserNotFoundException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Iterator;

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
    private final GameStateEnum gameState;

    public Lobby() {
        SecureRandom random = new SecureRandom();
        this.pin = random.nextInt(9000) + 1000;
        this.users = new HashSet<>();
        this.gameState = GameStateEnum.LOBBY;
        this.owner = null;
    }

    public Lobby(User user) {
        SecureRandom random = new SecureRandom();
        this.pin = random.nextInt(9000) + 1000;
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
        if (users.contains(user)) {
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
            users.remove(user);
        } else {
            throw new UserNotFoundException("User not found in lobby");
        }
    }

    /**
     * Todo: This method is not used in the codebase. It should be removed?
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

    public boolean canAddPlayer() {
        return this.users.size() < MAX_USERS;
    }

    public void startGame() {
        this.gameState = GameState.INGAME;
    }

    public boolean isEveryoneReady() {
        return users.stream().allMatch(User::isReady);
    }
}
