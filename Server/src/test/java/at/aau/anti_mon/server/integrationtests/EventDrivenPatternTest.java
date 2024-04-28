package at.aau.anti_mon.server.integrationtests;

import at.aau.anti_mon.server.commands.JoinLobbyCommand;
import at.aau.anti_mon.server.commands.LeaveLobbyCommand;
import at.aau.anti_mon.server.dtos.LobbyDTO;
import at.aau.anti_mon.server.dtos.UserDTO;
import at.aau.anti_mon.server.events.UserJoinedLobbyEvent;
import at.aau.anti_mon.server.events.UserLeftLobbyEvent;
import at.aau.anti_mon.server.exceptions.LobbyIsFullException;
import at.aau.anti_mon.server.exceptions.LobbyNotFoundException;
import at.aau.anti_mon.server.exceptions.UserNotFoundException;
import at.aau.anti_mon.server.game.JsonDataDTO;
import at.aau.anti_mon.server.game.Lobby;
import at.aau.anti_mon.server.game.User;
import at.aau.anti_mon.server.listener.UserEventListener;
import at.aau.anti_mon.server.service.LobbyService;
import at.aau.anti_mon.server.service.SessionManagementService;
import at.aau.anti_mon.server.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.WebSocketSession;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import at.aau.anti_mon.server.enums.*;

import static org.mockito.Mockito.*;

@SpringBootTest
public class EventDrivenPatternTest {


    @Mock
    UserEventListener userEventListener;
    @Autowired
    LobbyService lobbyService;
    @Autowired
    SessionManagementService sessionManagementService;

    @Mock
    ApplicationEventPublisher eventPublisher;
    @Autowired
    UserService userService;

    @Mock
    WebSocketSession session1;

    @Mock
    WebSocketSession session2;

    User user1;
    User user2;
    Lobby lobby;
    JoinLobbyCommand joinLobbyCommand;
    LeaveLobbyCommand leaveLobbyCommand;


    @BeforeEach
    public void init() throws URISyntaxException {

        when(session1.isOpen()).thenReturn(true);
        when(session1.getRemoteAddress()).thenReturn(new InetSocketAddress(1234));
        when(session1.getId()).thenReturn("session1");
        when(session1.getAcceptedProtocol()).thenReturn("protocol");
        when(session1.getHandshakeHeaders()).thenReturn(new HttpHeaders());
        when(session1.getUri()).thenReturn(new URI("ws://localhost:8080/game?userID=user1"));

        session2 = mock(WebSocketSession.class);

        when(session2.isOpen()).thenReturn(true);
        when(session2.getRemoteAddress()).thenReturn(new InetSocketAddress(1234));
        when(session2.getId()).thenReturn("session2");
        when(session2.getAcceptedProtocol()).thenReturn("protocol");
        when(session2.getHandshakeHeaders()).thenReturn(new HttpHeaders());
        when(session2.getUri()).thenReturn(new URI("ws://localhost:8080/game?userID=user2"));

        user1 = userService.findOrCreateUser("user1", session1);
        user2 = userService.findOrCreateUser("user2", session2);
        sessionManagementService.registerUserWithSession(user1.getName(), session1);
        sessionManagementService.registerUserWithSession(user2.getName(), session2);
        lobby = lobbyService.createLobby(user1);

    }

    @Test
    public void onUserJoinedLobbyEventShouldCallCorrectServiceMethod() throws UserNotFoundException, LobbyNotFoundException, LobbyIsFullException {

        // Given
        UserJoinedLobbyEvent event = new UserJoinedLobbyEvent(session2, new LobbyDTO(lobby.getPin()), new UserDTO("user2"));
        joinLobbyCommand = mock(JoinLobbyCommand.class);
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.JOIN_GAME);
        jsonDataDTO.putData("username","user2");
        jsonDataDTO.putData("pin",lobby.getPin().toString());

        // Mock Event Handling
        doAnswer((Answer<Void>) invocation -> {
            eventPublisher.publishEvent(event);
            return null;
        }).when(joinLobbyCommand).execute(session2,jsonDataDTO);

        doAnswer((Answer<Void>) invocation -> {
            userEventListener.onUserJoinedLobbyEvent(invocation.getArgument(0));
            return null;
        }).when(eventPublisher).publishEvent(event);

        // When JsonCommand is executed
        joinLobbyCommand.execute(session2,jsonDataDTO);

        // Then
        verify(userEventListener, times(1)).onUserJoinedLobbyEvent(event);
    }

    @Test
    public void onLeaveLobbyEventShouldCallCorrectServiceMethod() throws UserNotFoundException, LobbyNotFoundException {

        session1 = mock(WebSocketSession.class);
        lobbyService.addUserToLobby( user2.getName(),lobby.getPin());
        UserLeftLobbyEvent event = new UserLeftLobbyEvent(session1, new LobbyDTO(lobby.getPin()), new UserDTO("user1"));
        leaveLobbyCommand = mock(LeaveLobbyCommand.class);
        JsonDataDTO jsonDataDTO = new JsonDataDTO();
        jsonDataDTO.setCommand(Commands.LEAVE_GAME);
        jsonDataDTO.putData("username","user1");
        jsonDataDTO.putData("pin",lobby.getPin().toString());


        // Mock Event Handling
        doAnswer((Answer<Void>) invocation -> {
            eventPublisher.publishEvent(event);
            return null;
        }).when(leaveLobbyCommand).execute(session1, jsonDataDTO);

        doAnswer((Answer<Void>) invocation -> {
            userEventListener.onLeaveLobbyEvent(invocation.getArgument(0));
            return null;
        }).when(eventPublisher).publishEvent(event);

        // When JsonCommand is executed
        leaveLobbyCommand.execute(session1,jsonDataDTO);

        // Then
        verify(userEventListener, times(1)).onLeaveLobbyEvent(event);
    }




}