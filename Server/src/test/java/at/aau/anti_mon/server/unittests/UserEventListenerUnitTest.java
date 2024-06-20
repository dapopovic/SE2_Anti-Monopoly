package at.aau.anti_mon.server.unittests;

import at.aau.anti_mon.server.dtos.LobbyDTO;
import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.enums.Figures;
import at.aau.anti_mon.server.events.*;
import at.aau.anti_mon.server.exceptions.LobbyIsFullException;
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
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

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

    @Test
    void onUserJoinedLobbyEventShouldCallCorrectServiceMethod()
            throws UserNotFoundException, LobbyNotFoundException, LobbyIsFullException {

        // Given
        WebSocketSession session1 = mock(WebSocketSession.class);
        WebSocketSession session2 = mock(WebSocketSession.class);

        when(session1.isOpen()).thenReturn(true);
        when(session2.isOpen()).thenReturn(true);

        when(sessionManagementService.getSessionForUser("testUser")).thenReturn(session1);
        when(sessionManagementService.getSessionForUser("lobbyCreator")).thenReturn(session2);

        HashSet<User> users = new HashSet<>();
        User user1 = new User("lobbyCreator", session2);
        User user2 = new User("testUser", session1);
        users.add(user1);

        Lobby lobby = mock(Lobby.class);
        when(lobby.getUsers()).thenReturn(users);

        when(userService.findOrCreateUser("testUser", session1)).thenReturn(user2);
        when(lobbyService.findLobbyByPin(1234)).thenReturn(lobby);

        UserJoinedLobbyEvent event = new UserJoinedLobbyEvent(session1, new LobbyDTO(1234), new UserDTO("testUser", false, false, null, null));

        // When
        userEventListener.onUserJoinedLobbyEvent(event);

        // Then
        verify(lobbyService, times(1)).joinLobby(1234, "testUser");
    }

    @Test
    void onLeaveLobbyEventShouldCallCorrectServiceMethod()
            throws UserNotFoundException, LobbyNotFoundException {
        WebSocketSession session2 = mock(WebSocketSession.class);

        when(session2.isOpen()).thenReturn(true);
        when(sessionManagementService.getSessionForUser("lobbyCreator")).thenReturn(session2);

        HashSet<User> users = new HashSet<>();
        User user1 = new User("lobbyCreator", session2);
        users.add(user1);

        Lobby lobby = mock(Lobby.class);
        when(lobby.getUsers()).thenReturn(users);

        when(lobbyService.findLobbyByPin(1234)).thenReturn(lobby);

        UserLeftLobbyEvent event = new UserLeftLobbyEvent(session2, new LobbyDTO(1234), new UserDTO("lobbyCreator", false, true, null, null));

        // When
        userEventListener.onLeaveLobbyEvent(event);

        // Then
        verify(lobbyService, times(1)).leaveLobby(1234, "lobbyCreator");
    }

    @Test
    void onReadyUserEventShouldCallCorrectServiceMethod() throws UserNotFoundException, LobbyNotFoundException {
        // Given
        WebSocketSession session = mock(WebSocketSession.class);

        UserDTO userDTO = new UserDTO("user1", false, true, null, null);
        LobbyDTO lobbyDTO = new LobbyDTO(1234);
        UserReadyLobbyEvent event = new UserReadyLobbyEvent(session, lobbyDTO, userDTO);

        User user = mock(User.class);
        when(user.getName()).thenReturn("user1");
        when(user.isReady()).thenReturn(false);
        when(user.isReady()).thenReturn(false);

        Lobby m = mock(Lobby.class);
        when(m.getUsers()).thenReturn(new HashSet<>(List.of(user)));
        when(assertDoesNotThrow(() -> lobbyService.findLobbyByPin(1234))).thenReturn(m);
        when(userService.getUser("user1")).thenReturn(user);
        when(sessionManagementService.getSessionForUser("user1")).thenReturn(session);
        // When
        assertDoesNotThrow(() -> userEventListener.onReadyUserEvent(event));
        verify(lobbyService).readyUser(1234, "user1");
        verify(m).getUsers();
        verify(lobbyService).findLobbyByPin(1234);
        verify(userService).getUser("user1");
        verify(sessionManagementService).getSessionForUser("user1");
    }

    @Test
    void onStartGameEventShouldCallCorrectServiceMethod() throws LobbyNotFoundException {
        // Given
        WebSocketSession session = mock(WebSocketSession.class);

        UserDTO userDTO = new UserDTO("user1", false, true, null, null);
        LobbyDTO lobbyDTO = new LobbyDTO(1234);
        UserStartedGameEvent event = new UserStartedGameEvent(session, lobbyDTO, userDTO);

        User user = mock(User.class);
        when(user.getName()).thenReturn("user1");
        when(user.isReady()).thenReturn(false);
        when(user.isReady()).thenReturn(false);

        Lobby m = mock(Lobby.class);
        when(m.getUsers()).thenReturn(new HashSet<>(List.of(user)));
        when(assertDoesNotThrow(() -> lobbyService.findLobbyByPin(1234))).thenReturn(m);
        when(sessionManagementService.getSessionForUser("user1")).thenReturn(session);
        // When
        assertDoesNotThrow(() -> userEventListener.onStartGameEvent(event));
        verify(lobbyService).startGame(1234, "user1");
        verify(m).getUsers();
        verify(lobbyService).findLobbyByPin(1234);
        verify(sessionManagementService).getSessionForUser("user1");
    }

    @Test
    void onRollDiceEventShouldCallCorrectServiceMethod() throws UserNotFoundException {
        // Given
        WebSocketSession session = mock(WebSocketSession.class);

        DiceNumberEvent event = new DiceNumberEvent(session, "testuser", 5, true);

        Lobby lobby = mock(Lobby.class);

        User user = mock(User.class);
        when(user.getName()).thenReturn("testuser");
        when(user.getFigure()).thenReturn(Figures.GREEN_CIRCLE);
        when(user.getLocation()).thenReturn(0);
        when(user.getLobby()).thenReturn(lobby);
        HashSet<User> users = new HashSet<>();
        users.add(user);
        when(lobby.getUsers()).thenReturn(users);

        when(userService.getUser("testuser")).thenReturn(user);
        when(sessionManagementService.getSessionForUser("testuser")).thenReturn(session);

        // When
        assertDoesNotThrow(() -> userEventListener.onDiceNumberEvent(event));
        verify(userService).getUser("testuser");
        verify(sessionManagementService).getSessionForUser("testuser");
    }

    @Test
    void onChangeBalanceEventShouldCallCorrectServiceMethod() throws UserNotFoundException {
        WebSocketSession session = mock(WebSocketSession.class);
        ChangeBalanceEvent event = new ChangeBalanceEvent(session, "Julia", 1700);
        Lobby lobby = mock(Lobby.class);
        User user = mock(User.class);
        when(user.getName()).thenReturn("Julia");
        when(user.getLobby()).thenReturn(lobby);
        HashSet<User> users = new HashSet<>();
        users.add(user);
        when(lobby.getUsers()).thenReturn(users);
        when(userService.getUser("Julia")).thenReturn(user);
        when(sessionManagementService.getSessionForUser("Julia")).thenReturn(session);

        assertDoesNotThrow(() -> userEventListener.balanceChangedEvent(event));
        verify(userService).getUser("Julia");
        verify(sessionManagementService).getSessionForUser("Julia");
        verify(user).setMoney(1700);
    }

    @Test
    void testCheckCheatingCanNotCheat() {
        //Setup rng, server should not offer cheating despite good rng value(rngvalue>50)
        userEventListener.setFixProbabilityForCheating(65);

        //Setup mock class behavior
        MockedStatic<JsonDataUtility> mockedStatic = mockStatic(JsonDataUtility.class);
        WebSocketSession session = mock(WebSocketSession.class);
        User user = mock(User.class);
        //no method mocking, as neither session nor user should get called since cheating not allowed

        //Perform test
        userEventListener.checkCheating(false, user);

        //verify method calls
        verify(sessionManagementService, never()).getSessionForUser("Julia");
        mockedStatic.verify(() -> JsonDataUtility.sendCheating(session), times(0));
        mockedStatic.close();
    }

    @Test
    void testCheckCheatingCanCheatButNoOfferFromServer() {
        //Setup rng, server should not offer cheating (rngvalue<50)
        userEventListener.setFixProbabilityForCheating(45);

        //Setup mock class behavior
        MockedStatic<JsonDataUtility> mockedStatic = mockStatic(JsonDataUtility.class);
        WebSocketSession session = mock(WebSocketSession.class);
        User user = mock(User.class);
        //no method mocking, as neither session nor user should get called since cheating rng unlucky

        //Perform test
        userEventListener.checkCheating(true, user);

        //verify method calls
        verify(sessionManagementService, never()).getSessionForUser("Julia");
        mockedStatic.verify(() -> JsonDataUtility.sendCheating(session), times(0));
        mockedStatic.close();
    }

    @Test
    void testCheckCheatingCanCheatAndOfferFromServer() {
        //Setup rng, server should offer cheating (rngvalue<50)
        userEventListener.setFixProbabilityForCheating(65);

        //Setup mock class behavior
        MockedStatic<JsonDataUtility> mockedStatic = mockStatic(JsonDataUtility.class);

        WebSocketSession session = mock(WebSocketSession.class);

        User user = mock(User.class);
        when(user.getName()).thenReturn("Julia");

        when(sessionManagementService.getSessionForUser("Julia")).thenReturn(session);

        //Perform test
        userEventListener.checkCheating(true, user);

        //verify method calls
        verify(sessionManagementService).getSessionForUser("Julia");
        mockedStatic.verify(() -> JsonDataUtility.sendCheating(session), times(1));
        mockedStatic.close();
    }

    @Test
    void onFirstPlayerEventShouldCallCorrectServiceMethod() throws UserNotFoundException {
        WebSocketSession session = mock(WebSocketSession.class);
        FirstPlayerEvent event = new FirstPlayerEvent(session, "user0");
        Lobby lobby = mock(Lobby.class);
        User user = mock(User.class);
        when(user.getName()).thenReturn("user0");
        when(user.getLobby()).thenReturn(lobby);
        HashSet<User> users = new HashSet<>();
        users.add(user);
        when(lobby.getUsers()).thenReturn(users);
        when(userService.getUser("user0")).thenReturn(user);
        when(sessionManagementService.getSessionForUser("user0")).thenReturn(session);
        when(user.getSequence()).thenReturn(0);

        assertDoesNotThrow(() -> userEventListener.onFirstPlayerEvent(event));
        verify(userService).getUser("user0");
        verify(sessionManagementService).getSessionForUser("user0");
    }

    @Test
    void onNextPlayerEventShouldCallCorrectServiceMethod() throws UserNotFoundException {
        WebSocketSession session = mock(WebSocketSession.class);
        NextPlayerEvent event = new NextPlayerEvent(session, "user1");
        Lobby lobby = mock(Lobby.class);
        User user = mock(User.class);
        when(user.getName()).thenReturn("user1");
        when(user.getLobby()).thenReturn(lobby);
        when(user.getUnavailableRounds()).thenReturn(0);
        User user2 = mock(User.class);
        when(user2.getName()).thenReturn("user2");
        when(user2.getUnavailableRounds()).thenReturn(0);

        HashSet<User> users = new HashSet<>();
        users.add(user);
        users.add(user2);

        when(lobby.getUsers()).thenReturn(users);
        when(userService.getUser("user1")).thenReturn(user);
        when(sessionManagementService.getSessionForUser("user1")).thenReturn(session);
        when(sessionManagementService.getSessionForUser("user2")).thenReturn(session);
        when(user.getSequence()).thenReturn(1);

        assertDoesNotThrow(() -> userEventListener.onNextPlayerEvent(event));
        verify(userService).getUser("user1");
        verify(sessionManagementService).getSessionForUser("user1");
    }

    @Test
    void onLoseGameEventShouldCallCorrectServiceMethod() throws UserNotFoundException {
        WebSocketSession session = mock(WebSocketSession.class);
        LoseGameEvent event = new LoseGameEvent(session, "user1");
        Lobby lobby = mock(Lobby.class);
        User user = mock(User.class);
        when(user.getName()).thenReturn("user1");
        when(user.getLobby()).thenReturn(lobby);
        User user2 = mock(User.class);
        when(user2.getName()).thenReturn("user2");

        HashSet<User> users = new HashSet<>();
        users.add(user);
        users.add(user2);

        when(lobby.getUsers()).thenReturn(users);
        when(userService.getUser("user1")).thenReturn(user);
        when(sessionManagementService.getSessionForUser("user1")).thenReturn(session);
        when(sessionManagementService.getSessionForUser("user2")).thenReturn(session);

        assertDoesNotThrow(()-> userEventListener.onLoseGameEvent(event));
        verify(userService).getUser("user1");
        verify(sessionManagementService).getSessionForUser("user1");
    }

    @Test
    void onWinGameEventShouldCallCorrectServiceMethod() {
        MockedStatic<JsonDataUtility> mockedStatic = mockStatic(JsonDataUtility.class);

        WebSocketSession session = mock(WebSocketSession.class);

        User user = mock(User.class);
        when(user.getName()).thenReturn("user1");
        when(user.getUnavailableRounds()).thenReturn(0);

        HashSet<User> users = new HashSet<>();
        users.add(user);

        when(sessionManagementService.getSessionForUser("user1")).thenReturn(session);

        userEventListener.winGame(users);

        verify(sessionManagementService).getSessionForUser("user1");
        mockedStatic.verify(() -> JsonDataUtility.sendWinGame(session,"user1"), times(1));
        mockedStatic.close();
    }
    @Test
    void onWinGameEventButAllAvailableRoundsMinus1() {
        MockedStatic<JsonDataUtility> mockedStatic = mockStatic(JsonDataUtility.class);

        WebSocketSession session = mock(WebSocketSession.class);

        User user = mock(User.class);
        when(user.getUnavailableRounds()).thenReturn(-1);

        HashSet<User> users = new HashSet<>();
        users.add(user);

        userEventListener.winGame(users);

        verify(sessionManagementService, never()).getSessionForUser("user1");
        mockedStatic.verify(() -> JsonDataUtility.sendWinGame(session,"user1"),never());
        mockedStatic.close();
    }

    @Test
    void onNextPlayerEventAllPlayersPlayed() {
        HashSet<User> users = new HashSet<>();
        Lobby lobby = mock(Lobby.class);
        when(lobby.getUsers()).thenReturn(users);
        int sequence = 0;
        for (int i = 0; i < AMOUNT_PLAYERS; i++) {
            WebSocketSession session = mock(WebSocketSession.class);
            User user = mock(User.class);
            when(user.getName()).thenReturn("user" + i);
            when(user.getSequence()).thenReturn(sequence);
            when(user.isHasPlayed()).thenReturn(true);
            when(sessionManagementService.getSessionForUser("user" + i)).thenReturn(session);
            users.add(user);
            sequence++;
        }
        ArrayList<User> userList = new ArrayList<>(users);
        userList.sort(Comparator.comparing(User::getSequence));
        NextPlayerEvent event = new NextPlayerEvent(mock(WebSocketSession.class), "user0");
        User user = userList.get(0);
        when(user.getLobby()).thenReturn(lobby);
        when(assertDoesNotThrow(() -> userService.getUser("user0"))).thenReturn(user);
        assertDoesNotThrow(() -> userEventListener.onNextPlayerEvent(event));
        verify(userList.get(0), times(3 + AMOUNT_PLAYERS)).getName();
    }
    @Test
    void onNextPlayerEventAllPlayersPlayedButOneHasUnavailableRounds() {
        HashSet<User> users = new HashSet<>();
        Lobby lobby = mock(Lobby.class);
        when(lobby.getUsers()).thenReturn(users);
        int sequence = 0;
        for (int i = 0; i < AMOUNT_PLAYERS; i++) {
            WebSocketSession session = mock(WebSocketSession.class);
            User user = mock(User.class);
            when(user.getName()).thenReturn("user" + i);
            when(user.getSequence()).thenReturn(sequence);
            when(user.isHasPlayed()).thenReturn(true);
            if (i == 0) {
                when(user.getUnavailableRounds()).thenReturn(2);
            }
            when(sessionManagementService.getSessionForUser("user" + i)).thenReturn(session);
            users.add(user);
            sequence++;
        }
        ArrayList<User> userList = new ArrayList<>(users);
        userList.sort(Comparator.comparing(User::getSequence));
        NextPlayerEvent event = new NextPlayerEvent(mock(WebSocketSession.class), "user0");
        User user = userList.get(0);
        when(user.getLobby()).thenReturn(lobby);
        when(assertDoesNotThrow(() -> userService.getUser("user0"))).thenReturn(user);
        assertDoesNotThrow(() -> userEventListener.onNextPlayerEvent(event));
        verify(userList.get(1), times(3 + AMOUNT_PLAYERS)).getName();
    }
    @ParameterizedTest
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

    @ParameterizedTest
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

    @ParameterizedTest
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

    @Test
    void onGetPlayerOneUserAvailableRoundsGreaterThan0OnlyOneUser() {
        HashSet<User> users = new HashSet<>();
        Lobby lobby = mock(Lobby.class);
        when(lobby.getUsers()).thenReturn(users);
        WebSocketSession session = mock(WebSocketSession.class);
        User user = mock(User.class);
        when(user.getName()).thenReturn("user0");
        when(user.getUnavailableRounds()).thenReturn(2);
        when(sessionManagementService.getSessionForUser("user0")).thenReturn(session);
        users.add(user);

        NextPlayerEvent event = new NextPlayerEvent(mock(WebSocketSession.class), "user0");
        user = users.iterator().next();
        when(user.getLobby()).thenReturn(lobby);
        when(assertDoesNotThrow(() -> userService.getUser("user0"))).thenReturn(user);
        assertDoesNotThrow(() -> userEventListener.onNextPlayerEvent(event));
        verify(user, times(2)).getName();
    }
}
