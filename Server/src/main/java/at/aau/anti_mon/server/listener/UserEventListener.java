package at.aau.anti_mon.server.listener;

import java.security.SecureRandom;
import java.util.*;

import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.enums.Figures;
import at.aau.anti_mon.server.events.*;
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
    private SecureRandom random;
    private int fixProbabilityForCheating = -1;
    private final LobbyService lobbyService;
    private final SessionManagementService sessionManagementService;
    private final UserService userService;
    String player ="Spieler ";

    public void setFixProbabilityForCheating(int number) {
        this.fixProbabilityForCheating = number;
    }

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
        // create User
        User user = userService.findOrCreateUser(event.getUsername(), event.getSession());
        // create Lobby
        Lobby newLobby = lobbyService.createLobby(user);

        Logger.info("SERVER: Spiel erstellt mit PIN: " + newLobby.getPin());
        JsonDataUtility.sendPin(event.getSession(), String.valueOf(newLobby.getPin()));
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

        // create User
        User joinedUser = userService.findOrCreateUser(event.getUsername(), event.getSession());
        Logger.debug("User " + joinedUser.getName() + " joined the lobby." + " Is owner: " + joinedUser.isOwner());

        Lobby joinedLobby = lobbyService.findLobbyByPin(event.getPin());
        // Füge den joinedUser zur Lobby hinzu
        lobbyService.joinLobby(event.getPin(), joinedUser.getName());

        HashSet<User> users = joinedLobby.getUsers();
        Logger.info("Users in Lobby: " + users.size());
        // Sende allen Spielern in der Lobby die Information, dass der Spieler der Lobby
        // beigetreten ist
        for (User user : users) {
            if (!user.equals(joinedUser))
                JsonDataUtility.sendJoinedUser(sessionManagementService.getSessionForUser(user.getName()), joinedUser);
        }

        // Sende dem neuen Spieler alle Spieler in der Lobby
        for (User user : users) {
            if (!user.equals(joinedUser))
                JsonDataUtility.sendJoinedUser(sessionManagementService.getSessionForUser(joinedUser.getName()), user);
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

        lobbyService.leaveLobby(event.getPin(), event.getUsername());
        Logger.info(player + event.getUsername() + " hat die Lobby verlassen.");

        HashSet<User> users = lobbyService.findLobbyByPin(event.getPin()).getUsers();
        for (User user : users) {

            // Sende allen Spielern in der Lobby die Information, dass der Spieler die Lobby
            // verlassen hat
            JsonDataUtility.sendLeavedUser(sessionManagementService.getSessionForUser(user.getName()),
                    event.getUsername());

            // Sende dem neuen Spieler Bestätigung des Verlassens
            JsonDataUtility.sendAnswer(sessionManagementService.getSessionForUser(event.getUsername()), "SUCCESS");
            JsonDataUtility.sendInfo(sessionManagementService.getSessionForUser(event.getUsername()),
                    "Erfolgreich die Lobby verlassen.");
        }
    }

    @EventListener
    public void onReadyUserEvent(UserReadyLobbyEvent event) throws UserNotFoundException, LobbyNotFoundException {
        sessionManagementService.registerUserWithSession(event.getUsername(), event.getSession());
        lobbyService.readyUser(event.getPin(), event.getUsername());
        Logger.info(player + event.getUsername() + " ist bereit.");

        User readiedUser = userService.getUser(event.getUsername());

        HashSet<User> users = lobbyService.findLobbyByPin(event.getPin()).getUsers();
        for (User user : users) {
            JsonDataUtility.sendReadyUser(sessionManagementService.getSessionForUser(user.getName()), event.getUsername(), readiedUser.isReady());
        }
    }

    @EventListener
    public void onStartGameEvent(UserStartedGameEvent event) throws LobbyNotFoundException {
        sessionManagementService.registerUserWithSession(event.getUsername(), event.getSession());
        lobbyService.startGame(event.getPin(), event.getUsername());
        Logger.info(player + event.getUsername() + " hat das Spiel gestartet.");

        HashSet<User> users = lobbyService.findLobbyByPin(event.getPin()).getUsers();
        // convert to userDtos
        ArrayList<UserDTO> usersList = new ArrayList<>();
        for (User user : users) {
            usersList.add(new UserDTO(user.getName(), user.isOwner(), user.isReady(), user.getRole(), user.getFigure()));
        }
        for (User user : users) {
            JsonDataUtility.sendStartGame(sessionManagementService.getSessionForUser(user.getName()), usersList);
        }
    }

    @EventListener
    public void onDiceNumberEvent(DiceNumberEvent event) throws UserNotFoundException {
        String username = event.getUsername();
        Integer dicenumber = event.getDicenumber();

        User user = userService.getUser(username);
        Figures figure = user.getFigure();
        int location = user.getLocation();
        int nextlocation = location+dicenumber;

        if(nextlocation>40){
            nextlocation = nextlocation-40;
        }
        user.setLocation(nextlocation);

        HashSet<User> users = user.getLobby().getUsers();
        for (User u : users) {
            JsonDataUtility.sendDiceNumber(sessionManagementService.getSessionForUser(u.getName()), username,dicenumber, figure,location);
        }

        checkCheating(!event.getCheat(), user);

    }

    public void checkCheating(boolean canCheat, User user) {
        // suggest cheating with probability of 50% when it was not cheated till now
        if(!canCheat)
        {
            return;
        }
        int probability = fixProbabilityForCheating;
        //if -1 (aka not fixed rng), use rng generator to roll a number
        if (probability < 0) {
            probability = random.nextInt(100) + 1;
        }
        if (probability > 50) {
            WebSocketSession session = sessionManagementService.getSessionForUser(user.getName());
            JsonDataUtility.sendCheating(session);
        }
    }

    @EventListener
    public void balanceChangedEvent(ChangeBalanceEvent event) throws UserNotFoundException {
        String username = event.getUsername();
        Integer new_balance = event.getNewBalance();

        User user = userService.getUser(username);
        user.setMoney(new_balance);
        HashSet<User> users = user.getLobby().getUsers();
        for (User u : users) {
            JsonDataUtility.sendNewBalance(sessionManagementService.getSessionForUser(u.getName()), username, new_balance);
        }
    }

    @EventListener
    public void onNextPlayerEvent(NextPlayerEvent event) throws UserNotFoundException {
        Logger.info(player + event.getUsername() + " hat Finish ausgewählt.");
        String username = event.getUsername();
        User user = userService.getUser(username);
        int sequence = user.getSequence();
        HashSet<User> users = user.getLobby().getUsers();
        int playernumber = users.size();
        if(sequence==playernumber){sequence=0;}
        sequence++;

        int finalSequence = sequence;
        User userCurrentSequence = users.stream().filter(u -> u.getSequence() == finalSequence).toList().get(0);
        Logger.info(player + userCurrentSequence.getName() + " is the next Player.");
        for (User u : users) {
            JsonDataUtility.sendNextPlayer(sessionManagementService.getSessionForUser(u.getName()), userCurrentSequence.getName());
        }
    }
    @EventListener
    public void onFirstPlayerEvent(FirstPlayerEvent event) throws UserNotFoundException{
        Logger.info("Wir sind in FirstPlayerEventListener.");
        String username = event.getUsername();
        User user = userService.getUser(username);
        HashSet<User> users = user.getLobby().getUsers();
        Logger.info("Wir haben die Nummer: "+ user.getSequence());
        if(user.getSequence() == 1){
            Logger.info(player + event.getUsername() + " ist Spieler 1.");
            for (User u : users){
                JsonDataUtility.sendFirstPlayer(sessionManagementService.getSessionForUser(u.getName()),user.getName());
            }
        }
    }
}