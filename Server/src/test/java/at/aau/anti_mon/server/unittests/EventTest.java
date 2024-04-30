package at.aau.anti_mon.server.unittests;

import at.aau.anti_mon.server.dtos.LobbyDTO;
import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.events.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.WebSocketSession;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class EventTest {

    @Mock
    WebSocketSession session;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void userJoinedLobbyEventShouldReturnCorrectValues() throws URISyntaxException {

        when(session.isOpen()).thenReturn(true);
        when(session.getRemoteAddress()).thenReturn(new InetSocketAddress(1234));
        when(session.getId()).thenReturn("session1");
        when(session.getAcceptedProtocol()).thenReturn("protocol");
        when(session.getHandshakeHeaders()).thenReturn(new HttpHeaders());
        when(session.getUri()).thenReturn(new URI("ws://localhost:8080/game?userID=user1"));

        when(session.getId()).thenReturn("session1");
        UserDTO userDTO = new UserDTO("user1");
        LobbyDTO lobbyDTO = new LobbyDTO(1234);
        UserJoinedLobbyEvent event = new UserJoinedLobbyEvent(session, lobbyDTO, userDTO);

        assertEquals("user1", event.getUsername());
        assertEquals("session1", event.getUserSessionID());
        assertEquals(1234, event.getPin());
    }

    @Test
    void userLeftLobbyEventShouldReturnCorrectValues() {
        when(session.getId()).thenReturn("session1");
        UserDTO userDTO = new UserDTO("user1");
        LobbyDTO lobbyDTO = new LobbyDTO(1234);
        UserLeftLobbyEvent event = new UserLeftLobbyEvent(session, lobbyDTO, userDTO);

        assertEquals("user1", event.getUsername());
        assertEquals("session1", event.getUserSessionID());
        assertEquals(1234, event.getPin());
    }

    @Test
    void userCreatedLobbyEventShouldReturnCorrectValues() {
        when(session.getId()).thenReturn("session1");
        UserDTO userDTO = new UserDTO("user1");
        UserCreatedLobbyEvent event = new UserCreatedLobbyEvent(session, userDTO);

        assertEquals("user1", event.getUsername());
        assertEquals("session1", event.getUserSessionID());
    }

    @Test
    void sessionConnectEventShouldReturnCorrectValues() {
        when(session.getId()).thenReturn("session1");
        SessionConnectEvent event = new SessionConnectEvent(session);

        assertEquals("session1", event.getSession().getId());
    }

    @Test
    void sessionDisconnectEventShouldReturnCorrectValues() {
        when(session.getId()).thenReturn("session1");
        SessionDisconnectEvent event = new SessionDisconnectEvent(session);

        assertEquals("session1", event.getSession().getId());
    }
}