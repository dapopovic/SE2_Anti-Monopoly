package at.aau.anti_mon.server.unittests;

import at.aau.anti_mon.server.commands.CommandFactory;
import at.aau.anti_mon.server.commands.CreateGameCommand;
import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.enums.Figures;
import at.aau.anti_mon.server.enums.Roles;
import at.aau.anti_mon.server.events.UserCreatedLobbyEvent;
import at.aau.anti_mon.server.dtos.JsonDataDTO;
import at.aau.anti_mon.server.listener.UserEventListener;
import at.aau.anti_mon.server.service.SessionManagementService;
import at.aau.anti_mon.server.websocket.handler.GameHandler;
import at.aau.anti_mon.server.events.SessionConnectEvent;
import at.aau.anti_mon.server.events.SessionDisconnectEvent;
import at.aau.anti_mon.server.utilities.JsonDataUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Unit-Test class for GameHandler
 */
class GameHandlerUnitTest {

    ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    CommandFactory gameCommandFactory = mock(CommandFactory.class);
    SessionManagementService sessionManagementService = mock(SessionManagementService.class);

    private WebSocketSession setupMockWebSocketSession() throws URISyntaxException {
        WebSocketSession session = mock(WebSocketSession.class);
        when(session.getRemoteAddress()).thenReturn(new InetSocketAddress(1234));
        when(session.getId()).thenReturn("session1");
        when(session.getAcceptedProtocol()).thenReturn("protocol");
        when(session.getHandshakeHeaders()).thenReturn(new HttpHeaders());
        when(session.getUri()).thenReturn(new URI("ws://localhost:8080/game?userID=Test"));
        when(session.isOpen()).thenReturn(true);
        return session;
    }



    @Test
    void handleMessageShouldExecuteCommandAndSendAnswer() throws Exception {

        //Given
        WebSocketSession session = setupMockWebSocketSession();
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        CreateGameCommand createGameCommand = mock(CreateGameCommand.class);
        UserEventListener gameEventListener = mock(UserEventListener.class);
        when(gameCommandFactory.getCommand(Commands.CREATE_GAME.toString())).thenReturn(createGameCommand);

        // GAME HANDLER:
        GameHandler gameHandler = new GameHandler(eventPublisher,gameCommandFactory,sessionManagementService);

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
            UserCreatedLobbyEvent event = new UserCreatedLobbyEvent(session, new UserDTO("Test", true, true, Roles.COMPETITOR, Figures.GreenCircle));
            eventPublisher.publishEvent(event);
            return null;
        }).when(createGameCommand).execute(any(), any());

        // Konfigurieren des eventPublisher, um JsonDataManager.sendPin aufzurufen
        doAnswer(invocation -> {
            // UserCreatedLobbyEvent event = invocation.getArgument(0);
            JsonDataUtility.sendPin(session, "1234");
            return null;
        }).when(eventPublisher).publishEvent(any(UserCreatedLobbyEvent.class));

        // When onCreateLobbyEvent() is called -> sendPin()
        doAnswer((Answer<Void>) invocation -> {
            UserCreatedLobbyEvent event = invocation.getArgument(0);
            JsonDataUtility.sendPin(event.getSession(), "1234");
            return null;
        }).when(gameEventListener).onCreateLobbyEvent(any(UserCreatedLobbyEvent.class));

        // When
        gameHandler.handleMessage(session, message);

        // Then
        verify(session, times(1)).sendMessage(any());
    }

    // TODO: Changed the GameHandler for Testing ( catched the Exception )
    /*@Test
    void handleMessageShouldThrowErrorBecauseWrongJsonData() throws URISyntaxException {
        // Given
        try (WebSocketSession session = setupMockWebSocketSession()) {
            ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
            GameHandler gameHandler = new GameHandler(eventPublisher,gameCommandFactory);
            WebSocketMessage<?> message = new TextMessage("Test");

            // When & Then
            assertThrows(JsonParseException.class, () -> gameHandler.handleMessage(session, message));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testHandleMessageInvalidCommand() throws URISyntaxException, IOException {
        // Given
        try (WebSocketSession session = setupMockWebSocketSession()) {
            GameHandler gameHandler = new GameHandler(eventPublisher,gameCommandFactory);
            String json = "{\"command\":\"INVALID_COMMAND\",\"data\":{\"err\":\"Test\"}}";
            when(gameCommandFactory.getCommand("INVALID_COMMAND")).thenReturn(null);
            WebSocketMessage<?> message = new TextMessage(json);

            // When
            assertThrows(JsonProcessingException.class, () -> gameHandler.handleMessage(session, message));

            // Then
            verify(gameCommandFactory, never()).getCommand(String.valueOf(any(Commands.class)));
        }
    }

     */

    @Test
    void handleTransportErrorShouldCloseSession() throws Exception {
        // Given
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        GameHandler gameHandler = new GameHandler(eventPublisher,gameCommandFactory,sessionManagementService);
        WebSocketSession session = setupMockWebSocketSession();
        Exception exception = new Exception("Test exception");

        // When
        gameHandler.handleTransportError(session, exception);

        // Then
        verify(session, times(1)).close(CloseStatus.SERVER_ERROR.withReason(exception.getMessage()));
    }

    @Test
    void afterConnectionEstablishedShouldPublishEvent() throws Exception {
        // Given
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        GameHandler gameHandler = new GameHandler(eventPublisher,gameCommandFactory,sessionManagementService);
        WebSocketSession session = setupMockWebSocketSession();

        // When
        gameHandler.afterConnectionEstablished(session);

        // Then
        verify(eventPublisher, times(1)).publishEvent(any(SessionConnectEvent.class));
    }

    @Test
    void afterConnectionEstablishedNoClientAddress() throws Exception {
        // Given
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        GameHandler gameHandler = new GameHandler(eventPublisher,gameCommandFactory,sessionManagementService);
        WebSocketSession session = setupMockWebSocketSession();
        when(session.getRemoteAddress()).thenReturn(null);

        // When
        gameHandler.afterConnectionEstablished(session);

        // Then
        verify(eventPublisher, times(1)).publishEvent(any(SessionConnectEvent.class));
    }
    @Test
    void afterConnectionEstablishedNoUri() throws URISyntaxException {
        // Given
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        GameHandler gameHandler = new GameHandler(eventPublisher,gameCommandFactory,sessionManagementService);
        WebSocketSession session = setupMockWebSocketSession();
        when(session.getUri()).thenReturn(null);

        // When
        gameHandler.afterConnectionEstablished(session);

        // Then
        verify(eventPublisher, times(1)).publishEvent(any(SessionConnectEvent.class));
    }

    @Test
    void afterConnectionClosedShouldPublishEvent() throws Exception {
        // Given
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        GameHandler gameHandler = new GameHandler(eventPublisher,gameCommandFactory,sessionManagementService);
        WebSocketSession session = setupMockWebSocketSession();

        // When
        gameHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

        // Then
        verify(eventPublisher, times(1)).publishEvent(any(SessionDisconnectEvent.class));
    }

    @Test
    void afterConnectionClosedNoClientAddress() throws Exception {
        // Given
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        GameHandler gameHandler = new GameHandler(eventPublisher,gameCommandFactory,sessionManagementService);
        WebSocketSession session = setupMockWebSocketSession();
        when(session.getRemoteAddress()).thenReturn(null);

        gameHandler.afterConnectionClosed(session, CloseStatus.NORMAL);

        verify(eventPublisher, times(1)).publishEvent(any(SessionDisconnectEvent.class));
    }
    
}