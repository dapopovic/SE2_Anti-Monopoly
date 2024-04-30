package at.aau.anti_mon.client.integrationtests;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import at.aau.anti_mon.client.command.Command;
import at.aau.anti_mon.client.command.CommandFactory;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.command.CreateGameCommand;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.WebSocketClient;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

class WebSocketClientTest {
    private WebSocketListener webSocketListener;
    @Mock
    private CommandFactory commandFactory;
    @BeforeEach
    void setUp() {
        OkHttpClient okHttpClient = mock(OkHttpClient.class);
        commandFactory = mock(CommandFactory.class);
        WebSocketClient webSocketClient = new WebSocketClient(okHttpClient, commandFactory);
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
}