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
import at.aau.anti_mon.client.dependencyinjection.AppModule;
import at.aau.anti_mon.client.dependencyinjection.DaggerAppComponent;
import at.aau.anti_mon.client.enums.Commands;
import at.aau.anti_mon.client.enums.Figures;
import at.aau.anti_mon.client.events.ChangeBalanceEvent;
import at.aau.anti_mon.client.events.CheatingEvent;
import at.aau.anti_mon.client.events.DiceNumberReceivedEvent;
import at.aau.anti_mon.client.networking.WebSocketClientListener;
import at.aau.anti_mon.client.ui.gameboard.GameBoardViewModel;
import at.aau.anti_mon.client.utilities.GlobalEventQueue;
import at.aau.anti_mon.client.events.HeartBeatEvent;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.utilities.JsonDataUtility;
import at.aau.anti_mon.client.networking.WebSocketClient;
import at.aau.anti_mon.client.ui.creategame.CreateGameViewModel;
import at.aau.anti_mon.client.ui.lobby.LobbyViewModel;

class WebSocketClientTest extends AntiMonopolyApplication {
    @Inject
    WebSocketClient client;
    @Mock
    GlobalEventQueue globalEventQueue;

    @Mock
    GameBoardViewModel gameBoardViewModel
            ;
    @Inject
    CreateGameViewModel createGameViewModel;
    @Inject
    LobbyViewModel lobbyViewModel;

    @Inject
    WebSocketClientListener webSocketClientListener;

    @BeforeEach
    void init() {
        openMocks(this);
        appComponent = DaggerAppComponent.factory().create(new AppModule(this));
        appComponent.inject(this);

        Map<String, Command> commandMap = new HashMap<>();
        commandMap.put("PIN", new PinCommand(createGameViewModel));
        commandMap.put("HEARTBEAT", new HeartBeatCommand(globalEventQueue));
        commandMap.put("NEW_USER", new NewUserCommand(lobbyViewModel));
        commandMap.put("START_GAME", new StartGameCommand(lobbyViewModel));
        commandMap.put("LEAVE_GAME", new LeaveGameCommand(lobbyViewModel));
        commandMap.put("READY", new OnReadyCommand(lobbyViewModel));
        commandMap.put("DICENUMBER", new DiceNumberCommand(gameBoardViewModel));
        commandMap.put("CHANGE_BALANCE", new ChangeBalanceCommand(gameBoardViewModel));
        commandMap.put("CHEATING", new CheatingCommand(gameBoardViewModel));
    }

    @Test
    void testNewCreateGameCommandAndGetPin() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.PIN);
        jsonDataDTO.putData("pin", "1234");
        String message = JsonDataUtility.createJsonMessage(jsonDataDTO);
        assertNotNull(message);
        webSocketClientListener.onMessage(client.getWebSocket(), message);
    }

    @Test
    void testHeartBeatCommandReceivesHeartBeatEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.HEARTBEAT);
        jsonDataDTO.putData("msg", "test");
        String message = JsonDataUtility.createJsonMessage(jsonDataDTO);
        assertNotNull(message);
        webSocketClientListener.onMessage(client.getWebSocket(), message);
        verify(globalEventQueue).enqueueEvent(any(HeartBeatEvent.class));
    }

    @Test
    void testNewUserCommandShouldFireUserJoinedLobbyEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.NEW_USER);
        jsonDataDTO.putData("username", "testUser");
        jsonDataDTO.putData("isOwner", "false");
        jsonDataDTO.putData("isReady", "false");
        String message = JsonDataUtility.createJsonMessage(jsonDataDTO);
        assertNotNull(message);
        webSocketClientListener.onMessage(client.getWebSocket(), message);
    }

    @Test
    void testLeaveCommandShouldFireUserLeftLobbyEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.LEAVE_GAME);
        jsonDataDTO.putData("username", "testUser");
        String message = JsonDataUtility.createJsonMessage(jsonDataDTO);
        assertNotNull(message);
        webSocketClientListener.onMessage(client.getWebSocket(), message);
    }
    @Test
    void testStartGameCommandShouldFireGameStartedEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.START_GAME);
        User[] users = {

                new User.UserBuilder("testUser", false, false)
                        .playerMoney(1000)
                        .playerFigure(Figures.GREEN_CIRCLE)
                        .build(),
                new User.UserBuilder("testUser2", false, false)
                        .playerMoney(1000)
                        .playerFigure(Figures.BLUE_CIRCLE)
                        .build()
        };
        jsonDataDTO.putData("users", JsonDataUtility.createJsonMessage(users));
        String message = JsonDataUtility.createJsonMessage(jsonDataDTO);
        assertNotNull(message);
        webSocketClientListener.onMessage(client.getWebSocket(), message);
    }
    @Test
    void testOnReadyCommandShouldFireReadyEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.READY);
        jsonDataDTO.putData("username", "testUser");
        jsonDataDTO.putData("isReady", "true");
        String message = JsonDataUtility.createJsonMessage(jsonDataDTO);
        assertNotNull(message);
        webSocketClientListener.onMessage(client.getWebSocket(), message);
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
        String message = JsonDataUtility.createJsonMessage(jsonDataDTO);
        assertNotNull(message);
        webSocketClientListener.onMessage(client.getWebSocket(), message);

        verify(globalEventQueue).enqueueEvent(any(DiceNumberReceivedEvent.class));
    }

    @Test
    void testChangeBalanceCommandShouldFireChangeBalanceEvent() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.CHANGE_BALANCE);
        jsonData.putData("username", "Julia");
        jsonData.putData("new_balance", "1700");
        assertEquals(Commands.CHANGE_BALANCE, jsonData.getCommand());

        String message = JsonDataUtility.createJsonMessage(jsonData);
        assert message != null;
        webSocketClientListener.onMessage(client.getWebSocket(), message);

        verify(globalEventQueue).enqueueEvent(any(ChangeBalanceEvent.class));
    }

    @Test
    void testCheatingCommandShouldFireCheatingEvent() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.CHEATING);
        assertEquals(Commands.CHEATING, jsonData.getCommand());

        String message = JsonDataUtility.createJsonMessage(jsonData);
        assert message != null;
        webSocketClientListener.onMessage(client.getWebSocket(), message);

        verify(globalEventQueue).enqueueEvent(any(CheatingEvent.class));
    }
}
