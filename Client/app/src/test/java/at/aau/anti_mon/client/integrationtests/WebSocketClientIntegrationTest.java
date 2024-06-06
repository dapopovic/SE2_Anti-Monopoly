package at.aau.anti_mon.client.integrationtests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.command.Command;
import at.aau.anti_mon.client.command.CommandFactory;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.command.HeartBeatCommand;
import at.aau.anti_mon.client.command.JoinGameCommand;
import at.aau.anti_mon.client.command.LeaveGameCommand;
import at.aau.anti_mon.client.command.OnReadyCommand;
import at.aau.anti_mon.client.command.PinCommand;
import at.aau.anti_mon.client.command.StartGameCommand;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.HeartBeatEvent;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.MessagingService;
import at.aau.anti_mon.client.networking.WebSocketClient;
import at.aau.anti_mon.client.networking.WebSocketClientListener;
import at.aau.anti_mon.client.viewmodels.CreateGameViewModel;
import at.aau.anti_mon.client.viewmodels.LobbyViewModel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;

class WebSocketClientIntegrationTest {

    @Mock
    private OkHttpClient mockOkHttpClient;

    @Mock
    private CommandFactory mockCommandFactory;

    @Mock
    private WebSocket mockWebSocket;

    @Mock
    CreateGameViewModel mockCreateGameViewModel;

    @Mock
    LobbyViewModel mockLobbyViewModel;

    @Mock
    GlobalEventQueue mockGlobalEventQueue;

    @Mock
    Command mockCommand;

    @InjectMocks
    private WebSocketClient webSocketClient;
    private WebSocketClientListener webSocketClientListener;


    /**
     * This method is executed before each test.
     * It initializes the mocks and the WebSocketClient.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webSocketClient = new WebSocketClient( mockCommandFactory);
        webSocketClient.setConnected(true);
        webSocketClientListener = new WebSocketClientListener(webSocketClient);

        when(mockOkHttpClient.newWebSocket(any(Request.class), any(WebSocketClientListener.class))).thenReturn(mockWebSocket);
    }


    /**
     * This test checks if the WebSocketClient can connect to the server.
     */
    @Test
    void shouldConnectToServer() {
        webSocketClient.setConnected(false);
        webSocketClient.connectToServer("testUser");
        verify(mockOkHttpClient).newWebSocket(any(Request.class), any(WebSocketClientListener.class));
        Assertions.assertTrue(webSocketClient.isConnected());
    }


    /**
     * This test checks if the WebSocketClient can disconnect from the server.
     */
    @Test
    void shouldDisconnectFromServer() {
        webSocketClient.connectToServer("testUser");
        webSocketClient.disconnect();
        verify(mockWebSocket).close(anyInt(), anyString());
    }


    /**
     * This test checks if the WebSocketClient can send messages to the server.
     */
    @Test
    void shouldSendMessageToServer() {
        // Check Connection
        webSocketClient.connectToServer("testUser");
        verify(mockOkHttpClient).newWebSocket(any(Request.class), any(WebSocketClientListener.class));

        // Check Method 1 to send messages
        String outgoingMessage = MessagingService.createUserMessage("testuser", Commands.CREATE_GAME).getMessage();
        webSocketClient.sendMessageToServer(outgoingMessage);
        verify(mockWebSocket).send(outgoingMessage);

        // Check Method 2 to send messages
        JsonDataDTO jsonDataDTO = new JsonDataDTO(Commands.CREATE_GAME);
        jsonDataDTO.putData("username", "testUser");
        webSocketClient.sendJsonData(jsonDataDTO);
        verify(mockWebSocket).send(outgoingMessage);

        // Check Method 3 to send messages
        MessagingService.createUserMessage("testuser", Commands.CREATE_GAME).sendMessage();
        verify(mockWebSocket).send(outgoingMessage);
    }


