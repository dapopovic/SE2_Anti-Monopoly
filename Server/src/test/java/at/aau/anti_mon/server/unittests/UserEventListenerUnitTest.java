package at.aau.anti_mon.server.unittests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;
import java.util.*;

import at.aau.anti_mon.server.enums.Figures;
import at.aau.anti_mon.server.events.*;
import org.h2.util.json.JSONItemType;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import at.aau.anti_mon.server.dtos.LobbyDTO;
import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.exceptions.LobbyIsFullException;
import at.aau.anti_mon.server.exceptions.LobbyNotFoundException;
import at.aau.anti_mon.server.exceptions.UserNotFoundException;
import at.aau.anti_mon.server.game.Lobby;
import at.aau.anti_mon.server.game.User;
import at.aau.anti_mon.server.listener.UserEventListener;
import at.aau.anti_mon.server.service.LobbyService;
import at.aau.anti_mon.server.service.SessionManagementService;
import at.aau.anti_mon.server.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.WebSocketSession;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.stream.Collectors;

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

    /*
    @Test
    void onUserJoinedLobbyEventLobbyIsFull()
            throws UserNotFoundException, LobbyNotFoundException, LobbyIsFullException, URISyntaxException {

        // Given
        WebSocketSession session1 = mock(WebSocketSession.class);
        WebSocketSession session2 = mock(WebSocketSession.class);

        when(session1.isOpen()).thenReturn(true);

        when(sessionManagementService.getSessionForUser("testUser")).thenReturn(session1);
        HashSet<User> users = new HashSet<>();
        User user1 = new User("lobbyCreator", session2);
        User user2 = new User("testUser", session1);
        users.add(user1);

        Lobby lobby = mock(Lobby.class);
        when(lobby.canAddPlayer()).thenReturn(false);
        when(userService.findOrCreateUser("testUser", session1)).thenReturn(user2);
        when(lobbyService.findLobbyByPin(1234)).thenReturn(lobby);

        UserJoinedLobbyEvent event = new UserJoinedLobbyEvent(session1, new LobbyDTO(1234), new UserDTO("testUser", false, false));

        userEventListener.onUserJoinedLobbyEvent(event);
        verify(lobbyService, times(0)).joinLobby(1234, "testUser");
    }

     */

    @Test
    void onLeaveLobbyEventShouldCallCorrectServiceMethod()
            throws UserNotFoundException, LobbyNotFoundException {
        //WebSocketSession session1 = mock(WebSocketSession.class);
        WebSocketSession session2 = mock(WebSocketSession.class);

        when(session2.isOpen()).thenReturn(true);
        when(sessionManagementService.getSessionForUser("lobbyCreator")).thenReturn(session2);

        HashSet<User> users = new HashSet<>();
        User user1 = new User("lobbyCreator", session2);
        //User user2 = new User("testUser", session1);
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
    void onStartGameEventShouldCallCorrectServiceMethod() throws LobbyNotFoundException, UserNotFoundException {
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
    void onRollDiceEventShouldCallCorrectServiceMethod() throws LobbyNotFoundException, UserNotFoundException {
        // Given
        WebSocketSession session = mock(WebSocketSession.class);

        DiceNumberEvent event = new DiceNumberEvent(session, "testuser", 5);

        Lobby lobby = mock(Lobby.class);

        User user = mock(User.class);
        when(user.getName()).thenReturn("testuser");
        when(user.getFigure()).thenReturn(Figures.GreenCircle);
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
    void onFirstPlayerEventShouldCallCorrectServiceMethod() throws UserNotFoundException {
        WebSocketSession session = mock(WebSocketSession.class);
        FirstPlayerEvent event = new FirstPlayerEvent(session, "user1");
        Lobby lobby = mock(Lobby.class);
        User user = mock(User.class);
        when(user.getName()).thenReturn("user1");
        when(user.getLobby()).thenReturn(lobby);
        HashSet<User> users = new HashSet<>();
        users.add(user);
        when(lobby.getUsers()).thenReturn(users);
        when(userService.getUser("user1")).thenReturn(user);
        when(sessionManagementService.getSessionForUser("user1")).thenReturn(session);
        when(user.getSequence()).thenReturn(1);

        assertDoesNotThrow(() -> userEventListener.onFirstPlayerEvent(event));
        verify(userService).getUser("user1");
        verify(sessionManagementService).getSessionForUser("user1");
    }

    @Test
    void onNextPlayerEventShouldCallCorrectServiceMethod() throws UserNotFoundException {
        WebSocketSession session = mock(WebSocketSession.class);
        NextPlayerEvent event = new NextPlayerEvent(session, "user1");
        Lobby lobby = mock(Lobby.class);
        User user = mock(User.class);
        when(user.getName()).thenReturn("user1");
        when(user.getLobby()).thenReturn(lobby);
        HashSet<User> users = new HashSet<>();
        users.add(user);
        when(lobby.getUsers()).thenReturn(users);
        when(userService.getUser("user1")).thenReturn(user);
        when(sessionManagementService.getSessionForUser("user1")).thenReturn(session);
        when(user.getSequence()).thenReturn(1);

        assertDoesNotThrow(() -> userEventListener.onNextPlayerEvent(event));
        verify(userService).getUser("user1");
        verify(sessionManagementService).getSessionForUser("user1");
    }

    @RepeatedTest(100)
    void onGetPlayerAllUsersAvailableRoundsGreaterThan0() {
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
        NextPlayerEvent event = new NextPlayerEvent(mock(WebSocketSession.class), "user1");
        User user1 = users.stream().filter(user -> user.getName().equals("user1")).toList().get(0);
        when(user1.getLobby()).thenReturn(lobby);
        when(assertDoesNotThrow(() -> userService.getUser("user1"))).thenReturn(user1);
        assertDoesNotThrow(() -> userEventListener.onNextPlayerEvent(event));
        verify(users.iterator().next(), times(3 + AMOUNT_PLAYERS)).getName();
    }

    @RepeatedTest(100)
    void onGetPlayerOneUserAvailableRoundsGreaterThan0() {
        HashSet<User> users = new HashSet<>();
        Lobby lobby = mock(Lobby.class);
        when(lobby.getUsers()).thenReturn(users);
        int sequence = 0;
        int randomizedUser = new Random().nextInt(0, 4);
        System.out.println(randomizedUser);
        for (int i = 0; i < AMOUNT_PLAYERS; i++) {
            WebSocketSession session = mock(WebSocketSession.class);
            User user = mock(User.class);
            when(user.getName()).thenReturn("user" + i);
            when(user.getSequence()).thenReturn(sequence);
            when(user.getUnavailableRounds()).thenReturn(i == randomizedUser ? 2 : 0);
            when(sessionManagementService.getSessionForUser("user" + i)).thenReturn(session);
            users.add(user);
            sequence++;
        }
        NextPlayerEvent event = new NextPlayerEvent(mock(WebSocketSession.class), "user0");
        User user1 = users.stream().filter(user -> user.getName().equals("user0")).toList().get(0);
        when(user1.getLobby()).thenReturn(lobby);
        when(assertDoesNotThrow(() -> userService.getUser("user0"))).thenReturn(user1);
        assertDoesNotThrow(() -> userEventListener.onNextPlayerEvent(event));
        ArrayList<User> userList = new ArrayList<>(users);
        userList.sort(Comparator.comparing(User::getSequence));
        int nextUser = randomizedUser == 1 ? 2 : 1;
        verify(userList.get(nextUser), times(3 + AMOUNT_PLAYERS)).getName();
    }
//    @RepeatedTest(100)
//    void onGetPlayerAllUsersUntilRandomizedUserAvailableRoundsGreaterThan0() {
//        HashSet<User> users = new HashSet<>();
//        Lobby lobby = mock(Lobby.class);
//        when(lobby.getUsers()).thenReturn(users);
//        int sequence = 0;
//        int randomizedUser = new Random().nextInt(0, 4);
//        System.out.println(randomizedUser);
//        for (int i = 0; i < AMOUNT_PLAYERS; i++) {
//            WebSocketSession session = mock(WebSocketSession.class);
//            User user = mock(User.class);
//            when(user.getName()).thenReturn("user" + i);
//            when(user.getSequence()).thenReturn(sequence);
//            when(user.getUnavailableRounds()).thenReturn(i <= randomizedUser ? 2 : 0);
//            when(sessionManagementService.getSessionForUser("user" + i)).thenReturn(session);
//            users.add(user);
//            sequence++;
//        }
//        sequence = 0;
//        NextPlayerEvent event = new NextPlayerEvent(mock(WebSocketSession.class), "user0");
//        User user1 = users.stream().filter(user -> user.getName().equals("user0")).toList().get(0);
//        when(user1.getLobby()).thenReturn(lobby);
//        when(assertDoesNotThrow(() -> userService.getUser("user0"))).thenReturn(user1);
//        assertDoesNotThrow(() -> userEventListener.onNextPlayerEvent(event));
//        ArrayList<User> userList = new ArrayList<>(users);
//        userList.sort(Comparator.comparing(User::getSequence));
//        int nextUser = 0;
//        if(randomizedUser == sequence) {
//            nextUser = 1;
//        } else {
//            for (int i = 0; i < userList.size(); i++) {
//                if (userList.get(i).getUnavailableRounds() == 0) {
//                    nextUser = i;
//                    break;
//                }
//            }
//        }
//        System.out.println("Next User: " + userList.get(nextUser).getName());
//        verify(userList.get(nextUser), times(4 + AMOUNT_PLAYERS)).getName();
//    }

//    @RepeatedTest(100)
//    void onGetPlayerAllUsersUntilRandomizedUserAvailableRoundsGreaterThan0() {
//        HashSet<User> users = new HashSet<>();
//        int sequence = 0;
//        int randomizedUser = new Random().nextInt(0, 4);
//        System.out.println(randomizedUser);
//        for (int i = 0; i < 4; i++) {
//            WebSocketSession session = mock(WebSocketSession.class);
//            User user = new User("user" + i, session);
//            user.setUnavailableRounds(0);
//            if (i <= randomizedUser) {
//                user.setUnavailableRounds(2);
//            }
//            user.setSequence(sequence);
//            users.add(user);
//            sequence++;
//        }
//        sequence = 0;
//        User currentUser = userEventListener.getNextPlayer(users, sequence, 4);
//        ArrayList<User> userList = new ArrayList<>(users);
//        userList.sort(Comparator.comparing(User::getSequence));
//        int nextUser = 0;
//        if (randomizedUser == sequence) {
//            nextUser = 1;
//        } else {
//            for (int i = 0; i < userList.size(); i++) {
//                if (userList.get(i).getUnavailableRounds() == 0) {
//                    nextUser = i;
//                    break;
//                }
//            }
//        }
//        assertEquals(userList.get(nextUser), currentUser);
//    }

//    @RepeatedTest(100)
//    void onGetPlayerAllUsersUntilRandomizedUserAvailableRoundsGreaterThan0WithRandomSequence() {
//        HashSet<User> users = new HashSet<>();
//        int sequence = 0;
//        int randomizedUser = new Random().nextInt(0, 4);
//        System.out.println(randomizedUser);
//        for (int i = 0; i < 4; i++) {
//            WebSocketSession session = mock(WebSocketSession.class);
//            User user = new User("user" + i, session);
//            user.setUnavailableRounds(0);
//            if (i <= randomizedUser) {
//                user.setUnavailableRounds(2);
//            }
//            user.setSequence(sequence);
//            users.add(user);
//            sequence++;
//        }
//        sequence = new Random().nextInt(0, 4);
//        User currentUser = userEventListener.getNextPlayer(users, sequence, 4);
//        ArrayList<User> userList = new ArrayList<>(users);
//        userList.sort(Comparator.comparing(User::getSequence));
//        int nextUser = 0;
//        if ((randomizedUser == 0 && sequence != 0) || sequence > randomizedUser) {
//            nextUser = sequence;
//        } else {
//            for (int i = 0; i < userList.size(); i++) {
//                if (userList.get(i).getUnavailableRounds() == 0) {
//                    nextUser = i;
//                    break;
//                }
//            }
//        }
//        System.out.println("Next User: " + userList.get(nextUser).getName());
//        System.out.println("Current User: " + currentUser.getName());
//        assertEquals(userList.get(nextUser), currentUser);
//    }

}
