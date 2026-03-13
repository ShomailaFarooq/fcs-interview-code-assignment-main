package com.fulfilment.application.monolith.products;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class ProductResourceTest {

    @Test
    void testListProducts() {
        given()
                .when()
                .get("/product")
                .then()
                .statusCode(200);
    }
}