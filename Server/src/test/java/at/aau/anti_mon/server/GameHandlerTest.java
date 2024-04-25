package at.aau.anti_mon.server;


import at.aau.anti_mon.server.commands.Command;
import at.aau.anti_mon.server.commands.CommandFactory;
import at.aau.anti_mon.server.websocket.handler.GameHandler;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GameHandlerTest {

    @Mock
    private WebSocketSession session;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private CommandFactory gameCommandFactory;
    @InjectMocks
    private GameHandler gameHandler;

    @Test
    public void testHandleMessageValidCommand() throws Exception {
        // Vorbereitung
        String json = "{\"command\":{\"command\":\"CREATE_GAME\"}, \"data\":{}}";
        WebSocketMessage<?> message = new TextMessage(json);
        Command mockCommand = Mockito.mock(Command.class);
        when(gameCommandFactory.getCommand("CREATE_GAME")).thenReturn(mockCommand);

        // Ausführung
        gameHandler.handleMessage(session, message);

        // Überprüfung
        verify(mockCommand, times(1)).execute(any(), any());
    }

    @Test
    public void testHandleMessageInvalidCommand() {
        // Vorbereitung
        String json = "{\"command\":{\"command\":\"INVALID_COMMAND\"}, \"data\":{}}";
        WebSocketMessage<?> message = new TextMessage(json);
        when(gameCommandFactory.getCommand("INVALID_COMMAND")).thenReturn(null);

        // Ausführung
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gameHandler.handleMessage(session, message);
        });

        // Überprüfung
        assertEquals("Unbekannter oder nicht unterstützter Befehl: INVALID_COMMAND", exception.getMessage());
    }
}