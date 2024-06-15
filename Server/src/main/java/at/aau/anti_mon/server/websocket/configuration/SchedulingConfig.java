package at.aau.anti_mon.server.websocket.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * Configuration for the Scheduler
 */
@Configuration
//@ConfigurationProperties(prefix = "scheduler")
@EnableScheduling
public class SchedulingConfig {
}
