package com.fulfilment.application.monolith.stores;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StoreResourceTest {

    // Helper: generates a unique store name every time
    private String name(String base) {
        return base + "_" + System.nanoTime();
    }

    private String body(String name, int qty) {
        return """
                {
                  "name": "%s",
                  "quantityProductsInStock": %d
                }
                """.formatted(name, qty);
    }

    // ── CREATE ────────────────────────────────────────────────────────────────

    @Test
    @Order(1)
    void testCreateStore() {
        String storeName = name("Test Store");
        given()
                .contentType(ContentType.JSON)
                .body(body(storeName, 10))
                .when().post("/store")
                .then()
                .statusCode(201)
                .body("name", is(storeName))
                .body("quantityProductsInStock", is(10));
    }

    @Test
    @Order(2)
    void testCreateStore_withId_shouldFail() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                  "id": 999,
                  "name": "Bad Store"
                }
            """)
                .when().post("/store")
                .then()
                .statusCode(422)
                .body("error", containsString("Id was invalidly set"));
    }

    // ── LIST ─────────────────────────────────────────────────────────────────

    @Test
    @Order(3)
    void testListStores() {
        given()
                .when().get("/store")
                .then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    // ── GET SINGLE ────────────────────────────────────────────────────────────

    @Test
    @Order(4)
    void testGetSingleStore() {
        String storeName = name("Single Store");
        int id = given()
                .contentType(ContentType.JSON)
                .body(body(storeName, 5))
                .when().post("/store")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .when().get("/store/" + id)
                .then()
                .statusCode(200)
                .body("name", is(storeName));
    }

    @Test
    @Order(5)
    void testGetSingleStore_notFound() {
        given()
                .when().get("/store/999999")
                .then()
                .statusCode(404)
                .body("error", containsString("does not exist"));
    }

    // ── PUT ───────────────────────────────────────────────────────────────────

    @Test
    @Order(6)
    void testUpdateStore() {
        String updatedName = name("Updated Store");
        int id = given()
                .contentType(ContentType.JSON)
                .body(body(name("Update Me"), 3))
                .when().post("/store")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .contentType(ContentType.JSON)
                .body(body(updatedName, 99))
                .when().put("/store/" + id)
                .then()
                .statusCode(200)
                .body("name", is(updatedName))
                .body("quantityProductsInStock", is(99));
    }

    @Test
    @Order(7)
    void testUpdateStore_noName_shouldFail() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                  "quantityProductsInStock": 5
                }
            """)
                .when().put("/store/1")
                .then()
                .statusCode(422)
                .body("error", containsString("Store Name was not set"));
    }

    @Test
    @Order(8)
    void testUpdateStore_notFound() {
        given()
                .contentType(ContentType.JSON)
                .body(body(name("Ghost Store"), 1))
                .when().put("/store/999999")
                .then()
                .statusCode(404)
                .body("error", containsString("does not exist"));
    }

    // ── PATCH ─────────────────────────────────────────────────────────────────

    @Test
    @Order(9)
    void testPatchStore() {
        String patchedName = name("Patched Store");
        int id = given()
                .contentType(ContentType.JSON)
                .body(body(name("Patch Me"), 7))
                .when().post("/store")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .contentType(ContentType.JSON)
                .body(body(patchedName, 77))
                .when().patch("/store/" + id)
                .then()
                .statusCode(200)
                .body("name", is(patchedName));
    }

    @Test
    @Order(10)
    void testPatchStore_noName_shouldFail() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                  "quantityProductsInStock": 5
                }
            """)
                .when().patch("/store/1")
                .then()
                .statusCode(422)
                .body("error", containsString("Store Name was not set"));
    }

    @Test
    @Order(11)
    void testPatchStore_notFound() {
        given()
                .contentType(ContentType.JSON)
                .body(body(name("Nobody"), 1))
                .when().patch("/store/999999")
                .then()
                .statusCode(404)
                .body("error", containsString("does not exist"));
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Test
    @Order(12)
    void testDeleteStore() {
        int id = given()
                .contentType(ContentType.JSON)
                .body(body(name("Delete Me"), 0))
                .when().post("/store")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .when().delete("/store/" + id)
                .then()
                .statusCode(204);
    }

    @Test
    @Order(13)
    void testDeleteStore_notFound() {
        given()
                .when().delete("/store/999999")
                .then()
                .statusCode(404)
                .body("error", containsString("does not exist"));
    }
}