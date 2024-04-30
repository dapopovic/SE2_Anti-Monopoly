package at.aau.anti_mon.client.unittests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.util.Log;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.command.CreateGameCommand;
import at.aau.anti_mon.client.command.HeartBeatCommand;
import at.aau.anti_mon.client.command.JoinGameCommand;
import at.aau.anti_mon.client.command.LeaveGameCommand;
import at.aau.anti_mon.client.command.PinCommand;
import at.aau.anti_mon.client.events.CreatedGameEvent;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.HeartBeatEvent;
import at.aau.anti_mon.client.events.PinReceivedEvent;
import at.aau.anti_mon.client.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.client.events.UserLeftLobbyEvent;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.viewmodels.LobbyViewModel;

class CommandsTest {

    @Mock
    GlobalEventQueue queue;
    @Mock
    LobbyViewModel viewModel;

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

        JoinGameCommand joinGameCommand = new JoinGameCommand(queue, viewModel);
        joinGameCommand.execute(jsonDataDTO);

        verify(queue).enqueueEvent(any(UserJoinedLobbyEvent.class));
        verify(viewModel).userJoined("testUser");
    }
    @Test
    void leaveGameCommandShouldFireUserLeftLobbyEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.LEAVE_GAME);
        jsonDataDTO.putData("username", "testUser");

        LeaveGameCommand leaveGameCommand = new LeaveGameCommand(queue, viewModel);
        leaveGameCommand.execute(jsonDataDTO);

        verify(queue).enqueueEvent(any(UserLeftLobbyEvent.class));
        verify(viewModel).userLeft("testUser");
    }

    @Test
    void pinCommandShouldFirePinReceivedEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.PIN);
        jsonDataDTO.putData("pin", "1234");

        PinCommand pinCommand = new PinCommand(queue);
        pinCommand.execute(jsonDataDTO);

        verify(queue).enqueueEvent(any(PinReceivedEvent.class));
    }
    @Test
    void heartBeatCommandShouldFireHeartBeatEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.HEARTBEAT);
        jsonDataDTO.putData("msg", "testMessage");

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

        CreateGameCommand createGameCommand = new CreateGameCommand(queue, viewModel);
        createGameCommand.execute(jsonDataDTO);

        verify(queue).enqueueEvent(any(CreatedGameEvent.class));
        verify(viewModel).userJoined("testUser");
    }
    @Test
    void newUserCommandShouldFireUserJoinedLobbyEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.NEW_USER);
        jsonDataDTO.putData("username", "testUser");

        JoinGameCommand joinGameCommand = new JoinGameCommand(queue, viewModel);
        joinGameCommand.execute(jsonDataDTO);

        verify(queue).enqueueEvent(any(UserJoinedLobbyEvent.class));
        verify(viewModel).userJoined("testUser");
    }

}
