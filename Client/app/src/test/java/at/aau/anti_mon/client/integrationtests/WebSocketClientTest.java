package at.aau.anti_mon.client.integrationtests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.openMocks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.inject.Inject;

import at.aau.anti_mon.client.AntiMonopolyApplication;
import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.PinReceivedEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.NetworkModule;
import at.aau.anti_mon.client.networking.WebSocketClient;
import at.aau.anti_mon.client.viewmodels.CreateGameViewModel;

class WebSocketClientTest extends AntiMonopolyApplication {
    private static final String BASE_URL = "ws://localhost:8080/game?userID=";

    @Inject
    WebSocketClient client;
    @Mock
    GlobalEventQueue globalEventQueue;

    @Inject
    CreateGameViewModel createGameViewModel;

    @BeforeEach
    void init() {
        openMocks(this);
        TestComponent testComponent = DaggerTestComponent.builder().networkModule(new NetworkModule(this)).build();
        setGlobalEventQueue(globalEventQueue);
        testComponent.inject(this);
    }

    @Test
    void testNewCreateGameCommandAndGetPin() {
        client.setUserId("test");
        client.connectToServer(BASE_URL + "test");
        assertTrue(client.isConnected());

        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.CREATE_GAME);
        jsonDataDTO.putData("username", "test");
        String message = JsonDataManager.createJsonMessage(jsonDataDTO);
        client.sendMessageToServer(message);
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            fail("Thread interrupted");
//        }
//        verify(createGameViewModel).createGame(any(String.class));

    }

}
