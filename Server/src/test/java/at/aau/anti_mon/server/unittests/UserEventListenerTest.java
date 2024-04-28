package at.aau.anti_mon.server.unittests;

import at.aau.anti_mon.server.dtos.LobbyDTO;
import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.server.events.UserLeftLobbyEvent;
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

import static org.mockito.Mockito.*;

@SpringBootTest
public class UserEventListenerTest {

    @Mock
    private SessionManagementService sessionManagementService;

    @Mock
    private UserService userService;

    @Mock
    private LobbyService lobbyService;

    @InjectMocks
    private UserEventListener userEventListener;



    @Test
    public void onUserJoinedLobbyEventShouldCallCorrectServiceMethod() throws UserNotFoundException, LobbyNotFoundException, LobbyIsFullException, URISyntaxException {


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

        UserJoinedLobbyEvent event = new UserJoinedLobbyEvent(session1, new LobbyDTO(1234), new UserDTO("testUser"));

        // When
        userEventListener.onUserJoinedLobbyEvent(event);



        // Then
        verify(lobbyService, times(1)).joinLobby(1234, "testUser");
    }

    @Test
    public void onLeaveLobbyEventShouldCallCorrectServiceMethod() throws UserNotFoundException, LobbyNotFoundException, URISyntaxException {
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


        UserLeftLobbyEvent event = new UserLeftLobbyEvent(session2, new LobbyDTO(1234), new UserDTO("lobbyCreator"));

        // When
        userEventListener.onLeaveLobbyEvent(event);

        // Then
        verify(lobbyService, times(1)).leaveLobby(1234, "lobbyCreator");
    }
}