    /**
     * This test checks if the WebSocketClient can handle incoming messages.
     */
    @Test
    void shouldSendMessageAndHandleIncomingMessage(){
        webSocketClient.connectToServer("testUser");
        verify(mockOkHttpClient).newWebSocket(any(Request.class), any(WebSocketClientListener.class));

        String outgoingMessage = MessagingService.createUserMessage("testuser", Commands.CREATE_GAME).getMessage();
        String incomingMessage = MessagingService.createUserMessage("1234", Commands.PIN).getMessage();

        doAnswer((Answer<Void>) invocation -> {
            webSocketClientListener.onMessage(mockWebSocket, incomingMessage);
            return null;
        }).when(mockWebSocket).send(outgoingMessage);

        when(mockCommandFactory.getCommand(anyString())).thenReturn(mockCommand);

        doAnswer((Answer<Void>) invocation -> {
            mockCreateGameViewModel.createGame("1234");
            return null;
        }).when(mockCommand).execute(any(JsonDataDTO.class));


        // Send a message to the server
        webSocketClient.sendMessageToServer(outgoingMessage);

        verify(mockWebSocket).send(outgoingMessage);
        verify(mockCommandFactory).getCommand(Commands.PIN.name());
        verify(mockCreateGameViewModel).createGame("1234");
    }


    /**
     * This test checks if the WebSocketClient can handle invalid incoming messages.
     */
    @Test
    void shouldHandleIncomingMessage() {
        webSocketClient.connectToServer("testUser");
        String incomingMessage = MessagingService.createUserMessage("1234", Commands.PIN).getMessage();
        when(mockCommandFactory.getCommand(anyString())).thenReturn(mockCommand);

        webSocketClient.handleIncomingMessage(incomingMessage);

        verify(mockCommandFactory).getCommand(anyString());
        verify(mockCommand).execute(any(JsonDataDTO.class));
    }

    /**
     * This test checks if the WebSocketClient can handle invalid incoming messages.
     */
    @Test
    void shouldHandleInvalidIncomingMessage() {
        webSocketClient.connectToServer("testUser");
        String invalidMessage = "invalid message format";

        webSocketClient.handleIncomingMessage(invalidMessage);

        // Überprüfen, dass keine Commands ausgeführt wurden
        verify(mockCommandFactory, Mockito.never()).getCommand(anyString());
    }


    /**
     * This test checks if LiveDataEvents are triggered when a message is received.
     */
    @Test
    void shouldTriggerLiveDataEventCreateGameWhenMessageReceived() {
        String incomingMessage = MessagingService.createUserMessage( "1234", Commands.PIN).getMessage();
        JsonDataDTO jsonDataDTO = new JsonDataDTO(Commands.PIN);
        jsonDataDTO.putData("pin", "1234");

        when(mockCommandFactory.getCommand(anyString())).thenReturn(mockCommand);

        doAnswer((Answer<Void>) invocation -> {
            new PinCommand(mockCreateGameViewModel).execute(jsonDataDTO);
            return null;
        }).when(mockCommand).execute(any(JsonDataDTO.class));

        // Start Test
        webSocketClientListener.onMessage(mockWebSocket, incomingMessage);

        // Verify
        verify(mockCommandFactory).getCommand(Commands.PIN.name());
        verify(mockCreateGameViewModel).createGame("1234");
    }

    /**
     * This test checks if the WebSocketClient can handle a HeartBeatCommand.
     */
    @Test
    void testHeartBeatCommandReceivesHeartBeatEvent() {
        String message = MessagingService.createMessage("test", Commands.HEARTBEAT).getMessage();
        JsonDataDTO jsonDataDTO = new JsonDataDTO(Commands.HEARTBEAT);
        jsonDataDTO.putData("msg", "test");
        assertNotNull(message);

        when(mockCommandFactory.getCommand(anyString())).thenReturn(mockCommand);

        doAnswer((Answer<Void>) invocation -> {
            new HeartBeatCommand(mockGlobalEventQueue).execute(jsonDataDTO);
            return null;
        }).when(mockCommand).execute(any(JsonDataDTO.class));

        webSocketClientListener.onMessage(mockWebSocket, message);

        verify(mockGlobalEventQueue).enqueueEvent(any(HeartBeatEvent.class));
    }


