package at.aau.anti_mon.server.websocket.configuration;

import at.aau.anti_mon.server.commands.CommandFactory;
import at.aau.anti_mon.server.service.SessionManagementService;
import at.aau.anti_mon.server.websocket.handler.BroadcastWebSocketHandler;
import at.aau.anti_mon.server.websocket.handler.GameHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.tinylog.Logger;

/**
 * Configuration for the WebSocket Handler
 */
@Configuration
//@ConfigurationProperties(prefix = "websockethandler")
@EnableWebSocket
public class WebSocketHandlerConfig implements WebSocketConfigurer {

    private final ApplicationEventPublisher eventPublisher;
    private final CommandFactory gameCommandFactory;
    private final SessionManagementService sessionManagementService;

    @Autowired
    public WebSocketHandlerConfig(
            SessionManagementService sessionManagementService,
            CommandFactory gameCommandFactory,
            ApplicationEventPublisher eventPublisher
    ) {
        this.eventPublisher = eventPublisher;
        this.gameCommandFactory = gameCommandFactory;
        this.sessionManagementService = sessionManagementService;
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        Logger.debug("Register WebSocket handlers");

        registry.addHandler(new GameHandler
                        (
                                //lobbyService,
                        //        sessionManagementService,
                                eventPublisher,
                                gameCommandFactory,
                                sessionManagementService), "/game")
                .setAllowedOrigins("*");

        /*
         * The advantage of using sockJS here is whenever the websocket connection is disconnected or the websocket connection can not be established,
         * then the connection will be downgraded to HTTP and the communication between client and server can still continue.
         */
        registry
                .addHandler(broadcastWebSocketHandler(), "/broadcast")
                .setAllowedOrigins("*")
                .withSockJS()
        ;
    }

    @Bean
    public WebSocketHandler broadcastWebSocketHandler() {
        Logger.debug("Create EchoBroadcastWebSocketHandler");
        return new BroadcastWebSocketHandler();
    }

}

