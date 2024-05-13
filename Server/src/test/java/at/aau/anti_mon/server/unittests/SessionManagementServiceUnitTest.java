package at.aau.anti_mon.server.unittests;

import at.aau.anti_mon.server.exceptions.SessionNotFoundException;
import at.aau.anti_mon.server.service.SessionManagementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionManagementServiceUnitTest {

    @Mock
    private WebSocketSession session;

    @InjectMocks
    private SessionManagementService sessionManagementService;

    @Test
    void registerUserWithSessionShouldRegisterUser() {
        when(session.getId()).thenReturn("123");
        sessionManagementService.registerUserWithSession("user1", session);
        assertEquals(session, sessionManagementService.getSession("123"));
        assertEquals(session, sessionManagementService.getSessionForUser("user1"));
    }

    @Test
    void registerUserWithSessionShouldThrowException() {
        assertThrows(IllegalArgumentException.class,
                () -> sessionManagementService.registerUserWithSession(null, session));
    }

    @Test
    void getSessionForUserShouldThrowException() {
        when(session.getId()).thenReturn("123");
        sessionManagementService.registerUserWithSession("user1", session);
        assertEquals(session, sessionManagementService.getSession("123"));
        assertThrows(SessionNotFoundException.class, () -> sessionManagementService.getSessionForUser("432"));
    }

    @Test
    void removeSessionByIdShouldRemoveSession() {
        when(session.getId()).thenReturn("123");
        sessionManagementService.registerUserWithSession("user1", session);
        assertEquals(session, sessionManagementService.getSession("123"));
        sessionManagementService.removeSessionById("123", "user1");
        assertNull(sessionManagementService.getSession("123"));
        assertThrows(SessionNotFoundException.class, () -> sessionManagementService.getSessionForUser("user1"));
    }

    @Test
    void getSessionForUserShouldThrowExceptionForNonexistentUser() {
        assertThrows(SessionNotFoundException.class,
                () -> sessionManagementService.getSessionForUser("nonexistentUser"));
    }

    @Test
    void getNumberOfSessionsShouldReturnCorrectNumber() {
        when(session.getId()).thenReturn("123");
        sessionManagementService.registerUserWithSession("user1", session);
        assertEquals(1, sessionManagementService.getNumberOfSessions());
    }
}