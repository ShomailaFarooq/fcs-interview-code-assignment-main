package com.fulfilment.application.monolith.products;

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
class ProductResourceTest {

    private String name(String base) {
        return base + "_" + System.nanoTime();
    }

    private String body(String name, int stock) {
        return """
                {
                  "name": "%s",
                  "description": "Test description",
                  "price": 9.99,
                  "stock": %d
                }
                """.formatted(name, stock);
    }

    @Test
    @Order(1)
    void testListProducts() {
        given()
                .when().get("/product")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    @Order(2)
    void testCreateProduct() {
        String productName = name("Test Product");
        given()
                .contentType(ContentType.JSON)
                .body(body(productName, 10))
                .when().post("/product")
                .then()
                .statusCode(201)
                .body("name", is(productName));
    }

    @Test
    @Order(3)
    void testCreateProduct_withId_shouldFail() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                  "id": 999,
                  "name": "Bad Product"
                }
                """)
                .when().post("/product")
                .then()
                .statusCode(422)
                .body("error", containsString("Id was invalidly set"));
    }

    @Test
    @Order(4)
    void testGetSingleProduct() {
        int id = given()
                .contentType(ContentType.JSON)
                .body(body(name("Single Product"), 5))
                .when().post("/product")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .when().get("/product/" + id)
                .then()
                .statusCode(200)
                .body("id", is(id));
    }

    @Test
    @Order(5)
    void testGetSingleProduct_notFound() {
        given()
                .when().get("/product/999999")
                .then()
                .statusCode(404)
                .body("error", containsString("does not exist"));
    }

    @Test
    @Order(6)
    void testUpdateProduct() {
        String updatedName = name("Updated Product");
        int id = given()
                .contentType(ContentType.JSON)
                .body(body(name("Update Me"), 3))
                .when().post("/product")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .contentType(ContentType.JSON)
                .body(body(updatedName, 99))
                .when().put("/product/" + id)
                .then()
                .statusCode(200)
                .body("name", is(updatedName))
                .body("stock", is(99));
    }

    @Test
    @Order(7)
    void testUpdateProduct_noName_shouldFail() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                {
                  "stock": 5
                }
                """)
                .when().put("/product/1")
                .then()
                .statusCode(422)
                .body("error", containsString("Product Name was not set"));
    }

    @Test
    @Order(8)
    void testUpdateProduct_notFound() {
        given()
                .contentType(ContentType.JSON)
                .body(body(name("Ghost"), 1))
                .when().put("/product/999999")
                .then()
                .statusCode(404)
                .body("error", containsString("does not exist"));
    }

    @Test
    @Order(9)
    void testDeleteProduct() {
        int id = given()
                .contentType(ContentType.JSON)
                .body(body(name("Delete Me"), 0))
                .when().post("/product")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .when().delete("/product/" + id)
                .then()
                .statusCode(204);
    }

    @Test
    @Order(10)
    void testDeleteProduct_notFound() {
        given()
                .when().delete("/product/999999")
                .then()
                .statusCode(404)
                .body("error", containsString("does not exist"));
    }
}