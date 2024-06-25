package at.aau.anti_mon.server.listener;

import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.events.*;
import at.aau.anti_mon.server.exceptions.LobbyIsFullException;
import at.aau.anti_mon.server.exceptions.LobbyNotFoundException;
import at.aau.anti_mon.server.exceptions.UserNotFoundException;
import at.aau.anti_mon.server.game.Lobby;
import at.aau.anti_mon.server.game.User;
import at.aau.anti_mon.server.service.LobbyService;
import at.aau.anti_mon.server.service.SessionManagementService;
import at.aau.anti_mon.server.service.UserService;
import at.aau.anti_mon.server.utilities.JsonDataUtility;
import at.aau.anti_mon.server.utilities.MessagingUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

import java.security.SecureRandom;
import java.util.*;

/**
 * Event-Listener for user interactions
 */
@Component
public class UserEventListener {

    private static final String PLAYER_TAG = "Spieler ";
    private static final int CHANGE_BALANCE = 200;
    private static final int BOARD_SIZE = 40;
    private static final int JAIL_POSITION = 31;
    private static final int JAIL_EXIT_POSITION = 11;
    private static final int JAIL_FINE = 20;
    private static int FIXED_PROBABILITY_FOR_CHEATING = -1;


    private final SecureRandom random;
    private final LobbyService lobbyService;
    private final SessionManagementService sessionManagementService;
    private final UserService userService;

    /**
     * Konstruktor für UserEventListener
     * Dependency Injection für LobbyService
     *
     * @param lobbyService LobbyService
     */
    @Autowired
    UserEventListener(LobbyService lobbyService,
                      SessionManagementService sessionManagementService,
                      UserService userService) {
        this.lobbyService = lobbyService;
        this.sessionManagementService = sessionManagementService;
        this.userService = userService;
        this.random = new SecureRandom();
    }

    /**
     * Logik zum Erstellen einer Lobby
     * PIN der Lobby wird an den Benutzer zurückgegeben
     *
     * @param event Ereignis
     */
    @EventListener
    public void onCreateLobbyEvent(UserCreatedLobbyEvent event) {
        sessionManagementService.registerUserWithSession(event.getUsername(), event.getSession());
        User user = userService.findOrCreateUser(event.getUsername(), event.getSession());
        Lobby newLobby = lobbyService.createLobby(user);
        Logger.info("SERVER: Spiel erstellt mit PIN: " + newLobby.getPin());
        MessagingUtility.createMessage("pin", newLobby.getPin().toString(), Commands.PIN).send(event.getSession());
    }

    /**
     * Fügt den Benutzer zur Lobby hinzu
     * @param event Ereignis
     */
    @EventListener
    public void onUserJoinedLobbyEvent(UserJoinedLobbyEvent event)
            throws UserNotFoundException, LobbyNotFoundException, LobbyIsFullException {
        sessionManagementService.registerUserWithSession(event.getUsername(), event.getSession());
        Lobby lobby = validateLobbyForJoin(event.getLobbyPIN(), event.getUsername(), event.getSession());
        if (lobby == null) {
            return;
        }
        User joinedUser = userService.findOrCreateUser(event.getUsername(), event.getSession());
        handleUserLobbyJoin(joinedUser, lobby, event.getSession());
    }

    private Lobby validateLobbyForJoin(Integer pin, String username, WebSocketSession session) {
        Optional<Lobby> optionalLobby = lobbyService.findOptionalLobbyByPin(pin);
        if (optionalLobby.isEmpty()) {
            Logger.error("Lobby mit PIN " + pin + " nicht gefunden.");
            MessagingUtility.createErrorMessage("Lobby mit PIN " + pin + " nicht gefunden.", "ERROR_JOIN_GAME").send(session);
            return null;
        }
        Lobby lobby = optionalLobby.get();
        if (lobby.isFull()) {
            Logger.error("Lobby mit PIN " + pin + " ist voll.");
            MessagingUtility.createErrorMessage("Lobby mit PIN " + pin + " ist voll.", "ERROR_JOIN_GAME").send(session);
            return null;
        }
        //if (!lobby.hasUser(username)) {
        //    Logger.error("User " + username + " ist nicht in der Lobby.");
        //    MessagingUtility.createErrorMessage("User " + username + " ist nicht in der Lobby.", "ERROR_JOIN_GAME").send(session);
        //    return null;
        //}
        return lobby;
    }

