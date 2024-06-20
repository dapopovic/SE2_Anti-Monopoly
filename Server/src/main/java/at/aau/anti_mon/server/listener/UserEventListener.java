package at.aau.anti_mon.server.listener;

import java.util.*;

import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.enums.Figures;
import at.aau.anti_mon.server.events.*;
import at.aau.anti_mon.server.utilities.MessagingUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

import at.aau.anti_mon.server.exceptions.LobbyIsFullException;
import at.aau.anti_mon.server.exceptions.LobbyNotFoundException;
import at.aau.anti_mon.server.exceptions.UserNotFoundException;
import at.aau.anti_mon.server.game.Lobby;
import at.aau.anti_mon.server.game.User;
import at.aau.anti_mon.server.service.LobbyService;
import at.aau.anti_mon.server.service.SessionManagementService;
import at.aau.anti_mon.server.service.UserService;
import at.aau.anti_mon.server.utilities.JsonDataUtility;

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
     * Constructor Dependency Injection
     */
    @Autowired
    UserEventListener(
            LobbyService lobbyService,
            SessionManagementService sessionManagementService,
            UserService userService
    ) {
        this.lobbyService = lobbyService;
        this.sessionManagementService = sessionManagementService;
        this.userService = userService;
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
     * 
     * @param event Ereignis
     */
    @EventListener
    public void onUserJoinedLobbyEvent(UserJoinedLobbyEvent event)
            throws UserNotFoundException, LobbyNotFoundException, LobbyIsFullException {
        sessionManagementService.registerUserWithSession(event.getUsername(), event.getSession());
        Lobby lobby = validateLobbyForJoin(event.getPin(), event.getUsername(), event.getSession());
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
            Logger.error("User " + joinedUser.getName() + " is already in a lobby. --> Leave Lobby.");
            lobbyService.leaveLobby(joinedUser.getLobby().getPin(), joinedUser.getName());
            joinedUser.clear();
        }
        lobbyService.joinLobby(joinedLobby.getPin(), joinedUser.getName());
        Logger.debug("User " + joinedUser.getName() + " joined the lobby. Is owner: " + joinedUser.isOwner());
        MessagingUtility.createInfoMessage("Erfolgreich der Lobby beigetreten.", "INFO_JOIN_GAME").send(session);

        notifyUsersInLobby(joinedLobby, joinedUser, Commands.NEW_USER);
        Logger.info("User " + joinedUser.getName() + " joined the lobby with " + joinedLobby.getUsers().size() + " users.");
    }


    private void notifyUsersInLobby(Lobby lobby, User eventUser, Commands command) {
        Set<User> users = lobby.getUsers();
        for (User user : users) {
            MessagingUtility.createUserMessage(eventUser, command).send(sessionManagementService.getSessionForUser(user.getName()));
            MessagingUtility.createUserMessage(user, command).send(sessionManagementService.getSessionForUser(eventUser.getName()));
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
        Lobby lobby = validateLobby(event.getPin(), event.getUsername());
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
            MessagingUtility.createUsernameMessage(username, Commands.LEAVE_GAME).send(sessionManagementService.getSessionForUser(user.getName()));
            MessagingUtility.createInfoMessage("User " + username + " hat die Lobby verlassen.", "INFO_LOBBY").send(sessionManagementService.getSessionForUser(user.getName()));
        }
    }

    @EventListener
    public void onReadyUserEvent(UserReadyLobbyEvent event) throws UserNotFoundException, LobbyNotFoundException {
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
                .map(user -> new UserDTO(user.getName(), user.isOwner(), user.isReady(), user.getRole(), user.getFigure()))
                .toList();
        users.forEach(user -> MessagingUtility.createUserCollectionMessage(Commands.START_GAME, userDTOList)
                .send(sessionManagementService.getSessionForUser(user.getName())));
        users.forEach(user -> MessagingUtility.createUsernameMessage(user.getName(), Commands.FIRST_PLAYER)
                .send(sessionManagementService.getSessionForUser(user.getName())));
    }

    @EventListener
    public void onDiceNumberEvent(DiceNumberEvent event) throws UserNotFoundException {
        sessionManagementService.registerUserWithSession(event.getUsername(), event.getSession());

        Lobby lobby = validateLobby(event.getPin(), event.getUsername());
        if (lobby == null) {
            return;
        }
        User eventUser = userService.getUser(event.getUsername());
        processDiceRoll(eventUser, event.getDicenumber());
        sendDiceMessages(eventUser, event.getDicenumber());
    }

    private void processDiceRoll(User user, int diceNumber) {
        Figures figure = user.getFigure();
        int location = user.getLocation();
        int nextLocation = (location + diceNumber) % 40;
        user.setLocation(nextLocation);
    }

    private void sendDiceMessages(User eventUser, int diceNumber) {
        Set<User> users = eventUser.getLobby().getUsers();
        String username = eventUser.getName();
        Figures figure = eventUser.getFigure();
        int location = eventUser.getLocation();

        for (User user : users) {
            MessagingUtility.createGameMessage(username, diceNumber, figure, location, Commands.DICE)
                    .send(sessionManagementService.getSessionForUser(user.getName()));
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
            JsonDataUtility.sendNewBalance(sessionManagementService.getSessionForUser(u.getName()), user.getName(), newBalance);
        }
    }

    @EventListener
    public void onNextPlayerEvent(NextPlayerEvent event) throws UserNotFoundException {
        Logger.info("Spieler " + event.getUsername() + " hat Finish ausgewählt.");
        String username = event.getUsername();
        User user = userService.getUser(username);
        setNextPlayer(user);
    }

    private void setNextPlayer(User user) {
        int sequence = user.getSequence();
        Set<User> users = user.getLobby().getUsers();
        int playerNumber = users.size();
        sequence = (sequence % playerNumber) + 1;

        int finalSequence = sequence;
        User nextPlayer = users.stream().filter(u -> u.getSequence() == finalSequence).findFirst().orElse(null);
        if (nextPlayer != null) {
            Logger.info("Spieler " + nextPlayer.getName() + " is the next Player.");
            for (User u : users) {
                MessagingUtility.createUsernameMessage(nextPlayer.getName(), Commands.NEXT_PLAYER).send(sessionManagementService.getSessionForUser(u.getName()));
            }
        }
    }

    @EventListener
    public void onFirstPlayerEvent(FirstPlayerEvent event) throws UserNotFoundException {
        String username = event.getUsername();
        User user = userService.getUser(username);
        if (user.getSequence() == 1) {
            notifyFirstPlayer(user);
        }
    }

    private void notifyFirstPlayer(User user) {
        Set<User> users = user.getLobby().getUsers();
        for (User u : users) {
            MessagingUtility.createUsernameMessage(user.getName(), Commands.FIRST_PLAYER).send(sessionManagementService.getSessionForUser(u.getName()));
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

}