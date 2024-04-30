package at.aau.anti_mon.client.unittests;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Objects;

import at.aau.anti_mon.client.command.CommandFactory;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.command.CreateGameCommand;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.WebSocketClient;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

class WebSocketClientTest {
    private WebSocketListener webSocketListener;
    private WebSocketClient webSocketClient;
    @Mock
    private CommandFactory commandFactory;

    @BeforeEach
    void setUp() {
        OkHttpClient okHttpClient = mock(OkHttpClient.class);
        commandFactory = mock(CommandFactory.class);
        webSocketClient = new WebSocketClient(okHttpClient, commandFactory);
        webSocketListener = webSocketClient.getWebSocketListener();

    }

    @Test
    void testHandleIncomingMessage() {
        CreateGameCommand createGameCommand = mock(CreateGameCommand.class);
        when(commandFactory.getCommand("CREATE_GAME")).thenReturn(createGameCommand);
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.CREATE_GAME);
        jsonDataDTO.putData("username", "testUser");

        WebSocket webSocket = mock(WebSocket.class);
        String jsonData = JsonDataManager.createJsonMessage(jsonDataDTO);
        if (jsonData == null) {
            fail("Failed to create JSON message");
            return;
        }
        webSocketListener.onMessage(webSocket, jsonData);
        verify(commandFactory).getCommand("CREATE_GAME");
        verify(commandFactory.getCommand("CREATE_GAME")).execute(any(JsonDataDTO.class));
    }
    @Test
    void testHandleIncomingMessageWrongJson() {
        WebSocket webSocket = mock(WebSocket.class);
        webSocketListener.onMessage(webSocket, "wrongJson");
        verify(commandFactory, never()).getCommand(anyString());
    }
    @Test
    void testHandleIncomingMessageWithNoCommand() {
        WebSocket webSocket = mock(WebSocket.class);
        webSocketListener.onMessage(webSocket, "{\"data\": {\"username\": \"testUser\"}}");
        verify(commandFactory, never()).getCommand(anyString());
    }
    @Test
    void testOnClosed() {
        webSocketListener.onClosed(webSocketClient.getWebSocket(), 1000, "testReason");
        assertFalse(webSocketClient.isConnected());
    }
    @Test
    void testDisconnect() {
        WebSocket webSocket = mock(WebSocket.class);
        webSocketClient.setWebSocket(webSocket);
        webSocketClient.disconnect();
        assertFalse(webSocketClient.isConnected());
        assertNull(webSocketClient.getWebSocket());
    }
    @Test
    void testOnFailure() {
        webSocketListener.onFailure(webSocketClient.getWebSocket(), new Throwable("testError"), null);
        assertFalse(webSocketClient.isConnected());
        assertNull(webSocketClient.getWebSocket());
    }
    @Test
    void testOnFailureWithResponse() {
        Response response = new Response.Builder()
                .code(200)
                .message("OK")
                .protocol(Protocol.HTTP_1_1)
                .request(new Request.Builder().url("http://localhost").build())
                .build();
        webSocketListener.onFailure(webSocketClient.getWebSocket(), new Throwable("testError"), response);
        assertFalse(webSocketClient.isConnected());
        assertNull(webSocketClient.getWebSocket());
    }
    @Test
    void testOnConnectToServer() {
        webSocketClient.setUserId("testUser");
        webSocketClient.connectToServer();
        assertNull(webSocketClient.getWebSocket());
    }
    @Test
    void testOnConnectToServerWithoutUserId() {
        webSocketClient.setWebSocket(mock(WebSocket.class));
        webSocketClient.connectToServer();
        assertNotNull(webSocketClient.getWebSocket());
    }
    @Test
    void testOnOpen() {
        Response response = new Response.Builder()
                .code(200)
                .message("OK")
                .protocol(Protocol.HTTP_1_1)
                .request(new Request.Builder().url("http://localhost").build())
                .build();
        webSocketClient.setWebSocket(mock(WebSocket.class));
        webSocketListener.onOpen(webSocketClient.getWebSocket(), response);
        assertTrue(webSocketClient.isConnected());
    }

    @Test
    void testSendJsonData() {
        WebSocket webSocket = mock(WebSocket.class);
        when(webSocket.send(anyString())).thenReturn(true);
        webSocketClient.setWebSocket(webSocket);
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.CREATE_GAME);
        jsonDataDTO.putData("username", "testUser");
        webSocketClient.sendJsonData(jsonDataDTO);
        verify(webSocket).send(Objects.requireNonNull(JsonDataManager.createJsonMessage(jsonDataDTO)));
    }
    @Test
    void testSendJsonDataWithSendFailed() {
        WebSocket webSocket = mock(WebSocket.class);
        when(webSocket.send(anyString())).thenReturn(false);
        webSocketClient.setWebSocket(webSocket);
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.CREATE_GAME);
        jsonDataDTO.putData("username", "testUser");
        webSocketClient.sendJsonData(jsonDataDTO);
        verify(webSocket).send(Objects.requireNonNull(JsonDataManager.createJsonMessage(jsonDataDTO)));
    }

    @Test
    void testSendWebSocketMessage() {
        Response response = new Response.Builder()
                .code(200)
                .message("OK")
                .protocol(Protocol.HTTP_1_1)
                .request(new Request.Builder().url("http://localhost").build())
                .build();
        WebSocket webSocket = mock(WebSocket.class);
        when(webSocket.send(anyString())).thenReturn(true);
        webSocketClient.setWebSocket(webSocket);
        webSocketListener.onOpen(webSocket, response);
        System.out.println(webSocketClient.isConnected());
        webSocketClient.sendMessageToServer("testMessage");
        verify(webSocket).send("testMessage");
    }
    @Test
    void testSendWebSocketMessageNotConnected() {
        WebSocket webSocket = mock(WebSocket.class);
        when(webSocket.send(anyString())).thenReturn(true);
        webSocketClient.setWebSocket(webSocket);
        webSocketClient.sendMessageToServer("testMessage");
        verify(webSocket, never()).send("testMessage");
    }
    @Test
    void testSendWebsocketMessageNotConnectedButMessageQueuedAndThenConnected() {
        Response response = new Response.Builder()
                .code(200)
                .message("OK")
                .protocol(Protocol.HTTP_1_1)
                .request(new Request.Builder().url("http://localhost").build())
                .build();
        WebSocket webSocket = mock(WebSocket.class);
        when(webSocket.send(anyString())).thenReturn(true);
        webSocketClient.setWebSocket(webSocket);
        webSocketClient.sendMessageToServer("testMessage");
        webSocketClient.setUserId("testUser");
        webSocketClient.connectToServer();
        verify(webSocket, never()).send("testMessage");

        webSocketListener.onOpen(webSocket, response);
        webSocketClient.sendMessageToServer("testMessage2");
        verify(webSocket).send("testMessage2");
        verify(webSocket).send("testMessage");
    }
    @Test
    void testSendWebsocketMessageConnectedButSendFailed() {
        Response response = new Response.Builder()
                .code(200)
                .message("OK")
                .protocol(Protocol.HTTP_1_1)
                .request(new Request.Builder().url("http://localhost").build())
                .build();
        WebSocket webSocket = mock(WebSocket.class);
        when(webSocket.send(anyString())).thenReturn(false);
        webSocketClient.setWebSocket(webSocket);
        webSocketListener.onOpen(webSocket, response);
        webSocketClient.sendMessageToServer("testMessage");
        verify(webSocket).send("testMessage");
    }
}