    private void handleUserLobbyJoin(User joinedUser, Lobby joinedLobby, WebSocketSession session) throws LobbyNotFoundException, UserNotFoundException, LobbyIsFullException {
        if (joinedUser.getLobby() != null) {
            Logger.error("User " + joinedUser.getUserName() + " is already in a lobby. --> Leave Lobby.");
            lobbyService.leaveLobby(joinedUser.getLobby().getPin(), joinedUser.getUserName());
            joinedUser.clear();
        }
        lobbyService.joinLobby(joinedLobby.getPin(), joinedUser.getUserName());
        Logger.debug("User " + joinedUser.getUserName() + " joined the lobby. Is owner: " + joinedUser.isOwner());
        MessagingUtility.createInfoMessage("Erfolgreich der Lobby beigetreten.", "INFO_JOIN_GAME").send(session);

        notifyUsersInLobby(joinedLobby, joinedUser, Commands.NEW_USER);
        Logger.info("User " + joinedUser.getUserName() + " joined the lobby with " + joinedLobby.getUsers().size() + " users.");
    }

    private void notifyUsersInLobby(Lobby lobby, User eventUser, Commands command) {
        Set<User> users = lobby.getUsers();
        for (User user : users) {
            MessagingUtility.createUserMessage(eventUser, command).send(sessionManagementService.getSessionForUser(user.getUserName()));
            MessagingUtility.createUserMessage(user, command).send(sessionManagementService.getSessionForUser(eventUser.getUserName()));
        }
    }

    /**
     * Entfernt den Benutzer aus der Lobby
     *
     * @param event Ereignis
     */
    @EventListener
    public void onLeaveLobbyEvent(UserLeftLobbyEvent event) throws UserNotFoundException, LobbyNotFoundException {
        sessionManagementService.registerUserWithSession(event.getUsername(), event.getSession());
        Lobby lobby = validateLobby(event.getLobbyPIN(), event.getUsername());
        if (lobby == null) {
            return;
        }
        handleUserLobbyLeave(event.getUsername(), lobby, event.getSession());
    }

    private void handleUserLobbyLeave(String username, Lobby lobby, WebSocketSession session) throws LobbyNotFoundException, UserNotFoundException {
        lobbyService.leaveLobby(lobby.getPin(), username);
        MessagingUtility.createInfoMessage("Erfolgreich die Lobby verlassen.", "INFO_JOIN_GAME").send(session);
        Logger.info("Spieler " + username + " hat die Lobby verlassen.");
        notifyUsersOfLeave(lobby, username);
    }

    private void notifyUsersOfLeave(Lobby lobby, String username) {
        Set<User> users = lobby.getUsers();
        for (User user : users) {
            MessagingUtility.createUsernameMessage(username, Commands.LEAVE_GAME).send(sessionManagementService.getSessionForUser(user.getUserName()));
            MessagingUtility.createInfoMessage("User " + username + " hat die Lobby verlassen.", "INFO_LOBBY").send(sessionManagementService.getSessionForUser(user.getUserName()));
        }
    }

    private Lobby validateLobby(Integer pin, String username) {
        Optional<Lobby> optionalLobby = lobbyService.findOptionalLobbyByPin(pin);
        if (optionalLobby.isEmpty()) {
            Logger.error("Lobby mit PIN " + pin + " nicht gefunden.");
            return null;
        }
        Lobby lobby = optionalLobby.get();
        if (!lobby.hasUser(username)) {
            Logger.error("User " + username + " ist nicht in der Lobby.");
            return null;
        }
        return lobby;
    }

    @EventListener
    public void onReadyUserEvent(UserReadyLobbyEvent event) throws UserNotFoundException {
        sessionManagementService.registerUserWithSession(event.getUsername(), event.getSession());

        Lobby lobby = validateLobby(event.getPin(), event.getUsername());
        if (lobby == null) {
            return;
        }
        lobbyService.readyUser(event.getPin(), event.getUsername());
        Logger.info("Spieler " + event.getUsername() + " ist bereit.");
        notifyUsersInLobby(lobby, userService.getUser(event.getUsername()), Commands.READY);
    }

    @EventListener
    public void onStartGameEvent(UserStartedGameEvent event)  {
        sessionManagementService.registerUserWithSession(event.getUsername(), event.getSession());

        Lobby lobby = validateLobby(event.getPin(), event.getUsername());
        if (lobby == null) {
            return;
        }

        lobbyService.startGame(event.getPin(), event.getUsername());
        Logger.info("Spieler " + event.getUsername() + " hat das Spiel gestartet.");
        notifyStartGame(lobby);
    }

