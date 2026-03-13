package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusIntegrationTest
class WarehouseEndpointIT {

    @Test
    void testListWarehouses() {
        given()
                .when()
                .get("/warehouse")
                .then()
                .statusCode(200)
                .body(
                        containsString("MWH.001"),
                        containsString("MWH.012"),
                        containsString("MWH.023")
                );
    }

    @Test
    void testArchiveWarehouseFlow() {
        // 1. Verify initial list contains all warehouses
        given()
                .when()
                .get("/warehouse")
                .then()
                .statusCode(200)
                .body(
                        containsString("MWH.001"),
                        containsString("MWH.012"),
                        containsString("MWH.023")
                );

        // 2. Archive warehouse with ID 1
        given()
                .when()
                .delete("/warehouse/1")
                .then()
                .statusCode(204);

        // 3. Verify warehouse 1 is no longer returned
        given()
                .when()
                .get("/warehouse")
                .then()
                .statusCode(200)
                .body(
                        // archived warehouse should be gone
                        org.hamcrest.Matchers.not(containsString("MWH.001")),
                        // others should still be present
                        containsString("MWH.012"),
                        containsString("MWH.023")
                );
    }
}