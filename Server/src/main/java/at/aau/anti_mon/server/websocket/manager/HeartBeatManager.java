package at.aau.anti_mon.server.websocket.manager;

import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.dtos.JsonDataDTO;
import at.aau.anti_mon.server.service.SessionManagementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * TODO: TEST
 */
@Component
public class HeartBeatManager {

    private final SessionManagementService sessionManagementService;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public HeartBeatManager(SessionManagementService sessionManagementService) {
        this.sessionManagementService = sessionManagementService;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::sendHeartbeatToAllSessions, 0, 10, TimeUnit.SECONDS);
    }

    //@Scheduled(fixedRate = 25000)
    private void sendHeartbeat(WebSocketSession session) {
        sendHeartBeatMessage(session);
    }

    /**
     * Sends a heartbeat message to all active sessions
     */
    public void sendHeartbeatToAllSessions() {
        sessionManagementService.getAllSessions().values().forEach(this::sendHeartBeatMessage);
    }

    private void sendHeartBeatMessage(@NotNull WebSocketSession session) {
        try {
            Map<String, String> dataMap = new HashMap<>();
            JsonDataDTO jsonData = new JsonDataDTO(Commands.HEARTBEAT, dataMap);
            jsonData.putData("msg", "PING");
            ObjectMapper mapper = new ObjectMapper();
            String jsonMessage = mapper.writeValueAsString(jsonData);
            TextMessage heartbeatMessage = new TextMessage(jsonMessage);

            if (session.isOpen()) {
                session.sendMessage(heartbeatMessage);
            }
        } catch (IOException e) {
            Logger.error("Error sending heartbeat to session " + session.getId());
        }
    }


    public void stop() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // Set the interrupt flag
            Logger.error("Failed to stop scheduler properly", e);
        }
    }
}
