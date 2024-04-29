package at.aau.anti_mon.server.unittests;

import at.aau.anti_mon.server.commands.CreateGameCommand;
import at.aau.anti_mon.server.commands.HeartBeatCommand;
import at.aau.anti_mon.server.commands.JoinLobbyCommand;
import at.aau.anti_mon.server.commands.LeaveLobbyCommand;
import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.events.UserCreatedLobbyEvent;
import at.aau.anti_mon.server.events.SessionCheckEvent;
import at.aau.anti_mon.server.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.server.events.UserLeftLobbyEvent;
import at.aau.anti_mon.server.exceptions.CanNotExecuteJsonCommandException;
import at.aau.anti_mon.server.game.JsonDataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CommandsTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private WebSocketSession session;

    @InjectMocks
    private JoinLobbyCommand joinLobbyCommand;

    @InjectMocks
    private LeaveLobbyCommand leaveLobbyCommand;

    @InjectMocks
    private CreateGameCommand createGameCommand;

    @InjectMocks
    private HeartBeatCommand heartBeatCommand;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void joinLobbyCommandShouldPublishEvent() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.JOIN_GAME);
        jsonData.putData("pin", "1234");
        jsonData.putData("username", "testUser");

        joinLobbyCommand.execute(session, jsonData);

        verify(eventPublisher, times(1)).publishEvent(any(UserJoinedLobbyEvent.class));
    }

    @Test
    void joinLobbyCommandHasEmptyValuesAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.JOIN_GAME);

        assertThrows(CanNotExecuteJsonCommandException.class, () -> {
            joinLobbyCommand.execute(session, jsonData);
        });
    }

    @Test
    void joinLobbyCommandThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.JOIN_GAME);
        jsonData.putData("pin", "");
        jsonData.putData("username", "");

        assertThrows(NumberFormatException.class, () -> {
            joinLobbyCommand.execute(session, jsonData);
        });
    }

    @Test
    void leaveLobbyCommandShouldPublishEvent() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.LEAVE_GAME);
        jsonData.putData("pin", "1234");
        jsonData.putData("username", "testUser");

        leaveLobbyCommand.execute(session, jsonData);

        verify(eventPublisher, times(1)).publishEvent(any(UserLeftLobbyEvent.class));
    }

    @Test
    void leaveLobbyCommandHasEmptyValuesAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.LEAVE_GAME);

        assertThrows(CanNotExecuteJsonCommandException.class, () -> {
            leaveLobbyCommand.execute(session, jsonData);
        });
    }

    @Test
    void leaveLobbyCommandHasNoUsernameAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.LEAVE_GAME);
        jsonData.putData("test", "test");

        assertThrows(CanNotExecuteJsonCommandException.class, () -> {
            leaveLobbyCommand.execute(session, jsonData);
        });
    }

    @Test
    void leaveLobbyCommandHasNoPinAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.LEAVE_GAME);
        jsonData.putData("username", "testUser");

        assertThrows(CanNotExecuteJsonCommandException.class, () -> {
            leaveLobbyCommand.execute(session, jsonData);
        });
    }

    @Test
    void createGameCommandShouldPublishEvent() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.CREATE_GAME);
        jsonData.putData("username", "testUser");

        createGameCommand.execute(session, jsonData);

        verify(eventPublisher, times(1)).publishEvent(any(UserCreatedLobbyEvent.class));
    }

    @Test
    void createGameCommandHasEmptyValuesAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.CREATE_GAME);

        assertThrows(CanNotExecuteJsonCommandException.class, () -> {
            createGameCommand.execute(session, jsonData);
        });
    }

    @Test
    void createGameCommandHasNoUsernameAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.CREATE_GAME);
        jsonData.putData("test", "test");

        assertThrows(CanNotExecuteJsonCommandException.class, () -> {
            createGameCommand.execute(session, jsonData);
        });
    }

    @Test
    void HeartBeatCommandShouldExecuteSessionCheckEvent() throws URISyntaxException {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.HEARTBEAT);
        jsonData.putData("username", "testUser");
        jsonData.putData("msg", "test");

        when(session.getUri()).thenReturn(new URI("ws://localhost:8080/game?userID=testUser"));

        heartBeatCommand.execute(session, jsonData);

        verify(eventPublisher, times(1)).publishEvent(any(SessionCheckEvent.class));
    }

    @Test
    void HeartBeatCommandShouldThrowException() throws URISyntaxException {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.HEARTBEAT);
        jsonData.putData("username", "testUser");
        jsonData.putData("msg", "test");

        when(session.getUri()).thenReturn(new URI("ws://localhost:8080/game?udiDOASDnk"));

        assertThrows(IllegalArgumentException.class, () -> {
            heartBeatCommand.execute(session, jsonData);
        });
    }

    @Test
    void heartBeatCommandHasEmptyValuesAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.HEARTBEAT);

        assertThrows(CanNotExecuteJsonCommandException.class, () -> {
            heartBeatCommand.execute(session, jsonData);
        });
    }

    @Test
    void heartBeatCommandHasNoMsgAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.HEARTBEAT);
        jsonData.putData("username", "testUser");

        assertThrows(CanNotExecuteJsonCommandException.class, () -> {
            heartBeatCommand.execute(session, jsonData);
        });
    }

    @Test
    void heartBeatCommandHasNoSessionAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.HEARTBEAT);
        jsonData.putData("username", "testUser");
        jsonData.putData("msg", "test");
        when(session.getUri()).thenReturn(null);
        assertThrows(CanNotExecuteJsonCommandException.class, () -> {
            heartBeatCommand.execute(session, jsonData);
        });
        verify(eventPublisher, times(0)).publishEvent(any(SessionCheckEvent.class));
        verify(session, times(1)).getUri();
    }
}