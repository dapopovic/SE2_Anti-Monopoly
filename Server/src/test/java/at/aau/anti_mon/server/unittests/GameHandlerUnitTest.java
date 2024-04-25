package at.aau.anti_mon.server.unittests;

import at.aau.anti_mon.server.commands.Command;
import at.aau.anti_mon.server.commands.CommandFactory;
import at.aau.anti_mon.server.commands.CreateGameCommand;
import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.events.CreateLobbyEvent;
import at.aau.anti_mon.server.game.JsonDataDTO;
import at.aau.anti_mon.server.listener.UserEventListener;
import at.aau.anti_mon.server.service.LobbyService;
import at.aau.anti_mon.server.service.SessionManagementService;
import at.aau.anti_mon.server.service.UserService;
import at.aau.anti_mon.server.websocket.handler.GameHandler;
import at.aau.anti_mon.server.events.SessionConnectEvent;
import at.aau.anti_mon.server.events.SessionDisconnectEvent;
import at.aau.anti_mon.server.websocket.manager.JsonDataManager;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.net.InetSocketAddress;
import java.net.URI;

import static org.mockito.Mockito.*;

public class GameHandlerUnitTest {

    @Test
    public void handleMessageShouldExecuteCommand() throws Exception {

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
        //when(gameCommandFactory.getCommand(Commands.CREATE_GAME.name())).thenReturn(createGameCommand);
        doCallRealMethod().when(gameCommandFactory).getCommand(Commands.CREATE_GAME.name());

        UserEventListener userEventListener = mock(UserEventListener.class);
        CreateLobbyEvent createLobbyEvent = new CreateLobbyEvent(session, new UserDTO("Test"));

        // GAME HANDLER:
        GameHandler gameHandler = new GameHandler(eventPublisher);

        // Erstellen der Testdaten:
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.CREATE_GAME);
        jsonDataDTO.putData("username", "Test");
        String jsonMessage = JsonDataManager.createStringFromJsonMessage(jsonDataDTO);
        TextMessage message = new TextMessage(jsonMessage);


        // Konfigurieren des Command Verhaltens, um die Interaction weiterzuleiten
        doAnswer(invocation -> {
            ApplicationEventPublisher publisher = invocation.getArgument(0, ApplicationEventPublisher.class);
            // Simuliere das Event, das normalerweise in CreateGameCommand ausgelöst wird
            CreateLobbyEvent event = new CreateLobbyEvent(session, new UserDTO("Test"));
            publisher.publishEvent(event);
            return null;
        }).when(createGameCommand).execute(any(), any());

        // Konfigurieren des eventPublisher, um JsonDataManager.sendPin aufzurufen
        doAnswer(invocation -> {
            CreateLobbyEvent event = invocation.getArgument(0);
            JsonDataManager.sendPin(session, "1234");
            return null;
        }).when(eventPublisher).publishEvent(any(CreateLobbyEvent.class));



        // When onCreateLobbyEvent() is called, trigger sendPin()
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                CreateLobbyEvent event = invocation.getArgument(0);
                JsonDataManager.sendPin(event.getSession(), "1234");
                return null;
            }
        }).when(userEventListener).onCreateLobbyEvent(any(CreateLobbyEvent.class));


        //when(gameCommandFactory.getCommand(jsonDataDTO.getCommand().getCommand())).thenReturn(new CreateGameCommand(eventPublisher));


        //Command command = gameCommandFactory.getCommand(jsonDataDTO.getCommand().getCommand());
        //command.execute(session, jsonDataDTO);

        //when(userEventListener.onCreateLobbyEvent());




        // Mocking des Verhaltens von CreateGameCommand
        //doCallRealMethod().when(JsonDataManager).sendPin(session, "1234");
        // doCallRealMethod().when(createGameCommand).execute(session, jsonDataDTO);

        // Ausführen der Methode, die getestet wird
        gameHandler.handleMessage(session, message);

        //doNothing().when(createGameCommand).execute(session, jsonDataDTO);
        // Verifizieren, dass die execute Methode des Commands aufgerufen wurde
       // verify(createGameCommand, times(1)).execute(session, jsonDataDTO);

        // Ausführung des Tests
        verify(session, times(1)).sendMessage(any());
    }

    @Test
    public void handleTransportErrorShouldCloseSession() throws Exception {
        ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
        GameHandler gameHandler = new GameHandler(eventPublisher);
        WebSocketSession session = mock(WebSocketSession.class);

        gameHandler.handleTransportError(session, new Exception("Test exception"));

        verify(session, times(1)).close(any(CloseStatus.class));
    }

    @Test
    public void afterConnectionEstablishedShouldPublishEvent() throws Exception {
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
    public void afterConnectionClosedShouldPublishEvent() throws Exception {
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
}