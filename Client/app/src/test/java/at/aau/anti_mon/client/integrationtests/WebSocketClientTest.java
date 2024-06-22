package at.aau.anti_mon.client.integrationtests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import at.aau.anti_mon.client.command.ChangeBalanceCommand;
import at.aau.anti_mon.client.command.CheatingCommand;
import at.aau.anti_mon.client.command.Command;
import at.aau.anti_mon.client.command.CommandFactory;
import at.aau.anti_mon.client.command.DiceNumberCommand;
import at.aau.anti_mon.client.command.HeartBeatCommand;
import at.aau.anti_mon.client.command.LeaveGameCommand;
import at.aau.anti_mon.client.command.NewUserCommand;
import at.aau.anti_mon.client.command.OnReadyCommand;
import at.aau.anti_mon.client.command.PinCommand;
import at.aau.anti_mon.client.command.StartGameCommand;
import at.aau.anti_mon.client.enums.Commands;
import at.aau.anti_mon.client.enums.Figures;
import at.aau.anti_mon.client.events.ChangeBalanceEvent;
import at.aau.anti_mon.client.events.CheatingEvent;
import at.aau.anti_mon.client.events.DiceNumberReceivedEvent;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.HeartBeatEvent;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.networking.NetworkModule;
import at.aau.anti_mon.client.networking.WebSocketClient;
import at.aau.anti_mon.client.viewmodels.CreateGameViewModel;
import at.aau.anti_mon.client.viewmodels.LobbyViewModel;

class WebSocketClientTest extends AntiMonopolyApplication {
    @Inject
    WebSocketClient client;
    @Mock
    GlobalEventQueue globalEventQueue;
    @Inject
    CreateGameViewModel createGameViewModel;
    @Inject
    LobbyViewModel lobbyViewModel;
    @BeforeEach
    void init() {
        openMocks(this);
        TestComponent testComponent = DaggerTestComponent.builder().networkModule(new NetworkModule(this)).build();
        setGlobalEventQueue(globalEventQueue);
        testComponent.inject(this);

        Map<String, Command> commandMap = new HashMap<>();
        commandMap.put("PIN", new PinCommand(createGameViewModel));
        commandMap.put("HEARTBEAT", new HeartBeatCommand(globalEventQueue));
        commandMap.put("NEW_USER", new NewUserCommand(lobbyViewModel));
        commandMap.put("START_GAME", new StartGameCommand(lobbyViewModel));
        commandMap.put("LEAVE_GAME", new LeaveGameCommand(lobbyViewModel));
        commandMap.put("READY", new OnReadyCommand(lobbyViewModel));
        commandMap.put("DICENUMBER", new DiceNumberCommand(globalEventQueue));
        commandMap.put("CHANGE_BALANCE", new ChangeBalanceCommand(globalEventQueue));
        commandMap.put("CHEATING", new CheatingCommand(globalEventQueue));
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
    }

    @Test
    void testHeartBeatCommandReceivesHeartBeatEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.HEARTBEAT);
        jsonDataDTO.putData("msg", "test");
        String message = JsonDataManager.createJsonMessage(jsonDataDTO);
        assertNotNull(message);
        client.getWebSocketListener().onMessage(client.getWebSocket(), message);
        verify(globalEventQueue).enqueueEvent(any(HeartBeatEvent.class));
    }

    @Test
    void testNewUserCommandShouldFireUserJoinedLobbyEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.NEW_USER);
        jsonDataDTO.putData("username", "testUser");
        jsonDataDTO.putData("isOwner", "false");
        jsonDataDTO.putData("isReady", "false");
        String message = JsonDataManager.createJsonMessage(jsonDataDTO);
        assertNotNull(message);
        client.getWebSocketListener().onMessage(client.getWebSocket(), message);
    }

    @Test
    void testLeaveCommandShouldFireUserLeftLobbyEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.LEAVE_GAME);
        jsonDataDTO.putData("username", "testUser");
        String message = JsonDataManager.createJsonMessage(jsonDataDTO);
        assertNotNull(message);
        client.getWebSocketListener().onMessage(client.getWebSocket(), message);
    }
    @Test
    void testStartGameCommandShouldFireGameStartedEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.START_GAME);
        User[] users = {
                new User("testUser", false, false, 1000, null, Figures.GREEN_CIRCLE,false,false),
                new User("testUser2", false, false, 1000, null, Figures.BLUE_CIRCLE,false,false)
        };
        jsonDataDTO.putData("users", JsonDataManager.createJsonMessage(users));
        String message = JsonDataManager.createJsonMessage(jsonDataDTO);
        assertNotNull(message);
        client.getWebSocketListener().onMessage(client.getWebSocket(), message);
        // fixme the tests here are not testing anything meaningful except for the message != null check
    }
    @Test
    void testOnReadyCommandShouldFireReadyEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.READY);
        jsonDataDTO.putData("username", "testUser");
        jsonDataDTO.putData("isReady", "true");
        String message = JsonDataManager.createJsonMessage(jsonDataDTO);
        assertNotNull(message);
        client.getWebSocketListener().onMessage(client.getWebSocket(), message);
    }

    @Test
    void testOnDiceNumberCommandShouldFireDiceNumberEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.DICENUMBER);
        jsonDataDTO.putData("dicenumber", "8");
        jsonDataDTO.putData("name", "GreenTriangle");
        jsonDataDTO.putData("figure", Figures.GREEN_TRIANGLE.toString());
        jsonDataDTO.putData("location", "1");
        assertEquals(Commands.DICENUMBER, jsonDataDTO.getCommand());
        String message = JsonDataManager.createJsonMessage(jsonDataDTO);
        assertNotNull(message);
        client.getWebSocketListener().onMessage(client.getWebSocket(), message);

        verify(globalEventQueue).enqueueEvent(any(DiceNumberReceivedEvent.class));
    }

    @Test
    void testChangeBalanceCommandShouldFireChangeBalanceEvent() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.CHANGE_BALANCE);
        jsonData.putData("username", "Julia");
        jsonData.putData("new_balance", "1700");
        assertEquals(Commands.CHANGE_BALANCE, jsonData.getCommand());

        String message = JsonDataManager.createJsonMessage(jsonData);
        assert message != null;
        client.getWebSocketListener().onMessage(client.getWebSocket(), message);

        verify(globalEventQueue).enqueueEvent(any(ChangeBalanceEvent.class));
    }

    @Test
    void testCheatingCommandShouldFireCheatingEvent() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.CHEATING);
        assertEquals(Commands.CHEATING, jsonData.getCommand());

        String message = JsonDataManager.createJsonMessage(jsonData);
        assert message != null;
        client.getWebSocketListener().onMessage(client.getWebSocket(), message);

        verify(globalEventQueue).enqueueEvent(any(CheatingEvent.class));
    }
}
