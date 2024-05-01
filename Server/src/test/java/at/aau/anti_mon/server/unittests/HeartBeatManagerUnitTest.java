package at.aau.anti_mon.server.unittests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import at.aau.anti_mon.server.service.SessionManagementService;
import at.aau.anti_mon.server.websocket.manager.HeartBeatManager;

class HeartBeatManagerUnitTest {

    @Test
    void testSendHeartBeatMessage() throws IOException {
        // Arrange
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.isOpen()).thenReturn(true);
        when(session.getRemoteAddress()).thenReturn(null);
        when(session.getId()).thenReturn("test");
        SessionManagementService sessionManagementService = new SessionManagementService();
        sessionManagementService.registerUserWithSession("test", session);
        HeartBeatManager heartBeatManager = new HeartBeatManager(sessionManagementService);
        // Act
        heartBeatManager.sendHeartbeatToAllSessions();

        // Assert
        verify(session, times(1)).sendMessage(any(TextMessage.class));
    }
    @Test
    void testSendHeartBeatMessageIOException() throws IOException {
        // Arrange
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.isOpen()).thenReturn(true);
        when(session.getRemoteAddress()).thenReturn(null);
        when(session.getId()).thenReturn("test");
        doThrow(new IOException()).when(session).sendMessage(any(TextMessage.class));
        SessionManagementService sessionManagementService = new SessionManagementService();
        sessionManagementService.registerUserWithSession("test", session);
        HeartBeatManager heartBeatManager = new HeartBeatManager(sessionManagementService);

        // Act
        heartBeatManager.sendHeartbeatToAllSessions();

        // Assert
        verify(session, times(1)).sendMessage(any(TextMessage.class));
        // Add verification for Logger.error being called with the expected message
    }
}
