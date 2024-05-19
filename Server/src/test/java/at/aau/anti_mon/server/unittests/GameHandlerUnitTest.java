package at.aau.anti_mon.server.unittests;

import at.aau.anti_mon.server.commands.CommandFactory;
import at.aau.anti_mon.server.commands.CreateGameCommand;
import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.events.UserCreatedLobbyEvent;
import at.aau.anti_mon.server.dtos.JsonDataDTO;
import at.aau.anti_mon.server.listener.UserEventListener;
import at.aau.anti_mon.server.websocket.handler.GameHandler;
import at.aau.anti_mon.server.events.SessionConnectEvent;
import at.aau.anti_mon.server.events.SessionDisconnectEvent;
import at.aau.anti_mon.server.utilities.JsonDataUtility;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Unit-Test class for GameHandler
 */
class GameHandlerUnitTest {

    @Test
    void testHandleMessageInvalidCommand() throws URISyntaxException, IOException {

        // Vorbereitung

        try (WebSocketSession session = mock(WebSocketSession.class)) {
            CommandFactory gameCommandFactory = mock(CommandFactory.class);

            ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
            GameHandler gameHandler = new GameHandler(eventPublisher);
            String json = "{\"command\":\"INVALID_COMMAND\",\"data\":{\"err\":\"Test\"}}";
            when(gameCommandFactory.getCommand("INVALID_COMMAND")).thenReturn(null);
            WebSocketMessage<?> message = new TextMessage(json);

            when(session.isOpen()).thenReturn(true);
            when(session.getRemoteAddress()).thenReturn(new InetSocketAddress(1234));
            when(session.getId()).thenReturn("session1");
            when(session.getAcceptedProtocol()).thenReturn("protocol");
            when(session.getHandshakeHeaders()).thenReturn(new HttpHeaders());
            when(session.getUri()).thenReturn(new URI("ws://localhost:8080/game?userID=Test"));

            // Teste Json-Serialisierung und Exception-Handling
            assertThrows(JsonProcessingException.class, () -> gameHandler.handleMessage(session, message));
        }
    }

    // fixme in general you would extract setup methods here. you will learn this in a master course, but we can discuss it next time if youre interested
    @Test
    void handleMessageShouldExecuteCommandAndSendAnswer() throws Exception {

        WebSocketSession session = mock(WebSocketSession.class);
        when(session.isOpen()).thenReturn(true);
        when(session.getRemoteAddress()).thenReturn(new InetSocketAddress(1234));
        when(session.getId()).thenReturn("session1");
        when(session.getAcceptedProtocol()).thenReturn("protocol");
        when(session.getHandshakeHeaders()).thenReturn(new HttpHeaders());
        when(session.getUri()).thenReturn(new URI("ws://localhost:8080/game?userID=Test"));

        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        CreateGameCommand createGameCommand = mock(CreateGameCommand.class);

        // Angenommen, dass dies auch ein Mock sein kann
        CommandFactory gameCommandFactory = mock(CommandFactory.class);
        doCallRealMethod().when(gameCommandFactory).getCommand(Commands.CREATE_GAME.name());

        UserEventListener userEventListener = mock(UserEventListener.class);
        // UserCreatedLobbyEvent createLobbyEvent = new UserCreatedLobbyEvent(session,
        // new UserDTO("Test"));

        // GAME HANDLER:
        GameHandler gameHandler = new GameHandler(eventPublisher);

        // Erstellen der Testdaten:
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.CREATE_GAME);
        jsonDataDTO.putData("username", "Test");
        String jsonMessage = JsonDataUtility.createStringFromJsonMessage(jsonDataDTO);
        TextMessage message;
        if (jsonMessage != null) {
            message = new TextMessage(jsonMessage);
        } else {
            throw new JsonProcessingException("Error while creating JSON message") {
            };
        }

        // Konfigurieren des Command, um die Interaction weiterzuleiten
        doAnswer(invocation -> {
            ApplicationEventPublisher publisher = invocation.getArgument(0, ApplicationEventPublisher.class);
            UserCreatedLobbyEvent event = new UserCreatedLobbyEvent(session, new UserDTO("Test", true, true, null, null));
            publisher.publishEvent(event);
            return null;
        }).when(createGameCommand).execute(any(), any());

        // Konfigurieren des eventPublisher, um JsonDataManager.sendPin aufzurufen
        doAnswer(invocation -> {
            // UserCreatedLobbyEvent event = invocation.getArgument(0);
            JsonDataUtility.sendPin(session, "1234");
            return null;
        }).when(eventPublisher).publishEvent(any(UserCreatedLobbyEvent.class));

        // When onCreateLobbyEvent() is called, trigger sendPin()
        doAnswer((Answer<Void>) invocation -> {
            UserCreatedLobbyEvent event = invocation.getArgument(0);
            JsonDataUtility.sendPin(event.getSession(), "1234");
            return null;
        }).when(userEventListener).onCreateLobbyEvent(any(UserCreatedLobbyEvent.class));

