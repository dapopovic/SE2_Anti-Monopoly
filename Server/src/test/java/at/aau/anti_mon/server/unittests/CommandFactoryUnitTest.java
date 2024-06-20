package at.aau.anti_mon.server.unittests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import at.aau.anti_mon.server.enums.Commands;
import at.aau.anti_mon.server.commands.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

public class CommandFactoryUnitTest {

    private CommandFactory commandFactory;
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    public void setUp() {
        eventPublisher = mock(ApplicationEventPublisher.class);
        commandFactory = new CommandFactory(eventPublisher);
        commandFactory.init();
    }

    @Test
    public void testGetCommandRandomDice() {
        Command command = commandFactory.getCommand(Commands.RANDOM_DICE.getCommand());
        assertNotNull(command, "Command for RANDOM_DICE should not be null");
        assertInstanceOf(DiceNumberCommand.class, command, "Command for RANDOM_DICE should be instance of DiceNumberCommand");
    }

    @Test
    public void testGetCommandDice() {
        Command command = commandFactory.getCommand(Commands.DICE.getCommand());
        assertNotNull(command, "Command for DICE should not be null");
        assertInstanceOf(DiceNumberCommand.class, command, "Command for DICE should be instance of DiceNumberCommand");
    }

    @Test
    public void testGetCommandNotFound() {
        Command command = commandFactory.getCommand("UNKNOWN_COMMAND");
        assertNull(command, "Command for UNKNOWN_COMMAND should be null");
    }

    @Test
    public void testAllCommandsInitialized() {
        for (Commands cmd : Commands.values()) {
            Command command = commandFactory.getCommand(cmd.getCommand());
            assertNotNull(command, "Command for " + cmd.getCommand() + " should not be null");
        }
    }

    @Test
    public void testLoggingForUnknownCommand() {

        Command command = commandFactory.getCommand("UNKNOWN_COMMAND");

        // Assertions and verifications (for instance loggers)
        // verify(logger).error(contains("Command not found: UNKNOWN_COMMAND"));
        assertNull(command, "Command for UNKNOWN_COMMAND should be null");
    }
}
