package at.aau.anti_mon.client.unittests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import at.aau.anti_mon.client.command.Commands;
import at.aau.anti_mon.client.command.CreateGameCommand;
import at.aau.anti_mon.client.command.DiceNumberCommand;
import at.aau.anti_mon.client.command.HeartBeatCommand;
import at.aau.anti_mon.client.command.JoinGameCommand;
import at.aau.anti_mon.client.command.LeaveGameCommand;
import at.aau.anti_mon.client.command.NewUserCommand;
import at.aau.anti_mon.client.command.OnReadyCommand;
import at.aau.anti_mon.client.events.DiceNumberReceivedEvent;
import at.aau.anti_mon.client.command.StartGameCommand;
import at.aau.anti_mon.client.events.GlobalEventQueue;
import at.aau.anti_mon.client.events.HeartBeatEvent;
import at.aau.anti_mon.client.events.NextPlayerEvent;
import at.aau.anti_mon.client.game.User;
import at.aau.anti_mon.client.json.JsonDataDTO;
import at.aau.anti_mon.client.json.JsonDataManager;
import at.aau.anti_mon.client.ui.creategame.CreateGameViewModel;
import at.aau.anti_mon.client.ui.gamefield.GameFieldViewModel;
import at.aau.anti_mon.client.ui.lobby.LobbyViewModel;

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

        verify(lobbyViewModel).onUserJoined("testUser", false, false);
    }
    @Test
    void leaveGameCommandShouldFireUserLeftLobbyEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.LEAVE_GAME);
        jsonDataDTO.putData("username", "testUser");
        assertEquals(Commands.LEAVE_GAME, jsonDataDTO.getCommand());

        LeaveGameCommand leaveGameCommand = new LeaveGameCommand(lobbyViewModel);
        leaveGameCommand.execute(jsonDataDTO);

        verify(lobbyViewModel).onUserLeaved("testUser");
    }

    // TODO: Because of use Provider it is now commented out
  /*  @Test
    void pinCommandShouldFireCreateGame() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.PIN);
        jsonDataDTO.putData("pin", "1234");
        assertEquals(Commands.PIN, jsonDataDTO.getCommand());



        PinCommand pinCommand = new PinCommand(createGameViewModel);
        pinCommand.execute(jsonDataDTO);

        verify(createGameViewModel).createGame();
    }

   */
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

        verify(lobbyViewModel).onUserJoined("testUser", true, true);
    }
    @Test
    void newUserCommandShouldFireUserJoinedLobbyEvent() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.NEW_USER);
        jsonDataDTO.putData("username", "testUser");
        assertEquals(Commands.NEW_USER, jsonDataDTO.getCommand());

        JoinGameCommand joinGameCommand = new JoinGameCommand(lobbyViewModel);
        joinGameCommand.execute(jsonDataDTO);

        verify(lobbyViewModel).onUserJoined("testUser", false, false);
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

        verify(lobbyViewModel).onReadyEvent("testUser", true);
    }
    @Test
    void onStartGameCommandShouldFireLobbyViewModelStartGame() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.START_GAME);
        // create users
        User user = new User("username", true, true );
        User user2 = new User("username2", false, false);
        User[] users = {user, user2};
        jsonDataDTO.putData("users", JsonDataManager.createJsonMessage(users));
        assertEquals(Commands.START_GAME, jsonDataDTO.getCommand());

        StartGameCommand startGameCommand = new StartGameCommand(lobbyViewModel);
        startGameCommand.execute(jsonDataDTO);

        verify(lobbyViewModel).startGame(any());
    }

    @Test
    void onNewUserCommandShouldFireLobbyViewModelUserJoined() {
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.NEW_USER);
        jsonDataDTO.putData("username", "testUser");
        jsonDataDTO.putData("isOwner", "false");
        jsonDataDTO.putData("isReady", "false");
        assertEquals(Commands.NEW_USER, jsonDataDTO.getCommand());

        NewUserCommand newUserCommand = new NewUserCommand(lobbyViewModel);
        newUserCommand.execute(jsonDataDTO);

        verify(lobbyViewModel).onUserJoined("testUser", false, false);
    }

    @Test
    void DiceNumberCommandEVENT(){
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.DICENUMBER);
        jsonDataDTO.putData("dicenumber", "8");
        jsonDataDTO.putData("name", "GreenTriangle");
        jsonDataDTO.putData("figure", Figures.GreenTriangle.toString());
        jsonDataDTO.putData("location", "1");
        assertEquals(Commands.DICENUMBER, jsonDataDTO.getCommand());

        GameFieldViewModel gameFieldViewModel = mock(GameFieldViewModel.class);
        DiceNumberCommand diceNumberCommand = new DiceNumberCommand(queue, gameFieldViewModel);
        diceNumberCommand.execute(jsonDataDTO);

        verify(queue).enqueueEvent(any(DiceNumberReceivedEvent.class));
    }

    @Test
    void changeBalanceCommandEnqueueEvent() {
        JsonDataDTO jsonData = new JsonDataDTO();
        jsonData.setCommand(Commands.CHANGE_BALANCE);
        jsonData.putData("username", "Julia");
        jsonData.putData("new_balance", "1700");
        assertEquals(Commands.CHANGE_BALANCE, jsonData.getCommand());
        ChangeBalanceCommand changeBalanceCommand = new ChangeBalanceCommand(queue);
        changeBalanceCommand.execute(jsonData);
        verify(queue).enqueueEvent(any(ChangeBalanceEvent.class));
    }

    @Test
    void NextPlayerCommandEvent(){
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.NEXT_PLAYER);
        jsonDataDTO.putData("username", "user1");
        assertEquals(Commands.NEXT_PLAYER, jsonDataDTO.getCommand());
        NextPlayerCommand nextPlayerCommand = new NextPlayerCommand(queue);
        nextPlayerCommand.execute(jsonDataDTO);
        verify(queue).enqueueEvent(any(NextPlayerEvent.class));
    }
}
