package at.aau.anti_mon.server.unittests;

import at.aau.anti_mon.server.dtos.LobbyDTO;
import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.server.events.UserLeftLobbyEvent;
import at.aau.anti_mon.server.events.UserReadyLobbyEvent;
import at.aau.anti_mon.server.events.UserStartedGameEvent;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserEventListenerTest {

    @Mock
    private SessionManagementService sessionManagementService;

    @Mock
    private UserService userService;

    @Mock
    private LobbyService lobbyService;

    @InjectMocks
    private UserEventListener userEventListener;

    @Test
    void onUserJoinedLobbyEventShouldCallCorrectServiceMethod()
            throws UserNotFoundException, LobbyNotFoundException, LobbyIsFullException, URISyntaxException {

        // Given
        WebSocketSession session1 = mock(WebSocketSession.class);
        WebSocketSession session2 = mock(WebSocketSession.class);

        when(session1.isOpen()).thenReturn(true);
        when(session1.getRemoteAddress()).thenReturn(new InetSocketAddress(1234));
        when(session1.getId()).thenReturn("session1");
        when(session1.getAcceptedProtocol()).thenReturn("protocol");
        when(session1.getHandshakeHeaders()).thenReturn(new HttpHeaders());
        when(session1.getUri()).thenReturn(new URI("ws://localhost:8080/game?userID=user1"));

        when(session2.isOpen()).thenReturn(true);
        when(session2.getRemoteAddress()).thenReturn(new InetSocketAddress(1234));
        when(session2.getId()).thenReturn("session2");
        when(session2.getAcceptedProtocol()).thenReturn("protocol");
        when(session2.getHandshakeHeaders()).thenReturn(new HttpHeaders());
        when(session2.getUri()).thenReturn(new URI("ws://localhost:8080/game?userID=user2"));

        when(sessionManagementService.getSessionForUser("testUser")).thenReturn(session1);
        when(sessionManagementService.getSessionForUser("lobbyCreator")).thenReturn(session2);

        HashSet<User> users = new HashSet<>();
        User user1 = new User("lobbyCreator", session2);
        User user2 = new User("testUser", session1);
        users.add(user1);

        Lobby lobby = mock(Lobby.class);
        when(lobby.canAddPlayer()).thenReturn(true);
        when(lobby.getPin()).thenReturn(1234);
        when(lobby.getUsers()).thenReturn(users);

        when(userService.findOrCreateUser("testUser", session1)).thenReturn(user2);
        when(lobbyService.findLobbyByPin(1234)).thenReturn(lobby);

        UserJoinedLobbyEvent event = new UserJoinedLobbyEvent(session1, new LobbyDTO(1234), new UserDTO("testUser", false, false));

        // When
        userEventListener.onUserJoinedLobbyEvent(event);

        // Then
        verify(lobbyService, times(1)).joinLobby(1234, "testUser");
    }

    @Test
    void onUserJoinedLobbyEventLobbyIsFull()
            throws UserNotFoundException, LobbyNotFoundException, LobbyIsFullException, URISyntaxException {
        HashSet<User> users = new HashSet<>();
        for (int i = 0; i < 6; i++) {
            WebSocketSession session = mock(WebSocketSession.class);
            when(session.isOpen()).thenReturn(true);
            when(session.getRemoteAddress()).thenReturn(new InetSocketAddress(1234));
            when(session.getId()).thenReturn("session" + i);
            when(session.getAcceptedProtocol()).thenReturn("protocol");
            when(session.getHandshakeHeaders()).thenReturn(new HttpHeaders());
            when(session.getUri()).thenReturn(new URI("ws://localhost:8080/game?userID=user" + i));
            users.add(new User("user" + i, session));
        }

        Lobby lobby = mock(Lobby.class);
        when(lobby.getPin()).thenReturn(1234);
        when(lobby.getUsers()).thenReturn(users);


        when(lobbyService.findLobbyByPin(1234)).thenReturn(lobby);

        WebSocketSession session = mock(WebSocketSession.class);
        when(session.isOpen()).thenReturn(true);
        when(session.getRemoteAddress()).thenReturn(new InetSocketAddress(1234));
        when(session.getId()).thenReturn("session7");
        when(session.getAcceptedProtocol()).thenReturn("protocol");
        when(session.getHandshakeHeaders()).thenReturn(new HttpHeaders());
        when(session.getUri()).thenReturn(new URI("ws://localhost:8080/game?userID=user7"));

        when(sessionManagementService.getSessionForUser("user7")).thenReturn(session);

        User user7 = new User("user7", session);
        when(userService.findOrCreateUser("user7", session)).thenReturn(user7);
        doThrow(new LobbyIsFullException("Lobby is full. Cannot add more players.")).when(lobbyService).joinLobby(1234, "user7");

        UserJoinedLobbyEvent event = new UserJoinedLobbyEvent(session, new LobbyDTO(1234), new UserDTO("user7", false, false));
        assertThrows(LobbyIsFullException.class, () -> userEventListener.onUserJoinedLobbyEvent(event));
        // Then
        verify(lobbyService).joinLobby(1234, "user7");
    }

    @Test
    void onLeaveLobbyEventShouldCallCorrectServiceMethod()
            throws UserNotFoundException, LobbyNotFoundException, URISyntaxException {
        WebSocketSession session1 = mock(WebSocketSession.class);
        WebSocketSession session2 = mock(WebSocketSession.class);

        when(session1.isOpen()).thenReturn(true);
        when(session1.getRemoteAddress()).thenReturn(new InetSocketAddress(1234));
        when(session1.getId()).thenReturn("session1");
        when(session1.getAcceptedProtocol()).thenReturn("protocol");
        when(session1.getHandshakeHeaders()).thenReturn(new HttpHeaders());
        when(session1.getUri()).thenReturn(new URI("ws://localhost:8080/game?userID=user1"));

        when(session2.isOpen()).thenReturn(true);
        when(session2.getRemoteAddress()).thenReturn(new InetSocketAddress(1234));
        when(session2.getId()).thenReturn("session2");
        when(session2.getAcceptedProtocol()).thenReturn("protocol");
        when(session2.getHandshakeHeaders()).thenReturn(new HttpHeaders());
        when(session2.getUri()).thenReturn(new URI("ws://localhost:8080/game?userID=user2"));

        when(sessionManagementService.getSessionForUser("testUser")).thenReturn(session1);
        when(sessionManagementService.getSessionForUser("lobbyCreator")).thenReturn(session2);

        HashSet<User> users = new HashSet<>();
        User user1 = new User("lobbyCreator", session2);
        User user2 = new User("testUser", session1);
        users.add(user1);

        Lobby lobby = mock(Lobby.class);
        when(lobby.canAddPlayer()).thenReturn(true);
        when(lobby.getPin()).thenReturn(1234);
        when(lobby.getUsers()).thenReturn(users);

        when(userService.findOrCreateUser("testUser", session1)).thenReturn(user2);
        when(lobbyService.findLobbyByPin(1234)).thenReturn(lobby);

        UserLeftLobbyEvent event = new UserLeftLobbyEvent(session2, new LobbyDTO(1234), new UserDTO("lobbyCreator", false, true));

        // When
        userEventListener.onLeaveLobbyEvent(event);

        // Then
        verify(lobbyService, times(1)).leaveLobby(1234, "lobbyCreator");
    }
    @Test
    void onReadyUserEventShouldCallCorrectServiceMethod() throws UserNotFoundException, LobbyNotFoundException {
        // Given
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("session1");
        when(session.getAcceptedProtocol()).thenReturn("protocol");
        when(session.getHandshakeHeaders()).thenReturn(new HttpHeaders());
        when(session.getUri()).thenReturn(URI.create("ws://localhost:8080/game?userID=user1"));

        UserDTO userDTO = new UserDTO("user1", false, true);
        LobbyDTO lobbyDTO = new LobbyDTO(1234);
        UserReadyLobbyEvent event = new UserReadyLobbyEvent(session, lobbyDTO, userDTO);

        User user = mock(User.class);
        when(user.getName()).thenReturn("user1");
        when(user.isReady()).thenReturn(false);
        when(user.getSession()).thenReturn(session);
        when(user.isReady()).thenReturn(false);

        Lobby m = mock(Lobby.class);
        when(m.getPin()).thenReturn(1234);
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
        when(session.getId()).thenReturn("session1");
        when(session.getAcceptedProtocol()).thenReturn("protocol");
        when(session.getHandshakeHeaders()).thenReturn(new HttpHeaders());
        when(session.getUri()).thenReturn(URI.create("ws://localhost:8080/game?userID=user1"));

        UserDTO userDTO = new UserDTO("user1", false, true);
        LobbyDTO lobbyDTO = new LobbyDTO(1234);
        UserStartedGameEvent event = new UserStartedGameEvent(session, lobbyDTO, userDTO);

        User user = mock(User.class);
        when(user.getName()).thenReturn("user1");
        when(user.isReady()).thenReturn(false);
        when(user.getSession()).thenReturn(session);
        when(user.isReady()).thenReturn(false);

        Lobby m = mock(Lobby.class);
        when(m.getPin()).thenReturn(1234);
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
}