        // Ausführen der Methode, die getestet wird
        gameHandler.handleMessage(session, message);

        // Ausführung des Tests
        verify(session, times(1)).sendMessage(any());
    }

    @Test
    void handleMessageShouldThrowErrorBecauseWrongJsonData() throws URISyntaxException {
        try (WebSocketSession session = mock(WebSocketSession.class)) {
            when(session.isOpen()).thenReturn(true);
            when(session.getRemoteAddress()).thenReturn(new InetSocketAddress(1234));
            when(session.getId()).thenReturn("session1");
            when(session.getAcceptedProtocol()).thenReturn("protocol");
            when(session.getHandshakeHeaders()).thenReturn(new HttpHeaders());
            when(session.getUri()).thenReturn(new URI("ws://localhost:8080/game?userID=Test"));

            ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
            GameHandler gameHandler = new GameHandler(eventPublisher);

            WebSocketMessage<?> message = new TextMessage("Test");

            assertThrows(JsonParseException.class, () -> gameHandler.handleMessage(session, message));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void handleTransportErrorShouldCloseSession() throws Exception {
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        GameHandler gameHandler = new GameHandler(eventPublisher);
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getRemoteAddress()).thenReturn(new InetSocketAddress(1234));
        when(session.getId()).thenReturn("session1");
        when(session.getAcceptedProtocol()).thenReturn("protocol");
        when(session.getHandshakeHeaders()).thenReturn(new HttpHeaders());
        when(session.getUri()).thenReturn(new URI("ws://localhost:8080/game?userID=Test"));
        when(session.isOpen()).thenReturn(true);
        Exception exception = new Exception("Test exception");

        gameHandler.handleTransportError(session, exception);

        verify(session, times(1)).close(CloseStatus.SERVER_ERROR.withReason(exception.getMessage()));
    }

    @Test
    void afterConnectionEstablishedShouldPublishEvent() throws Exception {
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        GameHandler gameHandler = new GameHandler(eventPublisher);
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getRemoteAddress()).thenReturn(new InetSocketAddress(1234));
        when(session.getId()).thenReturn("session1");
        when(session.getAcceptedProtocol()).thenReturn("protocol");
        when(session.getHandshakeHeaders()).thenReturn(new HttpHeaders());
        when(session.getUri()).thenReturn(new URI("ws://localhost:8080/game?userID=Test"));

        gameHandler.afterConnectionEstablished(session);

        verify(eventPublisher, times(1)).publishEvent(any(SessionConnectEvent.class));
    }
    @Test
    void afterConnectionEstablishedNoClientAddress() throws Exception {
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        GameHandler gameHandler = new GameHandler(eventPublisher);
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getRemoteAddress()).thenReturn(null);
        when(session.getId()).thenReturn("session1");
        when(session.getUri()).thenReturn(new URI("ws://localhost:8080/game?userID=Test"));
        when(session.getAcceptedProtocol()).thenReturn("protocol");
        when(session.getHandshakeHeaders()).thenReturn(new HttpHeaders());

        gameHandler.afterConnectionEstablished(session);

        verify(eventPublisher, times(1)).publishEvent(any(SessionConnectEvent.class));
    }
    @Test
    void afterConnectionEstablishedNoUri() {
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        GameHandler gameHandler = new GameHandler(eventPublisher);
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getRemoteAddress()).thenReturn(new InetSocketAddress(1234));
        when(session.getId()).thenReturn("session1");
        when(session.getUri()).thenReturn(null);
        when(session.getAcceptedProtocol()).thenReturn("protocol");
        when(session.getHandshakeHeaders()).thenReturn(new HttpHeaders());

        gameHandler.afterConnectionEstablished(session);

        verify(eventPublisher, times(1)).publishEvent(any(SessionConnectEvent.class));
    }

    @Test
    void afterConnectionClosedShouldPublishEvent() throws Exception {
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        GameHandler gameHandler = new GameHandler(eventPublisher);
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getRemoteAddress()).thenReturn(new InetSocketAddress(1234));
        when(session.getId()).thenReturn("session1");
        when(session.getUri()).thenReturn(new URI("ws://localhost:8080"));
        when(session.getAcceptedProtocol()).thenReturn("protocol");
        when(session.getHandshakeHeaders()).thenReturn(new HttpHeaders());

        gameHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

        verify(eventPublisher, times(1)).publishEvent(any(SessionDisconnectEvent.class));
    }
    @Test
    void afterConnectionClosedNoClientAddress() throws Exception {
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        GameHandler gameHandler = new GameHandler(eventPublisher);
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getRemoteAddress()).thenReturn(null);
        when(session.getId()).thenReturn("session1");
        when(session.getUri()).thenReturn(new URI("ws://localhost:8080"));
        when(session.getAcceptedProtocol()).thenReturn("protocol");
        when(session.getHandshakeHeaders()).thenReturn(new HttpHeaders());

        gameHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

        verify(eventPublisher, times(1)).publishEvent(any(SessionDisconnectEvent.class));
    }
    
}