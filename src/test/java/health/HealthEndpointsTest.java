package health;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;

@QuarkusTest
class HealthEndpointsTest {

    @Test
    void liveness_isUp() {
        given()
        .when()
            .get("/q/health/live")
        .then()
            .statusCode(200)
            .body("status", equalTo("UP"))
            .body("checks.name", hasItem("Thread deadlock"));
    }

    @Test
    void readiness_isUpAndReportsCatalogSize() {
        given()
        .when()
            .get("/q/health/ready")
        .then()
            .statusCode(200)
            .body("status", equalTo("UP"))
            .body("checks.find { it.name == 'Sakila catalog' }.data.films", equalTo(1000));
    }

    @Test
    void health_aggregatesLivenessAndReadiness() {
        given()
        .when()
            .get("/q/health")
        .then()
            .statusCode(200)
            .body("status", equalTo("UP"))
            .body("checks.name", hasItems("Thread deadlock", "Sakila catalog"));
    }
}