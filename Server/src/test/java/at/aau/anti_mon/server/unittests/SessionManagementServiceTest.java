package at.aau.anti_mon.server.unittests;

import at.aau.anti_mon.server.exceptions.SessionNotFoundException;
import at.aau.anti_mon.server.service.SessionManagementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SessionManagementServiceTest {

    @Mock
    private WebSocketSession session;

    @InjectMocks
    private SessionManagementService sessionManagementService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(session.getId()).thenReturn("123");
    }

    @Test
    public void registerUserWithSessionShouldRegisterUser() {
        sessionManagementService.registerUserWithSession("user1", session);
        assertEquals(session, sessionManagementService.getSession("123"));
        assertEquals(session, sessionManagementService.getSessionForUser("user1"));
    }

    @Test
    public void registerUserWithSessionShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> sessionManagementService.registerUserWithSession(null, session));
    }

    @Test
    public void getSessionForUserShouldThrowException() {
        sessionManagementService.registerUserWithSession("user1", session);
        assertEquals(session, sessionManagementService.getSession("123"));
        assertThrows(SessionNotFoundException.class, () -> sessionManagementService.getSessionForUser("432"));
    }

    @Test
    public void removeSessionByIdShouldRemoveSession() {
        sessionManagementService.registerUserWithSession("user1", session);
        assertEquals(session, sessionManagementService.getSession("123"));
        sessionManagementService.removeSessionById("123", "user1");
        assertNull(sessionManagementService.getSession("123"));
        assertThrows(SessionNotFoundException.class, () -> sessionManagementService.getSessionForUser("user1"));
    }

    @Test
    public void getSessionForUserShouldThrowExceptionForNonexistentUser() {
        assertThrows(SessionNotFoundException.class, () -> sessionManagementService.getSessionForUser("nonexistentUser"));
    }

    @Test
    public void getNumberOfSessionsShouldReturnCorrectNumber() {
        sessionManagementService.registerUserWithSession("user1", session);
        assertEquals(1, sessionManagementService.getNumberOfSessions());
    }
}