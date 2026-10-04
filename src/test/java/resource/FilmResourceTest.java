package resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class FilmResourceTest {

    // ---------- GET /films/{filmId} ----------

    @Test
    void getFilm_returnsFilm() {
        given()
        .when()
            .get("/films/{filmId}", 1)
        .then()
            .statusCode(200)
            .contentType("application/json")
            .body("id", equalTo(1))
            .body("title", equalTo("ACADEMY DINOSAUR"))
            .body("length", equalTo(86))
            .body("rentalRate", equalTo(0.99f));
    }

    @Test
    void getFilm_unknownId_returns404WithErrorBody() {
        given()
        .when()
            .get("/films/{filmId}", 9999)
        .then()
            .statusCode(404)
            .contentType("application/json")
            .body("status", equalTo(404))
            .body("error", equalTo("Not Found"))
            .body("message", equalTo("No film found with id 9999"))
            .body("path", equalTo("/films/9999"));
    }

    // ---------- GET /films ----------

    @Test
    void getFilms_filtersByMinLength() {
        given()
            .queryParam("minLength", 180)
        .when()
            .get("/films")
        .then()
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("length", everyItem(greaterThan(180)));
    }

    @Test
    void getFilms_returnsTwentyFilmsPerPage() {
        given()
            .queryParam("page", 0)
            .queryParam("minLength", 0)
        .when()
            .get("/films")
        .then()
            .statusCode(200)
            .body("size()", equalTo(20));
    }

    @Test
    void getFilms_isSortedByLength() {
        List<Integer> lengths = given()
            .queryParam("minLength", 0)
        .when()
            .get("/films")
        .then()
            .statusCode(200)
            .extract().jsonPath().getList("length", Integer.class);

        List<Integer> sorted = new ArrayList<>(lengths);
        Collections.sort(sorted);
        assertEquals(sorted, lengths);
    }

    @Test
    void getFilms_negativeMinLength_returns400() {
        given()
            .queryParam("minLength", -1)
        .when()
            .get("/films")
        .then()
            .statusCode(400);
    }

    // ---------- GET /films/with-actors ----------

    @Test
    void getFilmsWithActors_returnsCast() {
        given()
            .queryParam("titlePrefix", "ACADEMY")
            .queryParam("minLength", 0)
        .when()
            .get("/films/with-actors")
        .then()
            .statusCode(200)
            .body("[0].title", equalTo("ACADEMY DINOSAUR"))
            .body("[0].actors.size()", greaterThan(0))
            .body("[0].actors[0].firstName", notNullValue());
    }

    // ---------- PUT /films/rental-rate (validation only) ----------

    @Test
    void updateRentalRate_missingParameters_returns400() {
        given()
        .when()
            .put("/films/rental-rate")
        .then()
            .statusCode(400);
    }

    @Test
    void updateRentalRate_tooManyDecimals_returns400() {
        given()
            .queryParam("minLength", 100)
            .queryParam("rentalRate", "2.999")
        .when()
            .put("/films/rental-rate")
        .then()
            .statusCode(400);
    }
}