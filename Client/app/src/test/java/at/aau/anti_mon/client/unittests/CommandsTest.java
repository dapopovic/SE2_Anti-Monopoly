package at.aau.anti_mon.client.unittests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.command.CreateGameCommand;
import at.aau.anti_mon.client.command.HeartBeatCommand;
import at.aau.anti_mon.client.command.JoinGameCommand;
import at.aau.anti_mon.client.command.LeaveGameCommand;
import at.aau.anti_mon.client.command.OnReadyCommand;
import at.aau.anti_mon.client.command.PinCommand;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.HeartBeatEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.viewmodels.CreateGameViewModel;
import at.aau.anti_mon.client.viewmodels.LobbyViewModel;

class CommandsTest {

    @Mock
    GlobalEventQueue queue;
    @Mock
    LobbyViewModel lobbyViewModel;
    @Mock
    CreateGameViewModel createGameViewModel;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void joinGameCommandShouldFireUserJoinedLobbyEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.JOIN_GAME);
        jsonDataDTO.putData("username", "testUser");
        jsonDataDTO.putData("pin", "1234");
        jsonDataDTO.putData("isOwner", "false");
        jsonDataDTO.putData("isReady", "false");
        assertEquals(Commands.JOIN_GAME, jsonDataDTO.getCommand());

        JoinGameCommand joinGameCommand = new JoinGameCommand(lobbyViewModel);
        joinGameCommand.execute(jsonDataDTO);

        verify(lobbyViewModel).userJoined("testUser", false, false);
    }
    @Test
    void leaveGameCommandShouldFireUserLeftLobbyEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.LEAVE_GAME);
        jsonDataDTO.putData("username", "testUser");
        assertEquals(Commands.LEAVE_GAME, jsonDataDTO.getCommand());

        LeaveGameCommand leaveGameCommand = new LeaveGameCommand(lobbyViewModel);
        leaveGameCommand.execute(jsonDataDTO);

        verify(lobbyViewModel).userLeft("testUser");
    }

    @Test
    void pinCommandShouldFireCreateGame() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.PIN);
        jsonDataDTO.putData("pin", "1234");
        assertEquals(Commands.PIN, jsonDataDTO.getCommand());

        PinCommand pinCommand = new PinCommand(createGameViewModel);
        pinCommand.execute(jsonDataDTO);

        verify(createGameViewModel).createGame("1234");
    }
    @Test
    void heartBeatCommandShouldFireHeartBeatEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.HEARTBEAT);
        jsonDataDTO.putData("msg", "testMessage");
        assertEquals(Commands.HEARTBEAT, jsonDataDTO.getCommand());

        HeartBeatCommand heartBeatCommand = new HeartBeatCommand(queue);
        heartBeatCommand.execute(jsonDataDTO);

        verify(queue).enqueueEvent(any(HeartBeatEvent.class));
    }
    @Test
    void createGameCommandShouldFireGameCreatedEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.CREATE_GAME);
        jsonDataDTO.putData("pin", "1234");
        jsonDataDTO.putData("username", "testUser");
        jsonDataDTO.putData("isOwner", "true");
        jsonDataDTO.putData("isReady", "true");
        assertEquals(Commands.CREATE_GAME, jsonDataDTO.getCommand());

        CreateGameCommand createGameCommand = new CreateGameCommand(lobbyViewModel);
        createGameCommand.execute(jsonDataDTO);

        verify(lobbyViewModel).userJoined("testUser", true, true);
    }
    @Test
    void newUserCommandShouldFireUserJoinedLobbyEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.NEW_USER);
        jsonDataDTO.putData("username", "testUser");
        assertEquals(Commands.NEW_USER, jsonDataDTO.getCommand());

        JoinGameCommand joinGameCommand = new JoinGameCommand(lobbyViewModel);
        joinGameCommand.execute(jsonDataDTO);

        verify(lobbyViewModel).userJoined("testUser", false, false);
    }
    @Test
    void onReadyCommandShouldFireLobbyViewModelReady() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.READY);
        jsonDataDTO.putData("username", "testUser");
        jsonDataDTO.putData("isReady", "true");
        assertEquals(Commands.READY, jsonDataDTO.getCommand());

        OnReadyCommand ReadyCommand = new OnReadyCommand(lobbyViewModel);
        ReadyCommand.execute(jsonDataDTO);

        verify(lobbyViewModel).readyUp("testUser", true);
    }

}
