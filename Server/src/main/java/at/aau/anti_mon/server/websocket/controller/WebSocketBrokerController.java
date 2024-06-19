package at.aau.anti_mon.server.websocket.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

/**
 * Controller for the WebSocket Broker
 */
@Controller
public class WebSocketBrokerController {

    @MessageMapping("/hello")
    @SendTo("/topic/hello-response")
    public String handleHello(String message) {
        return "echo from broker: " + HtmlUtils.htmlEscape(message);
    }

}