    /**
     * This test checks if the WebSocketClient can handle a JoinGameCommand.
     */
    @Test
    void testNewUserCommandShouldFireUserJoinedLobbyEvent() {
        String message = MessagingService.createUserMessage("testUser", false, false, Commands.NEW_USER).getMessage();
        assertNotNull(message);
        JsonDataDTO jsonDataDTO = new JsonDataDTO(Commands.NEW_USER);
        jsonDataDTO.putData("username", "testUser");
        jsonDataDTO.putData("isOwner", String.valueOf(false));
        jsonDataDTO.putData("isReady", String.valueOf(false));
        assertNotNull(message);

        when(mockCommandFactory.getCommand(anyString())).thenReturn(mockCommand);

        doAnswer((Answer<Void>) invocation -> {
            new JoinGameCommand(mockLobbyViewModel).execute(jsonDataDTO);
            return null;
        }).when(mockCommand).execute(any(JsonDataDTO.class));

        webSocketClientListener.onMessage(mockWebSocket, message);

        verify(mockLobbyViewModel).userJoined("testUser", false, false);
    }

    /**
     * This test checks if the WebSocketClient can handle a LeaveGameCommand.
     */
    @Test
    void testLeaveCommandShouldFireUserLeftLobbyEvent() {
        String message = MessagingService.createUserMessage("testUser", Commands.LEAVE_GAME).getMessage();
        assertNotNull(message);
        JsonDataDTO jsonDataDTO = new JsonDataDTO(Commands.LEAVE_GAME);
        jsonDataDTO.putData("username", "testUser");
        assertNotNull(message);

        when(mockCommandFactory.getCommand(anyString())).thenReturn(mockCommand);

        doAnswer((Answer<Void>) invocation -> {
            new LeaveGameCommand(mockLobbyViewModel).execute(jsonDataDTO);
            return null;
        }).when(mockCommand).execute(any(JsonDataDTO.class));

        webSocketClientListener.onMessage(mockWebSocket, message);

        verify(mockLobbyViewModel).userLeft("testUser");
    }

    /**
     * This test checks if the WebSocketClient can handle a StartGameCommand.
     */
    @Test
    void testStartGameCommandShouldFireGameStartedEvent() {
        User[] users = {
                new User("testUser", false, false, 1000),
                new User("testUser2", false, false, 1000)
        };

        String message = MessagingService.createMessage("users",users,Commands.START_GAME).getMessage();
        assertNotNull(message);
        JsonDataDTO jsonDataDTO = new JsonDataDTO(Commands.START_GAME);
        jsonDataDTO.putData("users", JsonDataManager.createJsonMessage(users));
        assertNotNull(message);

        when(mockCommandFactory.getCommand(anyString())).thenReturn(mockCommand);

        doAnswer((Answer<Void>) invocation -> {
            new StartGameCommand(mockLobbyViewModel).execute(jsonDataDTO);
            return null;
        }).when(mockCommand).execute(any(JsonDataDTO.class));

        webSocketClientListener.onMessage(mockWebSocket, message);
        verify(mockLobbyViewModel).startGame(new ArrayList<>(Arrays.asList(users)));
    }

    /**
     * This test checks if the WebSocketClient can handle a ReadyCommand.
     */
    @Test
    void testOnReadyCommandShouldFireReadyEvent() {
        String message = MessagingService.createUserMessage("testUser",true, Commands.READY).getMessage();
        assertNotNull(message);
        JsonDataDTO jsonDataDTO = new JsonDataDTO(Commands.LEAVE_GAME);
        jsonDataDTO.putData("username", "testUser");
        jsonDataDTO.putData("isReady", String.valueOf(true));
        assertNotNull(message);

        when(mockCommandFactory.getCommand(anyString())).thenReturn(mockCommand);

        doAnswer((Answer<Void>) invocation -> {
            new OnReadyCommand(mockLobbyViewModel).execute(jsonDataDTO);
            return null;
        }).when(mockCommand).execute(any(JsonDataDTO.class));

        webSocketClientListener.onMessage(mockWebSocket, message);

        verify(mockLobbyViewModel).readyUp("testUser", true);
    }

}