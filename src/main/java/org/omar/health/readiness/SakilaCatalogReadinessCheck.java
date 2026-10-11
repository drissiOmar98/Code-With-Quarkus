package org.omar.health.readiness;

import io.smallrye.common.annotation.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import org.eclipse.microprofile.health.Readiness;
import org.jboss.logging.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Readiness: the application can serve traffic only if the Sakila catalog
 * is reachable and contains films.
 */
@Readiness
@ApplicationScoped
public class SakilaCatalogReadinessCheck implements HealthCheck {

    private static final Logger LOG = Logger.getLogger(SakilaCatalogReadinessCheck.class);

    static final String CHECK_NAME = "Sakila catalog";
    private static final String COUNT_FILMS_SQL = "SELECT COUNT(*) FROM sakila.film";

    private final DataSource dataSource;

    public SakilaCatalogReadinessCheck(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    @Blocking // JDBC call: must not run on the event loop
    public HealthCheckResponse call() {
        HealthCheckResponseBuilder builder = HealthCheckResponse.named(CHECK_NAME);

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(COUNT_FILMS_SQL)) {

            resultSet.next();
            long films = resultSet.getLong(1);

            return films > 0
                    ? builder.up().withData("films", films).build()
                    : builder.down().withData("reason", "film table is empty").build();

        } catch (SQLException e) {
            // Details go to the logs, not to the public health endpoint
            LOG.warn("Readiness check failed: cannot query the Sakila catalog", e);
            return builder.down().withData("reason", "database unreachable").build();
        }
    }
}