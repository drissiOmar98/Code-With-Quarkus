package resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
class OpenApiTest {

    @Test
    void openApiDocument_isExposed() {
        given()
        .when()
            .get("/q/openapi")
        .then()
            .statusCode(200)
            .body(containsString("Sakila Film API"))
            .body(containsString("/films/{filmId}"));
    }
}