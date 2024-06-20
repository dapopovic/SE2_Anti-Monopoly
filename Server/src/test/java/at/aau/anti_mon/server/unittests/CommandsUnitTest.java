package at.aau.anti_mon.server.unittests;

import at.aau.anti_mon.server.commands.*;
import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.events.*;
import at.aau.anti_mon.server.exceptions.CanNotExecuteJsonCommandException;
import at.aau.anti_mon.server.dtos.JsonDataDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the Commands
 */
@ExtendWith(MockitoExtension.class)
class CommandsUnitTest {

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
    private LobbyReadyCommand lobbyReadyCommand;
    @InjectMocks
    private StartGameCommand startGameCommand;
    @InjectMocks
    private HeartBeatCommand heartBeatCommand;
    @InjectMocks
    private NextPlayerCommand nextPlayerCommand;
    @InjectMocks
    private FirstPlayerCommand firstPlayerCommand;
    @InjectMocks
    private DiceNumberCommand diceNumberCommand;

    @Test
    void diceCommandShouldPublishEvent(){
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.DICE);
        jsonData.putData("dicenumber", "2");
        jsonData.putData("username", "testUser");
        jsonData.putData("pin", "1234");

        diceNumberCommand.execute(session, jsonData);

        verify(eventPublisher, times(1)).publishEvent(any(DiceNumberEvent.class));
    }

    @Test
    void joinLobbyCommandShouldPublishEvent() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.JOIN);
        jsonData.putData("pin", "1234");
        jsonData.putData("username", "testUser");

        joinLobbyCommand.execute(session, jsonData);

        verify(eventPublisher, times(1)).publishEvent(any(UserJoinedLobbyEvent.class));
    }

    @Test
    void joinLobbyCommandHasEmptyValuesAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.JOIN);

        assertThrows(CanNotExecuteJsonCommandException.class, () -> joinLobbyCommand.execute(session, jsonData));
    }

    @Test
    void joinLobbyCommandThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.JOIN);
        jsonData.putData("pin", "");
        jsonData.putData("username", "");

        assertThrows(NumberFormatException.class, () -> joinLobbyCommand.execute(session, jsonData));
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

        assertThrows(CanNotExecuteJsonCommandException.class, () -> leaveLobbyCommand.execute(session, jsonData));
    }

    @Test
    void leaveLobbyCommandHasNoUsernameAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.LEAVE_GAME);
        jsonData.putData("test", "test");

        assertThrows(CanNotExecuteJsonCommandException.class, () -> leaveLobbyCommand.execute(session, jsonData));
    }

    @Test
    void leaveLobbyCommandHasNoPinAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.LEAVE_GAME);
        jsonData.putData("username", "testUser");

        assertThrows(CanNotExecuteJsonCommandException.class, () -> leaveLobbyCommand.execute(session, jsonData));
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

        assertThrows(CanNotExecuteJsonCommandException.class, () -> createGameCommand.execute(session, jsonData));
    }

    @Test
    void createGameCommandHasNoUsernameAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.CREATE_GAME);
        jsonData.putData("test", "test");

        assertThrows(CanNotExecuteJsonCommandException.class, () -> createGameCommand.execute(session, jsonData));
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

        assertThrows(IllegalArgumentException.class, () -> heartBeatCommand.execute(session, jsonData));
    }

    @Test
    void heartBeatCommandHasEmptyValuesAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.HEARTBEAT);

        assertThrows(CanNotExecuteJsonCommandException.class, () -> heartBeatCommand.execute(session, jsonData));
    }

    @Test
    void heartBeatCommandHasNoMsgAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.HEARTBEAT);
        jsonData.putData("username", "testUser");

        assertThrows(CanNotExecuteJsonCommandException.class, () -> heartBeatCommand.execute(session, jsonData));
    }

    @Test
    void heartBeatCommandHasNoSessionAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.HEARTBEAT);
        jsonData.putData("username", "testUser");
        jsonData.putData("msg", "test");
        when(session.getUri()).thenReturn(null);
        assertThrows(CanNotExecuteJsonCommandException.class, () -> heartBeatCommand.execute(session, jsonData));
        verify(eventPublisher, times(0)).publishEvent(any(SessionCheckEvent.class));
        verify(session, times(1)).getUri();
    }

    @Test
    void lobbyReadyCommandShouldPublishEvent() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.READY);
        jsonData.putData("pin", "1234");
        jsonData.putData("username", "testUser");

        lobbyReadyCommand.execute(session, jsonData);

        verify(eventPublisher, times(1)).publishEvent(any(UserReadyLobbyEvent.class));
    }

    @Test
    void lobbyReadyCommandHasEmptyValuesAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.READY);

        assertThrows(CanNotExecuteJsonCommandException.class, () -> lobbyReadyCommand.execute(session, jsonData));
    }

    @Test
    void lobbyReadyCommandHasNoUsernameAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.READY);
        jsonData.putData("test", "test");

        assertThrows(CanNotExecuteJsonCommandException.class, () -> lobbyReadyCommand.execute(session, jsonData));
    }

    @Test
    void lobbyReadyCommandHasNoPinAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.READY);
        jsonData.putData("username", "testUser");

        assertThrows(CanNotExecuteJsonCommandException.class, () -> lobbyReadyCommand.execute(session, jsonData));
    }

    @Test
    void startGameCommandShouldPublishEvent() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.START_GAME);
        jsonData.putData("pin", "1234");
        jsonData.putData("username", "testUser");

        startGameCommand.execute(session, jsonData);

        verify(eventPublisher, times(1)).publishEvent(any(UserStartedGameEvent.class));
    }

    @Test
    void startGameCommandHasEmptyValuesAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.START_GAME);

        assertThrows(CanNotExecuteJsonCommandException.class, () -> startGameCommand.execute(session, jsonData));
    }

    @Test
    void startGameCommandHasNoUsernameAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.START_GAME);
        jsonData.putData("test", "test");

        assertThrows(CanNotExecuteJsonCommandException.class, () -> startGameCommand.execute(session, jsonData));
    }

    @Test
    void startGameCommandHasNoPinAndThrowsException() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.START_GAME);
        jsonData.putData("username", "testUser");

        assertThrows(CanNotExecuteJsonCommandException.class, () -> startGameCommand.execute(session, jsonData));
    }
    @Test
    void diceNumberCommandShouldPublishEvent() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.DICENUMBER);
        jsonData.putData("dicenumber", "2");
        jsonData.putData("username", "testUser");
        jsonData.putData("pin", "1234");

        DiceNumberCommand diceNumberCommand = new DiceNumberCommand(eventPublisher);
        diceNumberCommand.execute(session, jsonData);

        verify(eventPublisher, times(1)).publishEvent(any(DiceNumberEvent.class));
    }

    @Test
    void changeBalanceCommandShouldPublishEvent() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.CHANGE_BALANCE);
        jsonData.putData("username", "Julia");
        jsonData.putData("new_balance", "1700");
        ChangeBalanceCommand changeBalanceCommand = new ChangeBalanceCommand(eventPublisher);
        changeBalanceCommand.execute(session, jsonData);
        verify(eventPublisher, times(1)).publishEvent(any(ChangeBalanceEvent.class));
    }

    @Test
    void nextPlayerCommandShouldPublishEvent(){
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.NEXT_PLAYER);
        jsonData.putData("username", "Alex");
        NextPlayerCommand nextPlayerCommand = new NextPlayerCommand(eventPublisher);
        nextPlayerCommand.execute(session,jsonData);
        verify(eventPublisher,times(1)).publishEvent(any(NextPlayerEvent.class));
    }

    @Test
    void firstPlayerCommandShouldPublishEvent(){
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.FIRST_PLAYER);
        jsonData.putData("username", "Alex");
        FirstPlayerCommand firstPlayerCommand = new FirstPlayerCommand(eventPublisher);
        firstPlayerCommand.execute(session,jsonData);
        verify(eventPublisher,times(1)).publishEvent(any(FirstPlayerEvent.class));
    }
}