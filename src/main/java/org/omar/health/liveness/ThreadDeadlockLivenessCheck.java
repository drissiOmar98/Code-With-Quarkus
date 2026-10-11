package org.omar.health.liveness;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Liveness;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * Liveness: reports DOWN when the JVM has deadlocked threads, a state the
 * application cannot recover from without a restart. Independent of external services.
 */
@Liveness
@ApplicationScoped
public class ThreadDeadlockLivenessCheck implements HealthCheck {

    static final String CHECK_NAME = "Thread deadlock";

    @Override
    public HealthCheckResponse call() {
        ThreadMXBean threads = ManagementFactory.getThreadMXBean();
        long[] deadlocked = threads.findDeadlockedThreads(); // null when none

        HealthCheckResponseBuilder builder = HealthCheckResponse.named(CHECK_NAME)
                .withData("liveThreads", threads.getThreadCount())
                .withData("uptimeSeconds", ManagementFactory.getRuntimeMXBean().getUptime() / 1000);

        return deadlocked == null
                ? builder.up().build()
                : builder.down().withData("deadlockedThreads", deadlocked.length).build();
    }
}