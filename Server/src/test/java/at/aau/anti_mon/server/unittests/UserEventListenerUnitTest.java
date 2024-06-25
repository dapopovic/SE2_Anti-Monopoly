package at.aau.anti_mon.server.unittests;

import at.aau.anti_mon.server.dtos.LobbyDTO;
import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.enums.Figures;
import at.aau.anti_mon.server.events.*;
import at.aau.anti_mon.server.exceptions.LobbyNotFoundException;
import at.aau.anti_mon.server.exceptions.UserNotFoundException;
import at.aau.anti_mon.server.game.Lobby;
import at.aau.anti_mon.server.game.User;
import at.aau.anti_mon.server.listener.UserEventListener;
import at.aau.anti_mon.server.service.LobbyService;
import at.aau.anti_mon.server.service.SessionManagementService;
import at.aau.anti_mon.server.service.UserService;
import at.aau.anti_mon.server.utilities.JsonDataUtility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserEventListenerUnitTest {

    @Mock
    private SessionManagementService sessionManagementService;

    @Mock
    private UserService userService;

    @Mock
    private LobbyService lobbyService;

    @InjectMocks
    private UserEventListener userEventListener;

    private final static int AMOUNT_PLAYERS = 4;

  /*  @Test
    void onUserJoinedLobbyEventShouldCallCorrectServiceMethod()
            throws UserNotFoundException, LobbyNotFoundException, LobbyIsFullException, IOException {

        // Given
        WebSocketSession session1 = Mockito.mock(WebSocketSession.class);
        Mockito.when(session1.isOpen()).thenReturn(true);
        TextMessage message = Mockito.mock(TextMessage.class);
    //    Mockito.when(new TextMessage(Mockito.anyString())).thenReturn(message);

        WebSocketSession session2 = Mockito.mock(WebSocketSession.class);
        Mockito.when(session2.isOpen()).thenReturn(true);

        Mockito.when(sessionManagementService.getSessionForUser("testUser")).thenReturn(session1);
        Mockito.when(sessionManagementService.getSessionForUser("lobbyCreator")).thenReturn(session2);

        HashSet<User> users = new HashSet<>();
        User user1 = new User("lobbyCreator", session2);
        User user2 = new User("testUser", session1);
        users.add(user1);

        Lobby lobby = Mockito.mock(Lobby.class);
        Mockito.when(lobby.getUsers()).thenReturn(users);
        Mockito.when(lobby.getPin()).thenReturn(1234);

        Mockito.when(lobbyService.findOptionalLobbyByPin(1234)).thenReturn(Optional.of(lobby));
        Mockito.when(userService.findOrCreateUser("testUser", session1)).thenReturn(user2);

        UserJoinedLobbyEvent event = new UserJoinedLobbyEvent(session1, 1234, "testUser");

        // When
        userEventListener.onUserJoinedLobbyEvent(event);

        // Then
        Mockito.verify(lobbyService, Mockito.times(1)).joinLobby(1234, "testUser");

        // Verify that the message was sent
        Mockito.verify(session1, Mockito.times(2)).sendMessage(Mockito.any(TextMessage.class));

        // verify the exact message content
        ArgumentCaptor<TextMessage> messageCaptor = ArgumentCaptor.forClass(TextMessage.class);
        Mockito.verify(session1,Mockito.times(2)).sendMessage(messageCaptor.capture());
    }

   */

    @Test
    void onLeaveLobbyEventShouldCallCorrectServiceMethod()
            throws UserNotFoundException, LobbyNotFoundException {
        WebSocketSession session2 = Mockito.mock(WebSocketSession.class);

        Mockito.when(session2.isOpen()).thenReturn(true);
        Mockito.when(sessionManagementService.getSessionForUser("lobbyCreator")).thenReturn(session2);

        HashSet<User> users = new HashSet<>();
        User user1 = new User("lobbyCreator", session2);
        users.add(user1);

        Lobby lobby = Mockito.mock(Lobby.class);
        Mockito.when(lobby.getUsers()).thenReturn(users);

        Mockito.when(lobbyService.findOptionalLobbyByPin(1234)).thenReturn(Optional.of(lobby));
        Mockito.when(lobby.hasUser("lobbyCreator")).thenReturn(true);
        Mockito.when(lobby.getPin()).thenReturn(1234);

        UserLeftLobbyEvent event = new UserLeftLobbyEvent(session2, 1234, "lobbyCreator");

        // When
        userEventListener.onLeaveLobbyEvent(event);

        // Then
        Mockito.verify(lobbyService, Mockito.times(1)).leaveLobby(1234, "lobbyCreator");
    }

    @Test
    void onReadyUserEventShouldCallCorrectServiceMethod() throws UserNotFoundException, LobbyNotFoundException {
        // Given
        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        Mockito.when(session.isOpen()).thenReturn(true);

        UserDTO userDTO = new UserDTO("user1", false, true, null, null);
        LobbyDTO lobbyDTO = new LobbyDTO(1234);
        UserReadyLobbyEvent event = new UserReadyLobbyEvent(session, lobbyDTO, userDTO);

        User user = Mockito.mock(User.class);
        Mockito.when(user.getUserName()).thenReturn("user1");
        Mockito.when(user.isReady()).thenReturn(false);
        Mockito.when(user.isReady()).thenReturn(false);

        Lobby m = Mockito.mock(Lobby.class);
        Mockito.when(m.getUsers()).thenReturn(new HashSet<>(List.of(user)));
        Mockito.when(assertDoesNotThrow(() -> lobbyService.findOptionalLobbyByPin(1234))).thenReturn(Optional.of(m));
        Mockito.when(userService.getUser("user1")).thenReturn(user);
        Mockito.when(sessionManagementService.getSessionForUser("user1")).thenReturn(session);
        Mockito.when(lobbyService.findOptionalLobbyByPin(1234)).thenReturn(Optional.of(m));
        Mockito.when(m.hasUser("user1")).thenReturn(true);


        // When
        assertDoesNotThrow(() -> userEventListener.onReadyUserEvent(event));
        Mockito.verify(lobbyService).readyUser(1234, "user1");
        Mockito.verify(m).getUsers();
        Mockito.verify(lobbyService).findOptionalLobbyByPin(1234);
        Mockito.verify(userService).getUser("user1");
    }

    @Test
    void onStartGameEventShouldCallCorrectServiceMethod() {
        // Given
        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        Mockito.when(session.isOpen()).thenReturn(true);

        UserDTO userDTO = new UserDTO("user1", false, true, null, null);
        LobbyDTO lobbyDTO = new LobbyDTO(1234);
        UserStartedGameEvent event = new UserStartedGameEvent(session, lobbyDTO, userDTO);

        User user = Mockito.mock(User.class);
        Mockito.when(user.getUserName()).thenReturn("user1");
        Mockito.when(user.isReady()).thenReturn(false);
        Mockito.when(user.isReady()).thenReturn(false);

        Lobby m = Mockito.mock(Lobby.class);
        Mockito.when(m.getUsers()).thenReturn(new HashSet<>(List.of(user)));
        Mockito.when(assertDoesNotThrow(() -> lobbyService.findOptionalLobbyByPin(1234))).thenReturn(Optional.of(m));
        Mockito.when(sessionManagementService.getSessionForUser("user1")).thenReturn(session);
        Mockito.when(lobbyService.findOptionalLobbyByPin(1234)).thenReturn(Optional.of(m));
        Mockito.when(m.hasUser("user1")).thenReturn(true);

        // When
        assertDoesNotThrow(() -> userEventListener.onStartGameEvent(event));
        Mockito.verify(lobbyService).startGame(1234, "user1");
        Mockito.verify(m).getUsers();
        Mockito.verify(lobbyService).findOptionalLobbyByPin(1234);
    }

    @Test
    void onRollDiceEventShouldCallCorrectServiceMethod() throws UserNotFoundException {
        // Given
        WebSocketSession session = Mockito.mock(WebSocketSession.class);

        DiceNumberEvent event = new DiceNumberEvent(session, "testuser", 5, 1234, true);

        Lobby lobby = Mockito.mock(Lobby.class);
        Mockito.when(lobbyService.findOptionalLobbyByPin(1234)).thenReturn(Optional.of(lobby));
        Mockito.when(lobby.hasUser("testuser")).thenReturn(true);

        User user = Mockito.mock(User.class);
        Mockito.when(user.getUserName()).thenReturn("testuser");
        Mockito.when(user.getFigure()).thenReturn(Figures.GREEN_CIRCLE);
        Mockito.when(user.getPlayerLocation()).thenReturn(0);

        HashSet<User> users = new HashSet<>();
        users.add(user);
        Mockito.when(lobby.getUsers()).thenReturn(users);

        Mockito.when(userService.getUser("testuser")).thenReturn(user);
        Mockito.when(sessionManagementService.getSessionForUser("testuser")).thenReturn(session);

        // When
        assertDoesNotThrow(() -> userEventListener.onDiceNumberEvent(event));
        Mockito.verify(userService).getUser("testuser");
        Mockito.verify(sessionManagementService).getSessionForUser("testuser");
    }

    @Test
    void onChangeBalanceEventShouldCallCorrectServiceMethod() throws UserNotFoundException {
        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        ChangeBalanceEvent event = new ChangeBalanceEvent(session, "Julia", 1700);
        Lobby lobby = Mockito.mock(Lobby.class);
        User user = Mockito.mock(User.class);
        Mockito.when(user.getUserName()).thenReturn("Julia");
        Mockito.when(user.getLobby()).thenReturn(lobby);
        HashSet<User> users = new HashSet<>();
        users.add(user);
        Mockito.when(lobby.getUsers()).thenReturn(users);
        Mockito.when(userService.getUser("Julia")).thenReturn(user);
        Mockito.when(sessionManagementService.getSessionForUser("Julia")).thenReturn(session);

        assertDoesNotThrow(() -> userEventListener.balanceChangedEvent(event));
        Mockito.verify(userService).getUser("Julia");
        Mockito.verify(sessionManagementService).getSessionForUser("Julia");
        Mockito.verify(user).setMoney(1700);
    }

    @Test
    void testCheckCheatingCanNotCheat() {
        WebSocketSession session = Mockito.mock(WebSocketSession.class);

        //Setup rng, server should not offer cheating despite good rng value(rngvalue>50)
        userEventListener.setFIXED_PROBABILITY_FOR_CHEATING(65);

        //Setup mock class behavior
        MockedStatic<JsonDataUtility> mockedStatic = Mockito.mockStatic(JsonDataUtility.class);
        User user = Mockito.mock(User.class);
        //no method mocking, as neither session nor user should get called since cheating not allowed

        DiceNumberEvent event = Mockito.mock(DiceNumberEvent.class);
        //Perform test
        userEventListener.checkCheating( user);

        //verify method calls
        Mockito.verify(sessionManagementService, Mockito.never()).getSessionForUser("Julia");
        mockedStatic.verify(() -> JsonDataUtility.sendCheating(session), Mockito.times(0));
        mockedStatic.close();
    }

    @Test
    void testCheckCheatingCanCheatButNoOfferFromServer() {
        //Setup rng, server should not offer cheating (rngvalue<50)
        userEventListener.setFIXED_PROBABILITY_FOR_CHEATING(45);

        //Setup mock class behavior
        MockedStatic<JsonDataUtility> mockedStatic = Mockito.mockStatic(JsonDataUtility.class);
        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        User user = Mockito.mock(User.class);
        //no method mocking, as neither session nor user should get called since cheating rng unlucky

        DiceNumberEvent event = Mockito.mock(DiceNumberEvent.class);
        //Perform test
        userEventListener.checkCheating( user);

        //verify method calls
        Mockito.verify(sessionManagementService, Mockito.never()).getSessionForUser("Julia");
        mockedStatic.verify(() -> JsonDataUtility.sendCheating(session), Mockito.times(0));
        mockedStatic.close();
    }

  /*  @Test
    void testCheckCheatingCanCheatAndOfferFromServer() {
        WebSocketSession session = Mockito.mock(WebSocketSession.class);

        //Setup rng, server should offer cheating (rngvalue<50)
        userEventListener.setFIXED_PROBABILITY_FOR_CHEATING(65);

        //Setup mock class behavior

        User user = Mockito.mock(User.class);
        Mockito.when(user.getName()).thenReturn("Julia");

        Mockito.when(sessionManagementService.getSessionForUser(user.getName())).thenReturn(session);

        //Perform test
        userEventListener.checkCheating( user);

        //verify method calls
        Mockito.verify(sessionManagementService).getSessionForUser(user.getName());
    }

   */

    @Test
    void onFirstPlayerEventShouldCallCorrectServiceMethod() throws UserNotFoundException {
        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        FirstPlayerEvent event = new FirstPlayerEvent(session, "user0");
        Lobby lobby = Mockito.mock(Lobby.class);
        User user = Mockito.mock(User.class);
        Mockito.when(user.getUserName()).thenReturn("user0");
        Mockito.when(user.getLobby()).thenReturn(lobby);
        HashSet<User> users = new HashSet<>();
        users.add(user);
        Mockito.when(lobby.getUsers()).thenReturn(users);
        Mockito.when(userService.getUser("user0")).thenReturn(user);
        Mockito.when(sessionManagementService.getSessionForUser("user0")).thenReturn(session);
        Mockito.when(user.getSequence()).thenReturn(0);

        assertDoesNotThrow(() -> userEventListener.onFirstPlayerEvent(event));
        Mockito.verify(userService).getUser("user0");
        Mockito.verify(sessionManagementService).getSessionForUser("user0");
    }

    @Test
    void onNextPlayerEventShouldCallCorrectServiceMethod() throws UserNotFoundException {
        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        NextPlayerEvent event = new NextPlayerEvent(session, "user1");
        Lobby lobby = Mockito.mock(Lobby.class);
        User user = Mockito.mock(User.class);
        Mockito.when(user.getUserName()).thenReturn("user1");
        Mockito.when(user.getLobby()).thenReturn(lobby);
        Mockito.when(user.getUnavailableRounds()).thenReturn(0);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user2.getUserName()).thenReturn("user2");
        Mockito.when(user2.getUnavailableRounds()).thenReturn(0);

        HashSet<User> users = new HashSet<>();
        users.add(user);
        users.add(user2);

        Mockito.when(lobby.getUsers()).thenReturn(users);
        Mockito.when(userService.getUser("user1")).thenReturn(user);
        Mockito.when(sessionManagementService.getSessionForUser("user1")).thenReturn(session);
        Mockito.when(sessionManagementService.getSessionForUser("user2")).thenReturn(session);
        Mockito.when(user.getSequence()).thenReturn(1);

        assertDoesNotThrow(() -> userEventListener.onNextPlayerEvent(event));
        Mockito.verify(userService).getUser("user1");
        Mockito.verify(sessionManagementService).getSessionForUser("user1");
    }

    @Test
    void onLoseGameEventShouldCallCorrectServiceMethod() throws UserNotFoundException {
        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        LooseGameEvent event = new LooseGameEvent(session, "user1");
        Lobby lobby = Mockito.mock(Lobby.class);
        User user = Mockito.mock(User.class);
        Mockito.when(user.getUserName()).thenReturn("user1");
        Mockito.when(user.getLobby()).thenReturn(lobby);
        User user2 = Mockito.mock(User.class);
        Mockito.when(user2.getUserName()).thenReturn("user2");

        HashSet<User> users = new HashSet<>();
        users.add(user);
        users.add(user2);

        Mockito.when(lobby.getUsers()).thenReturn(users);
        Mockito.when(userService.getUser("user1")).thenReturn(user);
        Mockito.when(sessionManagementService.getSessionForUser("user1")).thenReturn(session);
        Mockito.when(sessionManagementService.getSessionForUser("user2")).thenReturn(session);

        assertDoesNotThrow(()-> userEventListener.onLoseGameEvent(event));
        Mockito.verify(userService).getUser("user1");
        Mockito.verify(sessionManagementService).getSessionForUser("user1");
    }

    @Test
    void onWinGameEventShouldCallCorrectServiceMethod() {
        MockedStatic<JsonDataUtility> mockedStatic = Mockito.mockStatic(JsonDataUtility.class);

        WebSocketSession session = Mockito.mock(WebSocketSession.class);

        User user = Mockito.mock(User.class);
        Mockito.when(user.getUserName()).thenReturn("user1");
        Mockito.when(user.getUnavailableRounds()).thenReturn(0);

        HashSet<User> users = new HashSet<>();
        users.add(user);

        Mockito.when(sessionManagementService.getSessionForUser("user1")).thenReturn(session);

        userEventListener.checkWinCondition(users);

        Mockito.verify(sessionManagementService).getSessionForUser("user1");
        mockedStatic.verify(() -> JsonDataUtility.sendWinGame(session,"user1"), Mockito.times(1));
        mockedStatic.close();
    }
    @Test
    void onWinGameEventButAllAvailableRoundsMinus1() {
        MockedStatic<JsonDataUtility> mockedStatic = Mockito.mockStatic(JsonDataUtility.class);

        WebSocketSession session = Mockito.mock(WebSocketSession.class);

        User user = Mockito.mock(User.class);
        Mockito.when(user.getUnavailableRounds()).thenReturn(-1);

        HashSet<User> users = new HashSet<>();
        users.add(user);

        userEventListener.checkWinCondition(users);

        Mockito.verify(sessionManagementService, Mockito.never()).getSessionForUser("user1");
        mockedStatic.verify(() -> JsonDataUtility.sendWinGame(session,"user1"), Mockito.never());
        mockedStatic.close();
    }

    @Test
    void onNextPlayerEventAllPlayersPlayed() {
        HashSet<User> users = new HashSet<>();
        Lobby lobby = Mockito.mock(Lobby.class);
        Mockito.when(lobby.getUsers()).thenReturn(users);
        int sequence = 0;
        for (int i = 0; i < AMOUNT_PLAYERS; i++) {
            WebSocketSession session = Mockito.mock(WebSocketSession.class);
            User user = Mockito.mock(User.class);
            Mockito.when(user.getUserName()).thenReturn("user" + i);
            Mockito.when(user.getSequence()).thenReturn(sequence);
            Mockito.when(user.isHasPlayed()).thenReturn(true);
            Mockito.when(sessionManagementService.getSessionForUser("user" + i)).thenReturn(session);
            users.add(user);
            sequence++;
        }
        ArrayList<User> userList = new ArrayList<>(users);
        userList.sort(Comparator.comparing(User::getSequence));
        NextPlayerEvent event = new NextPlayerEvent(Mockito.mock(WebSocketSession.class), "user0");
        User user = userList.get(0);
        Mockito.when(user.getLobby()).thenReturn(lobby);
        Mockito.when(assertDoesNotThrow(() -> userService.getUser("user0"))).thenReturn(user);
        assertDoesNotThrow(() -> userEventListener.onNextPlayerEvent(event));
        Mockito.verify(userList.get(0), Mockito.times(3 + AMOUNT_PLAYERS)).getUserName();
    }
    @Test
    void onNextPlayerEventAllPlayersPlayedButOneHasUnavailableRounds() {
        HashSet<User> users = new HashSet<>();
        Lobby lobby = Mockito.mock(Lobby.class);
        Mockito.when(lobby.getUsers()).thenReturn(users);
        int sequence = 0;
        for (int i = 0; i < AMOUNT_PLAYERS; i++) {
            WebSocketSession session = Mockito.mock(WebSocketSession.class);
            User user = Mockito.mock(User.class);
            Mockito.when(user.getUserName()).thenReturn("user" + i);
            Mockito.when(user.getSequence()).thenReturn(sequence);
            Mockito.when(user.isHasPlayed()).thenReturn(true);
            if (i == 0) {
                Mockito.when(user.getUnavailableRounds()).thenReturn(2);
            }
            Mockito.when(sessionManagementService.getSessionForUser("user" + i)).thenReturn(session);
            users.add(user);
            sequence++;
        }
        ArrayList<User> userList = new ArrayList<>(users);
        userList.sort(Comparator.comparing(User::getSequence));
        NextPlayerEvent event = new NextPlayerEvent(Mockito.mock(WebSocketSession.class), "user0");
        User user = userList.get(0);
        Mockito.when(user.getLobby()).thenReturn(lobby);
        Mockito.when(assertDoesNotThrow(() -> userService.getUser("user0"))).thenReturn(user);
        assertDoesNotThrow(() -> userEventListener.onNextPlayerEvent(event));
        Mockito.verify(userList.get(1), Mockito.times(3 + AMOUNT_PLAYERS)).getUserName();
    }



    private HashSet<User> initializeUsers(int unavailableUserIndex, int unavailableRounds) {
        HashSet<User> users = new HashSet<>();
        int sequence = 0;
        for (int i = 0; i < AMOUNT_PLAYERS; i++) {
            WebSocketSession session = Mockito.mock(WebSocketSession.class);
            Mockito.when(session.isOpen()).thenReturn(true);
            Mockito.when(sessionManagementService.getSessionForUser("user"+i)).thenReturn(session);
            User user = Mockito.mock(User.class);
            Mockito.when(user.getUserName()).thenReturn("user" + i);
            Mockito.when(user.getSequence()).thenReturn(sequence);
            Mockito.when(user.getUnavailableRounds()).thenReturn(i == unavailableUserIndex ? unavailableRounds : 0);
            users.add(user);
            sequence++;
        }
        return users;
    }

    private ArrayList<User> sortUsers(HashSet<User> users) {
        ArrayList<User> userList = new ArrayList<>(users);
        userList.sort(Comparator.comparing(User::getSequence));
        return userList;
    }

   /* @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void onGetPlayerAllUsersUnAvailableRoundsGreaterThan0(int randomizedUser) {

        HashSet<User> users = initializeUsers(-1, 2);  // -1 bedeutet, dass alle Benutzer 2 Runden unpassierbar sind
        ArrayList<User> userList = sortUsers(users);

        Lobby lobby = Mockito.mock(Lobby.class);
        Mockito.when(lobby.getUsers()).thenReturn(users);

        User user = userList.get(randomizedUser);
        Mockito.when(user.getLobby()).thenReturn(lobby);
        Mockito.when(assertDoesNotThrow(() -> userService.getUser("user" + randomizedUser))).thenReturn(user);

        Mockito.verify(userList.get(randomizedUser)).getName();
    }

    */



    @ParameterizedTest
    @CsvSource({
            "0, 2",
            "1, 2",
            "2, 2",
            "3, 2"
    })
    void onGetPlayerOneUserAvailableRoundsGreaterThan0(int randomizedUser, int unavailableRounds) {
        HashSet<User> users = initializeUsers(randomizedUser, unavailableRounds);
        ArrayList<User> userList = sortUsers(users);

        Lobby lobby = Mockito.mock(Lobby.class);
        Mockito.when(lobby.getUsers()).thenReturn(users);

        int userIndex = randomizedUser == 0 ? 3 : randomizedUser - 1;
        NextPlayerEvent event = new NextPlayerEvent(Mockito.mock(WebSocketSession.class), "user" + userIndex);
        User user = userList.get(userIndex);
        Mockito.when(user.getLobby()).thenReturn(lobby);
        Mockito.when(assertDoesNotThrow(() -> userService.getUser("user" + userIndex))).thenReturn(user);
        assertDoesNotThrow(() -> userEventListener.onNextPlayerEvent(event));

        int nextUser = (userIndex == 3) ? 1 : (userIndex + 2 == 4) ? 0 : userIndex + 2;
        Mockito.verify(userList.get(nextUser), Mockito.times(3 + AMOUNT_PLAYERS)).getUserName();
    }

    /*
    @ParameterizedTest
    @CsvSource({
            "0, 2",
            "1, 2",
            "2, 2",
            "3, 2"
    })
    void onGetPlayerAllUsersUntilRandomizedUserAvailableRoundsGreaterThan0(int randomizedUser, int unavailableRounds) {
        HashSet<User> users = initializeUsers(randomizedUser, unavailableRounds);
        ArrayList<User> userList = sortUsers(users);

        Lobby lobby = Mockito.mock(Lobby.class);
        Mockito.when(lobby.getUsers()).thenReturn(users);

        int userIndex = randomizedUser == 0 ? 3 : randomizedUser - 1;
        NextPlayerEvent event = new NextPlayerEvent(Mockito.mock(WebSocketSession.class), "user" + userIndex);
        User user1 = userList.get(userIndex);
        Mockito.when(user1.getLobby()).thenReturn(lobby);
        Mockito.when(assertDoesNotThrow(() -> userService.getUser("user" + userIndex))).thenReturn(user1);
        assertDoesNotThrow(() -> userEventListener.onNextPlayerEvent(event));

        int nextUser = 0;
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getUnavailableRounds() == 0) {
                nextUser = i;
                break;
            }
        }
        Mockito.verify(userList.get(nextUser), Mockito.times(3 + AMOUNT_PLAYERS)).getName();
    }

     */



 /*   @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void onGetPlayerAllUsersUnAvailableRoundsGreaterThan0(int randomizedUser) {
        HashSet<User> users = new HashSet<>();
        Lobby lobby = mock(Lobby.class);
        when(lobby.getUsers()).thenReturn(users);
        int sequence = 0;
        for (int i = 0; i < AMOUNT_PLAYERS; i++) {
            WebSocketSession session = mock(WebSocketSession.class);
            User user = mock(User.class);
            when(user.getName()).thenReturn("user" + i);
            when(user.getSequence()).thenReturn(sequence);
            when(user.getUnavailableRounds()).thenReturn(2);
            when(sessionManagementService.getSessionForUser("user" + i)).thenReturn(session);
            users.add(user);
            sequence++;
        }
        ArrayList<User> userList = new ArrayList<>(users);
        userList.sort(Comparator.comparing(User::getSequence));
        NextPlayerEvent event = new NextPlayerEvent(mock(WebSocketSession.class), "user" + randomizedUser);
        User user = userList.get(randomizedUser);
        when(user.getLobby()).thenReturn(lobby);
        when(assertDoesNotThrow(() -> userService.getUser("user" + randomizedUser))).thenReturn(user);
        assertDoesNotThrow(() -> userEventListener.onNextPlayerEvent(event));
        verify(userList.get(0), times(3 + AMOUNT_PLAYERS)).getName();
    }

  */

  /*  @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void onGetPlayerOneUserAvailableRoundsGreaterThan0(int randomizedUser) {
        HashSet<User> users = new HashSet<>();
        Lobby lobby = mock(Lobby.class);
        when(lobby.getUsers()).thenReturn(users);
        int sequence = 0;
        for (int i = 0; i < AMOUNT_PLAYERS; i++) {
            WebSocketSession session = mock(WebSocketSession.class);
            User user = mock(User.class);
            when(user.getName()).thenReturn("user" + i);
            when(user.getSequence()).thenReturn(sequence);
            if (i == randomizedUser) {
                when(user.getUnavailableRounds()).thenReturn(2);
            }
            when(sessionManagementService.getSessionForUser("user" + i)).thenReturn(session);
            users.add(user);
            sequence++;
        }
        int userIndex = randomizedUser == 0 ? 3 : randomizedUser - 1;
        NextPlayerEvent event = new NextPlayerEvent(mock(WebSocketSession.class), "user" + (userIndex));
        ArrayList<User> userList = new ArrayList<>(users);
        userList.sort(Comparator.comparing(User::getSequence));
        User user = userList.get(userIndex);
        when(user.getLobby()).thenReturn(lobby);
        when(assertDoesNotThrow(() -> userService.getUser("user" + (userIndex)))).thenReturn(user);
        assertDoesNotThrow(() -> userEventListener.onNextPlayerEvent(event));
        int nextUser = (userIndex) == 3 ? 1 : userIndex + 2 == 4 ? 0 : userIndex + 2;
        verify(userList.get(nextUser), times(3 + AMOUNT_PLAYERS)).getName();
    }

   */

   /* @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void onGetPlayerAllUsersUntilRandomizedUserAvailableRoundsGreaterThan0(int randomizedUser) {
        HashSet<User> users = new HashSet<>();
        Lobby lobby = mock(Lobby.class);
        when(lobby.getUsers()).thenReturn(users);
        int sequence = 0;
        for (int i = 0; i < AMOUNT_PLAYERS; i++) {
            WebSocketSession session = mock(WebSocketSession.class);
            User user = mock(User.class);
            when(user.getName()).thenReturn("user" + i);
            when(user.getSequence()).thenReturn(sequence);
            if (i <= randomizedUser) {
                when(user.getUnavailableRounds()).thenReturn(2);
            }
            when(sessionManagementService.getSessionForUser("user" + i)).thenReturn(session);
            users.add(user);
            sequence++;
        }
        sequence = 0;
        int userIndex = randomizedUser == 0 ? 3 : randomizedUser - 1;
        NextPlayerEvent event = new NextPlayerEvent(mock(WebSocketSession.class), "user" + (userIndex));
        ArrayList<User> userList = new ArrayList<>(users);
        userList.sort(Comparator.comparing(User::getSequence));
        User user1 = userList.get(userIndex);
        when(user1.getLobby()).thenReturn(lobby);
        when(assertDoesNotThrow(() -> userService.getUser("user" + userIndex))).thenReturn(user1);
        assertDoesNotThrow(() -> userEventListener.onNextPlayerEvent(event));

        int nextUser = 0;
        if (randomizedUser == sequence) {
            nextUser = 1;
        } else {
            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).getUnavailableRounds() == 0) {
                    nextUser = i;
                    break;
                }
            }
        }
        verify(userList.get(nextUser), times(3 + AMOUNT_PLAYERS)).getName();
    }

    */

    @Test
    void onGetPlayerOneUserAvailableRoundsGreaterThan0OnlyOneUser() {
        HashSet<User> users = new HashSet<>();
        Lobby lobby = Mockito.mock(Lobby.class);
        Mockito.when(lobby.getUsers()).thenReturn(users);
        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        User user = Mockito.mock(User.class);
        Mockito.when(user.getUserName()).thenReturn("user0");
        Mockito.when(user.getUnavailableRounds()).thenReturn(2);
        Mockito.when(sessionManagementService.getSessionForUser("user0")).thenReturn(session);
        users.add(user);

        NextPlayerEvent event = new NextPlayerEvent(Mockito.mock(WebSocketSession.class), "user0");
        user = users.iterator().next();
        Mockito.when(user.getLobby()).thenReturn(lobby);
        Mockito.when(assertDoesNotThrow(() -> userService.getUser("user0"))).thenReturn(user);
        assertDoesNotThrow(() -> userEventListener.onNextPlayerEvent(event));
        Mockito.verify(user, Mockito.times(2)).getUserName();
    }

    @Test
    void onEndGameEventShouldCallCorrectServiceMethod() throws UserNotFoundException {
        MockedStatic<JsonDataUtility> mockedStatic = Mockito.mockStatic(JsonDataUtility.class);

        WebSocketSession session = Mockito.mock(WebSocketSession.class);

        User user = Mockito.mock(User.class);
        Mockito.when(user.getUserName()).thenReturn("user1");
        Mockito.when(user.getMoney()).thenReturn(1500);

        Lobby lobby = Mockito.mock(Lobby.class);
        Mockito.when(user.getLobby()).thenReturn(lobby);

        HashSet<User> users = new HashSet<>();
        users.add(user);

        Mockito.when(sessionManagementService.getSessionForUser("user1")).thenReturn(session);
        Mockito.when(lobby.getUsers()).thenReturn(users);
        Mockito.when(userService.getUser("user1")).thenReturn(user);
        Mockito.when(sessionManagementService.getSessionForUser("user1")).thenReturn(session);

        EndGameEvent event = new EndGameEvent(session,"user1");
        userEventListener.onEndGameEvent(event);

        Mockito.verify(sessionManagementService).getSessionForUser("user1");
        mockedStatic.verify(() -> JsonDataUtility.sendEndGame(session,1), Mockito.times(1));
        mockedStatic.close();
    }
}
