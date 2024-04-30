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

class SessionManagementServiceTest {

    @Mock
    private WebSocketSession session;

    @InjectMocks
    private SessionManagementService sessionManagementService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        when(session.getId()).thenReturn("123");
    }

    @Test
    void registerUserWithSessionShouldRegisterUser() {
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
        sessionManagementService.registerUserWithSession("user1", session);
        assertEquals(session, sessionManagementService.getSession("123"));
        assertThrows(SessionNotFoundException.class, () -> sessionManagementService.getSessionForUser("432"));
    }

    @Test
    void removeSessionByIdShouldRemoveSession() {
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
        sessionManagementService.registerUserWithSession("user1", session);
        assertEquals(1, sessionManagementService.getNumberOfSessions());
    }
}