package health;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.junit.jupiter.api.Test;
import org.omar.health.liveness.ThreadDeadlockLivenessCheck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThreadDeadlockLivenessCheckTest {

    @Test
    void call_withoutDeadlock_isUp() {
        HealthCheckResponse response = new ThreadDeadlockLivenessCheck().call();

        assertEquals(HealthCheckResponse.Status.UP, response.getStatus());
        assertEquals("Thread deadlock", response.getName());
        assertTrue(response.getData().orElseThrow().containsKey("liveThreads"));
    }
}