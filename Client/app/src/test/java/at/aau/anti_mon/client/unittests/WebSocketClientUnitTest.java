package at.aau.anti_mon.client.unittests;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Objects;

import at.aau.anti_mon.client.command.CommandFactory;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.command.CreateGameCommand;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.WebSocketClient;
import at.aau.anti_mon.client.networking.WebSocketClientListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketClientUnitTest {

    private WebSocketClient webSocketClient;
    private WebSocketListener webSocketListener;

    @Mock
    private CommandFactory commandFactory;
    @Mock
    private WebSocket webSocket;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        OkHttpClient okHttpClient = mock(OkHttpClient.class);
        webSocketClient = new WebSocketClient();
        webSocketListener = new WebSocketClientListener(webSocketClient);
        webSocketClient.setWebSocket(webSocket);
    }

    @Test
    void testHandleIncomingMessage() {
        CreateGameCommand createGameCommand = mock(CreateGameCommand.class);
        when(commandFactory.getCommand("CREATE_GAME")).thenReturn(createGameCommand);

        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.CREATE_GAME);
        jsonDataDTO.putData("username", "testUser");



        String jsonData = JsonDataManager.createJsonMessage(jsonDataDTO);
        assertNotNull(jsonData);

        webSocketListener.onMessage(webSocket, jsonData);

        verify(commandFactory).getCommand("CREATE_GAME");
        verify(createGameCommand).execute(any(JsonDataDTO.class));
    }

    @Test
    void testHandleIncomingMessageWrongJson() {
        webSocketListener.onMessage(webSocket, "wrongJson");
        verify(commandFactory, never()).getCommand(anyString());
    }

    @Test
    void testHandleIncomingMessageWithNoCommand() {
        webSocketListener.onMessage(webSocket, "{\"data\": {\"username\": \"testUser\"}}");
        verify(commandFactory, never()).getCommand(anyString());
    }

    @Test
    void testHandleIncomingMessageWithWrongCommand() {
        webSocketListener.onMessage(webSocket, "{\"command\": \"WRONG_COMMAND\", \"data\": {\"username\": \"testUser\"}}");
        verify(commandFactory, never()).getCommand(anyString());
    }

    @Test
    void testOnClosed() {
        webSocketListener.onClosed(webSocket, 1000, "testReason");
        assertFalse(webSocketClient.isConnected());
    }

    @Test
    void testDisconnect() {
        webSocketClient.disconnect();
        assertFalse(webSocketClient.isConnected());
        assertNull(webSocketClient.getWebSocket());
    }

    @Test
    void testOnFailure() {
        webSocketListener.onFailure(webSocket, new Throwable("testError"), null);
        assertFalse(webSocketClient.isConnected());
        assertNull(webSocketClient.getWebSocket());
    }

    @Test
    void testOnFailureWithResponse() {
        Response response = new Response.Builder()
                .code(200)
                .message("OK")
                .protocol(okhttp3.Protocol.HTTP_1_1)
                .request(new Request.Builder().url("http://localhost").build())
                .build();
        webSocketListener.onFailure(webSocket, new Throwable("testError"), response);
        assertFalse(webSocketClient.isConnected());
        assertNull(webSocketClient.getWebSocket());
    }

    @Test
    void testOnConnectToServer() {
        webSocketClient.connectToServer("testUser");
        assertNotNull(webSocketClient.getWebSocket());
    }

    @Test
    void testOnConnectToServerWithoutUserId() {
        webSocketClient.connectToServer(null);
        assertNotNull(webSocketClient.getWebSocket());
    }

    @Test
    void testOnOpen() {
        Response response = new Response.Builder()
                .code(200)
                .message("OK")
                .protocol(okhttp3.Protocol.HTTP_1_1)
                .request(new Request.Builder().url("http://localhost").build())
                .build();
        webSocketListener.onOpen(webSocket, response);
        assertTrue(webSocketClient.isConnected());
    }

    @Test
    void testSendJsonData() {
        when(webSocket.send(anyString())).thenReturn(true);

        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.CREATE_GAME);
        jsonDataDTO.putData("username", "testUser");
        webSocketClient.sendJsonData(jsonDataDTO);

        verify(webSocket).send(Objects.requireNonNull(JsonDataManager.createJsonMessage(jsonDataDTO)));
    }

    @Test
    void testSendJsonDataWithSendFailed() {
        when(webSocket.send(anyString())).thenReturn(false);

        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.CREATE_GAME);
        jsonDataDTO.putData("username", "testUser");
        webSocketClient.sendJsonData(jsonDataDTO);

        verify(webSocket).send(Objects.requireNonNull(JsonDataManager.createJsonMessage(jsonDataDTO)));
    }

    @Test
    void testSendWebSocketMessage() {
        when(webSocket.send(anyString())).thenReturn(true);
        Response response = new Response.Builder()
                .code(200)
                .message("OK")
                .protocol(okhttp3.Protocol.HTTP_1_1)
                .request(new Request.Builder().url("http://localhost").build())
                .build();
        webSocketListener.onOpen(webSocket, response);
        webSocketClient.sendMessageToServer("testMessage");
        verify(webSocket).send("testMessage");
    }

    @Test
    void testSendWebSocketMessageNotConnected() {
        webSocketClient.disconnect();
        webSocketClient.sendMessageToServer("testMessage");
        verify(webSocket, never()).send("testMessage");
    }

    @Test
    void testSendWebSocketMessageConnectedButSendFailed() {
        when(webSocket.send(anyString())).thenReturn(false);
        Response response = new Response.Builder()
                .code(200)
                .message("OK")
                .protocol(okhttp3.Protocol.HTTP_1_1)
                .request(new Request.Builder().url("http://localhost").build())
                .build();
        webSocketListener.onOpen(webSocket, response);
        webSocketClient.sendMessageToServer("testMessage");
        verify(webSocket).send("testMessage");
    }
}