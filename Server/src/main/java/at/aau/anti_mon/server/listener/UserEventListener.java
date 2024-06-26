package at.aau.anti_mon.server.listener;

import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.enums.Figures;
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
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Event-Listener for user interactions
 */
@Component
public class UserEventListener {
    private static final String PLAYER_TAG = "Spieler ";
    private static final double DECREASE_PERCENT_OF_MONEY_FOR_CHEATING = 0.2;

    private final SecureRandom random;
    @Setter
    private int fixProbabilityForCheating = -1;
    private final LobbyService lobbyService;
    private final SessionManagementService sessionManagementService;
    private final UserService userService;

    private static final int CHANGE_BALANCE = 100;

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
        Logger.info(PLAYER_TAG + event.getUsername() + " hat die Lobby verlassen.");

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
        Logger.info(PLAYER_TAG + event.getUsername() + " ist bereit.");

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
        Logger.info(PLAYER_TAG + event.getUsername() + " hat das Spiel gestartet.");

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
        int nextlocation = location + dicenumber;

        int newBalance = user.getMoney();
        if (nextlocation > 40) {
            nextlocation = nextlocation - 40;
            newBalance += CHANGE_BALANCE;
        }
        if (nextlocation == 1) {
            dicenumber += CHANGE_BALANCE;
        }
        if (nextlocation == 31) {
            dicenumber += 20;
            nextlocation = 11;
            user.setUnavailableRounds(2);
        }
        user.setLocation(nextlocation);

        if (newBalance != user.getMoney()) {
            balanceChangedEvent(new ChangeBalanceEvent(sessionManagementService.getSessionForUser(username), username, newBalance));
        }

        HashSet<User> users = user.getLobby().getUsers();
        for (User u : users) {
            JsonDataUtility.sendDiceNumber(sessionManagementService.getSessionForUser(u.getName()), username, dicenumber, figure, location);
        }

