package at.aau.anti_mon.client.integrationtests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.command.Command;
import at.aau.anti_mon.client.command.CommandFactory;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.command.PinCommand;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.PinReceivedEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.NetworkModule;
import at.aau.anti_mon.client.networking.WebSocketClient;
import at.aau.anti_mon.client.viewmodels.CreateGameViewModel;

class WebSocketClientTest extends AntiMonopolyApplication {
    @Inject
    WebSocketClient client;
    @Mock
    GlobalEventQueue globalEventQueue;

    @Mock
    CreateGameViewModel createGameViewModel;

    @BeforeEach
    void init() {
        openMocks(this);
        TestComponent testComponent = DaggerTestComponent.builder().networkModule(new NetworkModule(this)).build();
        setGlobalEventQueue(globalEventQueue);
        testComponent.inject(this);

        Map<String, Command> commandMap = new HashMap<>();
        commandMap.put("PIN", new PinCommand(createGameViewModel));
        client.setCommandFactory(new CommandFactory(commandMap));
    }

    @Test
    void testNewCreateGameCommandAndGetPin() {

        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.PIN);
        jsonDataDTO.putData("pin", "1234");
        String message = JsonDataManager.createJsonMessage(jsonDataDTO);
        assertNotNull(message);
        client.getWebSocketListener().onMessage(client.getWebSocket(), message);
        verify(createGameViewModel).createGame("1234");

    }

}
