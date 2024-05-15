package at.aau.anti_mon.server.database;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Base class for database tests.
 * This class starts a PostgreSQL test-container and sets the database properties for the tests.
 * Singleton class, only one instance of the database is started for all tests.
 * <a href="https://java.testcontainers.org/test_framework_integration/manual_lifecycle_control/#singleton-containers">Source</a>
 */
public abstract class TestDatabase {

    static final PostgreSQLContainer DATABASE = new PostgreSQLContainer("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    static {
        DATABASE.start();
    }

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", DATABASE::getJdbcUrl);
        registry.add("spring.datasource.username", DATABASE::getUsername);
        registry.add("spring.datasource.password", DATABASE::getPassword);


    }
}