        checkCheating(!event.getCheat(), user);

    }

    public void checkCheating(boolean canCheat, User user) {
        // suggest cheating with probability of 50% when it was not cheated till now
        if (!canCheat) {
            user.setCheating(true);
            startTimerForCatchingCheater(user);
            return;
        }
        if (user.getUnavailableRounds() > 0) {
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

    // it is only possible to catch a cheater within 2 Minutes
    private void startTimerForCatchingCheater(User user) {
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> user.setCheating(false), 2, TimeUnit.MINUTES);
    }


    @EventListener
    public void balanceChangedEvent(ChangeBalanceEvent event) throws UserNotFoundException {
        String username = event.getUsername();
        int newBalance = event.getNewBalance();

        User user = userService.getUser(username);
        user.setMoney(newBalance);
        HashSet<User> users = user.getLobby().getUsers();
        for (User u : users) {
            JsonDataUtility.sendNewBalance(sessionManagementService.getSessionForUser(u.getName()), username, newBalance);
        }
    }

    @EventListener
    public void onReportCheatingEvent(ReportCheatingEvent event) throws UserNotFoundException {
        String username = event.getUsername();
        String cheatingUsername = event.getCheatingUsername();
        User cheater = userService.getUser(cheatingUsername);
        // report to the Reporter that the suggestion about cheating was in/correct
        boolean isCheating = cheater.isCheating();
        JsonDataUtility.sendResultOfReportCheating(sessionManagementService.getSessionForUser(username), username, username, isCheating);
        if (isCheating) {
            // report to the Cheater that s/he was caught by cheating
            JsonDataUtility.sendResultOfReportCheating(sessionManagementService.getSessionForUser(cheatingUsername), cheatingUsername, username, true);
            // reduce money of the cheater on 20% as a punishment for the cheating
            // update balance by all the players
            User user = userService.getUser(username);
            int newBalance = (int) (cheater.getMoney() - cheater.getMoney() * DECREASE_PERCENT_OF_MONEY_FOR_CHEATING);
            HashSet<User> users = user.getLobby().getUsers();
            user.setMoney(newBalance);
            for (User u : users) {
                JsonDataUtility.sendNewBalance(sessionManagementService.getSessionForUser(u.getName()), cheatingUsername, newBalance);
            }
            // set cheating back to false - a cheater can be caught only one time
            cheater.setCheating(false);
        }

    }

    @EventListener
    public void onNextPlayerEvent(NextPlayerEvent event) throws UserNotFoundException {
        Logger.info(PLAYER_TAG + event.getUsername() + " hat Finish ausgewählt.");
        String username = event.getUsername();
        User user = userService.getUser(username);
        user.setHasPlayed(true);
        HashSet<User> users = user.getLobby().getUsers();

        if (winGame(users)) {
            return;
        }

        int sequence = user.getSequence();
        int playerAmount = users.size();
        sequence++;
        if (haveAllPlayersPlayed(users)) {
            Logger.info("Alle " + PLAYER_TAG + "haben gespielt.");
            for (User u : users) {
                if (u.getUnavailableRounds() == 0) u.setHasPlayed(false);
            }
            sequence = 0;
            reduceUnavailableRounds(users);
        }
        User userCurrentSequence = getNextPlayer(users, sequence, playerAmount);
        Logger.info(PLAYER_TAG + userCurrentSequence.getName() + " is the next Player.");
        for (User u : users) {
            JsonDataUtility.sendNextPlayer(sessionManagementService.getSessionForUser(u.getName()), userCurrentSequence.getName());
        }
    }
    public boolean winGame(Set<User> users){
        int numberofplayers = 0;
        User winner = null;
        for (User u : users) {
            if (u.getUnavailableRounds() >= 0) {
                numberofplayers++;
                winner = u;
            }
        }
        boolean isWinner = numberofplayers == 1;
        if (isWinner) {
            JsonDataUtility.sendWinGame(sessionManagementService.getSessionForUser(winner.getName()), winner.getName());
        }
        return isWinner;
    }

    private boolean haveAllPlayersPlayed(HashSet<User> users) {
        int havePlayed = users.stream().filter(User::isHasPlayed).mapToInt(u -> 1).sum();
        Logger.info(PLAYER_TAG + "die gespielt haben: " + havePlayed);
        return havePlayed == users.size();
    }

    private void reduceUnavailableRounds(HashSet<User> users) {
        for (User u : users) {
            if (u.getUnavailableRounds() > 0) {
                u.setUnavailableRounds(u.getUnavailableRounds() - 1);
            }
        }
    }

    public User getNextPlayer(Set<User> users, int sequence, int playerAmount) {
        if (playerAmount == 1) {
            return users.iterator().next();
        }
        ArrayList<User> usersList = new ArrayList<>(users);
        usersList.sort(Comparator.comparingInt(User::getSequence));
        int i = sequence;
        while (i <= playerAmount) {
            if (i == playerAmount) {
                i = 0;
            }
            User u = usersList.get(i);
            Logger.info(PLAYER_TAG + u.getName() + " mit Sequence " + u.getSequence() + " und unavailableRounds " + u.getUnavailableRounds());
            if (u.getUnavailableRounds() == 0) {
                return u;
            }
            i++;
            if (i == sequence) {
                break;
            }
        }
        User userCurrentSequence = usersList.iterator().next();
        userCurrentSequence.setUnavailableRounds(0);
        return userCurrentSequence;
    }

    @EventListener
    public void onFirstPlayerEvent(FirstPlayerEvent event) throws UserNotFoundException {
        Logger.info("Wir sind in FirstPlayerEventListener.");
        String username = event.getUsername();
        User user = userService.getUser(username);
        HashSet<User> users = user.getLobby().getUsers();
        Logger.info("Wir haben die Nummer: " + user.getSequence());
        if (user.getSequence() == 0) {
            Logger.info(PLAYER_TAG + event.getUsername() + " ist " + PLAYER_TAG + "1.");
            for (User u : users) {
                JsonDataUtility.sendFirstPlayer(sessionManagementService.getSessionForUser(u.getName()), user.getName());
            }
        }
    }

    @EventListener
    public void onLoseGameEvent(LoseGameEvent event) throws UserNotFoundException{
        Logger.info("Wir sind in LoseGameEventListener.");
        String username = event.getUsername();
        User user = userService.getUser(username);
        user.setUnavailableRounds(-1);
        HashSet<User> users = user.getLobby().getUsers();
        for (User u : users) {
            JsonDataUtility.sendLoseGame(sessionManagementService.getSessionForUser(u.getName()), user.getName());
        }
    }

    @EventListener
    public void onEndGameEvent(EndGameEvent event) throws UserNotFoundException {
        Logger.info("Wir sind in EndGameEventListener.");
        String username = event.getUsername();
        User user = userService.getUser(username);
        HashSet<User> users = user.getLobby().getUsers();
        int rank;
        for (User user1 : users) {
            rank = 1;
            for (User user2 : users) {
                if(user1.getMoney()<user2.getMoney()){
                    rank++;
                }
            }
            JsonDataUtility.sendEndGame(sessionManagementService.getSessionForUser(user1.getName()),rank);
        }
    }
}