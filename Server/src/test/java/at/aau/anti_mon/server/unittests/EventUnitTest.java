package at.aau.anti_mon.server.unittests;

import at.aau.anti_mon.server.dtos.LobbyDTO;
import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.events.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventUnitTest {

    @Mock
    WebSocketSession session;

    @Test
    void userJoinedLobbyEventShouldReturnCorrectValues(){

        when(session.getId()).thenReturn("session1");

        when(session.getId()).thenReturn("session1");
        UserDTO userDTO = new UserDTO("user1", false, true, null, null);
        LobbyDTO lobbyDTO = new LobbyDTO(1234);
        UserJoinedLobbyEvent event = new UserJoinedLobbyEvent(session, lobbyDTO, userDTO);

        assertEquals("user1", event.getUsername());
        assertEquals("session1", event.getUserSessionID());
        assertEquals(1234, event.getPin());
    }

    @Test
    void userLeftLobbyEventShouldReturnCorrectValues() {
        when(session.getId()).thenReturn("session1");
        UserDTO userDTO = new UserDTO("user1", false, true, null, null);
        LobbyDTO lobbyDTO = new LobbyDTO(1234);
        UserLeftLobbyEvent event = new UserLeftLobbyEvent(session, lobbyDTO, userDTO);

        assertEquals("user1", event.getUsername());
        assertEquals("session1", event.getUserSessionID());
        assertEquals(1234, event.getPin());
    }

    @Test
    void userCreatedLobbyEventShouldReturnCorrectValues() {
        when(session.getId()).thenReturn("session1");
        UserDTO userDTO = new UserDTO("user1", false, true, null, null);
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