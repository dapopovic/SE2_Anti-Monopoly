package at.aau.anti_mon.server.websocket.configuration;

import at.aau.anti_mon.server.service.LobbyService;
import at.aau.anti_mon.server.websocket.handler.GameHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Configuration for the WebSocket Handler
 */
@Configuration
//@ConfigurationProperties(prefix = "websockethandler")
@EnableWebSocket
public class WebSocketHandlerConfig implements WebSocketConfigurer {

    private final LobbyService lobbyService;
    //private final SessionManagementService sessionManagementService;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public WebSocketHandlerConfig(
            LobbyService lobbyService,
    //        SessionManagementService sessionManagementService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.lobbyService = lobbyService;
    //    this.sessionManagementService = sessionManagementService;
        this.eventPublisher = eventPublisher;
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new GameHandler
                        (
                                lobbyService,
                        //        sessionManagementService,
                                eventPublisher
                        ), "/game")
                .setAllowedOrigins("*");
               // .withSockJS()
               // .setHeartbeatTime(25000);
    }


}

