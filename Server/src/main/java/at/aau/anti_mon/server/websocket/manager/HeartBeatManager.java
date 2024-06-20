package at.aau.anti_mon.server.websocket.manager;

import at.aau.anti_mon.server.service.SessionManagementService;
import at.aau.anti_mon.server.utilities.MessagingUtility;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.tinylog.Logger;

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

    @Autowired
    public HeartBeatManager(SessionManagementService sessionManagementService) {
        this.sessionManagementService = sessionManagementService;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::sendHeartbeatToAllSessions, 0, 15, TimeUnit.SECONDS);
    }

    //@Scheduled(fixedRate = 25000)
    private void sendHeartbeat(WebSocketSession session) {
        sendHeartBeatMessage(session);
    }

    /**
     * Sends a heartbeat message to all active sessions
     */
    public void sendHeartbeatToAllSessions() {
       // sessionManagementService.getAllSessions().values().forEach(this::sendHeartBeatMessage);

        // TODO: TEST

        // Iterieren über eine Kopie der Sessions, um ConcurrentModificationException zu vermeiden
        new HashMap<>(sessionManagementService.getAllSessions()).forEach((sessionId, session) -> {
            if (session.isOpen()) {
                sendHeartBeatMessage(session);
            } else {
                // Entferne geschlossene Session aus beiden Maps
                sessionManagementService.getAllSessions().remove(sessionId);
                sessionManagementService.getUserSessionMap().entrySet().stream()
                        .filter(entry -> entry.getValue().equals(sessionId))
                        .map(Map.Entry::getKey)
                        .findFirst().ifPresent(userId -> sessionManagementService.getUserSessionMap().remove(userId));
                // Logge die Entfernung der Session
                System.out.println("Session mit ID " + sessionId + " wurde entfernt, da sie geschlossen ist.");
            }
        });

    }

    private void sendHeartBeatMessage(@NotNull WebSocketSession session) {
        MessagingUtility.createHeartbeatMessage().send(session);
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
