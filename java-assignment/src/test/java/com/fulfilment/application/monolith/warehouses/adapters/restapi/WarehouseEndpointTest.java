package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WarehouseEndpointTest {

    private String uniqueCode(String base) {
        return base + (System.nanoTime() % 100000);
    }

    @Test
    @Order(1)
    void testListWarehouses() {
        given()
                .when().get("/warehouse")
                .then()
                .statusCode(200)
                .body(containsString("MWH.001"),
                        containsString("MWH.012"),
                        containsString("MWH.023"));
    }

    @Test
    @Order(2)
    void testGetWarehouseById() {
        given()
                .when().get("/warehouse/1")
                .then()
                .statusCode(200)
                .body("businessUnitCode", notNullValue());
    }

    @Test
    @Order(3)
    void testGetWarehouseById_notFound() {
        given()
                .when().get("/warehouse/999999")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(4)
    void testCreateWarehouse() {
        // VETSBY-001: max 1 warehouse, max capacity 90 — empty so far
        String code = uniqueCode("MWH.T.");
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                  "businessUnitCode": "%s",
                  "location": "VETSBY-001",
                  "capacity": 10,
                  "stock": 5
                }
                """.formatted(code))
                .when().post("/warehouse")
                .then()
                .statusCode(anyOf(is(200), is(201)))
                .body("businessUnitCode", is(code));
    }

    @Test
    @Order(5)
    void testCreateWarehouse_duplicateCode_shouldFail() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                  "businessUnitCode": "MWH.001",
                  "location": "ZWOLLE-001",
                  "capacity": 10,
                  "stock": 5
                }
                """)
                .when().post("/warehouse")
                .then()
                .statusCode(anyOf(is(400), is(409), is(500)));
    }

    @Test
    @Order(6)
    void testCreateWarehouse_invalidLocation_shouldFail() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                  "businessUnitCode": "%s",
                  "location": "INVALID-999",
                  "capacity": 10,
                  "stock": 5
                }
                """.formatted(uniqueCode("MWH.BAD.")))
                .when().post("/warehouse")
                .then()
                .statusCode(anyOf(is(400), is(500)));
    }

    @Test
    @Order(7)
    void testCreateWarehouse_stockExceedsCapacity_shouldFail() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                  "businessUnitCode": "%s",
                  "location": "HELMOND-001",
                  "capacity": 5,
                  "stock": 99
                }
                """.formatted(uniqueCode("MWH.OVER.")))
                .when().post("/warehouse")
                .then()
                .statusCode(anyOf(is(400), is(500)));
    }

    @Test
    @Order(8)
    void testReplaceWarehouse() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                  "businessUnitCode": "MWH.012",
                  "location": "AMSTERDAM-001",
                  "capacity": 50,
                  "stock": 5
                }
                """)
                .when().post("/warehouse/MWH.012/replacement")
                .then()
                .statusCode(anyOf(is(200), is(201)));
    }

    @Test
    @Order(9)
    void testReplaceWarehouse_notFound() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                  "businessUnitCode": "MWH.999",
                  "location": "AMSTERDAM-001",
                  "capacity": 50,
                  "stock": 5
                }
                """)
                .when().post("/warehouse/MWH.999/replacement")
                .then()
                .statusCode(anyOf(is(400), is(404), is(500)));
    }

    @Test
    @Order(10)
    void testArchiveWarehouse() {
        given()
                .when().delete("/warehouse/3")
                .then()
                .statusCode(anyOf(is(200), is(204)));
    }

    @Test
    @Order(11)
    void testArchiveWarehouse_notFound() {
        given()
                .when().delete("/warehouse/999999")
                .then()
                .statusCode(anyOf(is(400), is(404), is(500)));
    }

    @Test
    @Order(12)
    void testListWarehousesAfterArchive() {
        given()
                .when().get("/warehouse")
                .then()
                .statusCode(200)
                .body(containsString("MWH.012"));
    }
}