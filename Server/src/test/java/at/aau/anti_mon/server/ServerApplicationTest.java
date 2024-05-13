package at.aau.anti_mon.server;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

/**
 * Simple sanity check test that will fail if the application context cannot start.
 * @see <a href="https://spring.io/guides/gs/testing-web">Spring Testing Web</a>
 */
@SpringBootTest
@ActiveProfiles("test")
class ServerApplicationTest {

        @Test
        void contextLoads() {
        }

}