    private void notifyStartGame(Lobby lobby) {
        Set<User> users = lobby.getUsers();
        List<UserDTO> userDTOList = users.stream()
                .map(user -> new UserDTO(user.getUserName(), user.isOwner(), user.isReady(), user.getRole(), user.getFigure()))
                .toList();
        users.forEach(user -> MessagingUtility.createUserCollectionMessage(Commands.START_GAME, userDTOList)
                .send(sessionManagementService.getSessionForUser(user.getUserName())));
      //  users.forEach(user -> MessagingUtility.createUsernameMessage(user.getName(), Commands.FIRST_PLAYER)
      //          .send(sessionManagementService.getSessionForUser(user.getName())));
    }

    @EventListener
    public void onDiceNumberEvent(DiceNumberEvent event) throws UserNotFoundException {
        sessionManagementService.registerUserWithSession(event.getUsername(), event.getSession());

        Lobby lobby = validateLobby(event.getPin(), event.getUsername());
        if (lobby == null) {
            return;
        }
        User user = getUserFromEvent(event);
        int diceNumber = event.getDicenumber();
        processMove(user, diceNumber);
        notifyUsersAboutDices(lobby, user, diceNumber);

        if (shouldCheckCheating(event, user)) {
            checkCheating(user);
        }
    }

    private void notifyUsersAboutDices(Lobby lobby, User diceEventUser, int diceNumber) {
        Set<User> users = lobby.getUsers();
        for (User user : users) {
            MessagingUtility.createGameMessage(diceEventUser.getUserName(), diceNumber, diceEventUser.getFigure(), diceEventUser.getPlayerLocation(),Commands.DICENUMBER).send(sessionManagementService.getSessionForUser(user.getUserName()));
        }
    }

    private boolean isSpecialPosition(int location) {
        return location == 1 || location == JAIL_POSITION;
    }

    private void handleSpecialPosition(User user, int location) {
        if (location == 1) {
            user.setMoney(user.getMoney() + CHANGE_BALANCE);
        } else if (location == JAIL_POSITION) {
            user.setMoney(user.getMoney() + JAIL_FINE);
            user.setPlayerLocation(JAIL_EXIT_POSITION);
            user.setUnavailableRounds(2);
        }
    }

    private void processMove(User user, int diceNumber) throws UserNotFoundException {
        int currentLocation = user.getPlayerLocation();
        int nextLocation = calculateNextLocation(currentLocation, diceNumber);
        user.setPlayerLocation(nextLocation);

        int newBalance = user.getMoney();
        if (nextLocation < currentLocation) {
            newBalance += CHANGE_BALANCE;
        }
        if (isSpecialPosition(nextLocation)) {
            handleSpecialPosition(user, nextLocation);
        }
        if (newBalance != user.getMoney()) {
            user.setMoney(newBalance);
            balanceChangedEvent(new ChangeBalanceEvent(sessionManagementService.getSessionForUser(user.getUserName()), user.getUserName(), newBalance));
        }
    }

    private int calculateNextLocation(int currentLocation, int diceNumber) {
        int nextLocation = currentLocation + diceNumber;
        if (nextLocation > BOARD_SIZE) {
            nextLocation -= BOARD_SIZE;
        }
        return nextLocation;
    }

    private boolean shouldCheckCheating(DiceNumberEvent event, User user) {
        return !event.getCheat() && user.getUnavailableRounds() == 0;
    }

    public void checkCheating(User user) {
        int probability = FIXED_PROBABILITY_FOR_CHEATING;
        probability = random.nextInt(100) + 1;
        if (probability > 50) {
            WebSocketSession session = sessionManagementService.getSessionForUser(user.getUserName());
            JsonDataUtility.sendCheating(session);
        }
    }

    @EventListener
    public void balanceChangedEvent(ChangeBalanceEvent event) throws UserNotFoundException {
        String username = event.getUsername();
        Integer newBalance = event.getNewBalance();
        User user = userService.getUser(username);
        user.setMoney(newBalance);
        notifyBalanceChange(user, newBalance);
    }

    private void notifyBalanceChange(User user, int newBalance) {
        Set<User> users = user.getLobby().getUsers();
        for (User u : users) {
            JsonDataUtility.sendNewBalance(sessionManagementService.getSessionForUser(u.getUserName()), user.getUserName(), newBalance);
        }
    }

    @EventListener
    public void onNextPlayerEvent(NextPlayerEvent event) throws UserNotFoundException {
        Logger.info(PLAYER_TAG + event.getUsername() + " hat Finish ausgewählt.");
        String username = event.getUsername();
        User user = userService.getUser(username);
        user.setHasPlayed(true);
        Set<User> users = user.getLobby().getUsers();

        if (checkWinCondition(users)) {
            return;
        }

        int nextSequence = getNextSequence(users, user.getSequence());
        User nextPlayer = getNextPlayer(users, nextSequence);

        Logger.info(PLAYER_TAG + nextPlayer.getUserName() + " is the next Player.");
        notifyNextPlayer(users, nextPlayer);
    }

    private User getWinner(Set<User> users) {
        List<User> activePlayers = users.stream()
                .filter(user -> user.getUnavailableRounds() >= 0)
                .toList();
        return activePlayers.size() == 1 ? activePlayers.get(0) : null;
    }

    public boolean checkWinCondition(Set<User> users) {
        User winner = getWinner(users);
        if (winner != null) {
            JsonDataUtility.sendWinGame(sessionManagementService.getSessionForUser(winner.getUserName()), winner.getUserName());
            return true;
        }
        return false;
    }

    private int getNextSequence(Set<User> users, int currentSequence) {
        if (allPlayersHavePlayed(users)) {
            resetPlayers(users);
            return 0;
        }
        return currentSequence + 1;
    }

    private boolean allPlayersHavePlayed(Set<User> users) {
        long count = users.stream().filter(User::isHasPlayed).count();
        Logger.info(PLAYER_TAG + "die gespielt haben: " + count);
        return count == users.size();
    }

    private void resetPlayers(Set<User> users) {
        Logger.info("Alle " + PLAYER_TAG + "haben gespielt.");
        users.forEach(user -> {
            if (user.getUnavailableRounds() == 0) {
                user.setHasPlayed(false);
            }
        });
        reduceUnavailableRounds(users);
    }

    private void reduceUnavailableRounds(Set<User> users) {
        users.forEach(user -> {
            if (user.getUnavailableRounds() > 0) {
                user.setUnavailableRounds(user.getUnavailableRounds() - 1);
            }
        });
    }

    private User getNextPlayer(Set<User> users, int sequence) {
        List<User> sortedUsers = users.stream()
                .sorted(Comparator.comparingInt(User::getSequence))
                .toList();
        int playerAmount = sortedUsers.size();
        int i = sequence;

        while (true) {
            if (i >= playerAmount) {
                i = 0;
            }
            User user = sortedUsers.get(i);
            Logger.info(PLAYER_TAG + user.getUserName() + " mit Sequence " + user.getSequence() + " und unavailableRounds " + user.getUnavailableRounds());
            if (user.getUnavailableRounds() == 0) {
                return user;
            }
            i++;
        }
    }

    private void notifyNextPlayer(Set<User> users, User nextPlayer) {
        users.forEach(user -> MessagingUtility.createMessage("username", nextPlayer.getUserName(), Commands.NEXT_PLAYER)
                .send(sessionManagementService.getSessionForUser(user.getUserName())));
    }

    @EventListener
    public void onFirstPlayerEvent(FirstPlayerEvent event) throws UserNotFoundException {
        Logger.info("Wir sind in FirstPlayerEventListener.");
        String username = event.getUsername();
        User user = userService.getUser(username);
        Set<User> users = user.getLobby().getUsers();
        Logger.info("Wir haben die Nummer: " + user.getSequence());
        if (user.getSequence() == 0) {
            Logger.info(PLAYER_TAG + event.getUsername() + " ist " + PLAYER_TAG + "1.");
            for (User u : users) {
                JsonDataUtility.sendFirstPlayer(sessionManagementService.getSessionForUser(u.getUserName()), user.getUserName());
            }
        }
    }

    @EventListener
    public void onLoseGameEvent(LooseGameEvent event) throws UserNotFoundException{
        Logger.info("Wir sind in LoseGameEventListener.");
        String username = event.getUsername();
        User user = userService.getUser(username);
        user.setUnavailableRounds(-1);
        Set<User> users = user.getLobby().getUsers();
        for (User u : users) {
            JsonDataUtility.sendLoseGame(sessionManagementService.getSessionForUser(u.getUserName()), user.getUserName());
        }
    }

    @EventListener
    public void onEndGameEvent(EndGameEvent event) throws UserNotFoundException {
        Logger.info("Wir sind in EndGameEventListener.");
        String username = event.getUsername();
        User user = userService.getUser(username);
        Set<User> users = user.getLobby().getUsers();
        int rank;
        for (User user1 : users) {
            rank = 1;
            for (User user2 : users) {
                if(user1.getMoney()<user2.getMoney()){
                    rank++;
                }
            }
            JsonDataUtility.sendEndGame(sessionManagementService.getSessionForUser(user1.getUserName()),rank);
        }
    }

    private User getUserFromEvent(BaseUserEvent event) throws UserNotFoundException {
        return userService.getUser(event.getUsername());
    }

    public void setFIXED_PROBABILITY_FOR_CHEATING(int i) {
        FIXED_PROBABILITY_FOR_CHEATING = i;
    }
}