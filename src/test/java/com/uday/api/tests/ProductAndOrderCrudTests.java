package com.uday.api.tests;

import com.uday.api.base.BaseTest;
import com.uday.api.models.OrderPayload;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ProductAndOrderCrudTests extends BaseTest {

    @Test(priority = 1, groups = {"smoke", "functional", "regression"},
          description = "TC01: Verify GET /posts returns 200 OK, JSON content-type header, and 100 records")
    public void test01_GetAllOrdersStatusAndHeaders() {
        given()
            .spec(requestSpec)
        .when()
            .get("/posts")
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .header("Content-Type", containsString("application/json"))
            .body("size()", equalTo(100))
            .body("[0].id", notNullValue())
            .body("[0].userId", notNullValue())
            .body("[0].title", not(emptyOrNullString()));
    }

    @Test(priority = 2, groups = {"smoke", "functional", "regression"},
          description = "TC02: Verify GET /posts/{id} returns exact record payload for ID 1")
    public void test02_GetSingleOrderById() {
        given()
            .spec(requestSpec)
            .pathParam("id", 1)
        .when()
            .get("/posts/{id}")
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("id", equalTo(1))
            .body("userId", equalTo(1))
            .body("title", not(emptyOrNullString()))
            .body("body", not(emptyOrNullString()));
    }

    @Test(priority = 3, groups = {"smoke", "functional", "regression"},
          description = "TC03: Validate GET /posts/{id} response against JSON Schema contract")
    public void test03_ValidateOrderJsonSchema() {
        InputStream schemaStream = getClass().getClassLoader()
                .getResourceAsStream("schemas/order-schema.json");

        given()
            .spec(requestSpec)
            .pathParam("id", 1)
        .when()
            .get("/posts/{id}")
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body(JsonSchemaValidator.matchesJsonSchema(schemaStream));
    }

    @DataProvider(name = "orderIdProvider")
    public Object[][] orderIdProvider() {
        return new Object[][] {
            { 1, 1 },
            { 15, 2 },
            { 25, 3 },
            { 45, 5 },
            { 95, 10 }
        };
    }

    @Test(priority = 4, dataProvider = "orderIdProvider", groups = {"functional", "regression"},
          description = "TC04-TC08: Data-driven GET /posts/{id} verifying userId mapping across 5 records")
    public void test04_GetOrderByIdDataDriven(int orderId, int expectedUserId) {
        given()
            .spec(requestSpec)
            .pathParam("id", orderId)
        .when()
            .get("/posts/{id}")
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("id", equalTo(orderId))
            .body("userId", equalTo(expectedUserId));
    }

    @DataProvider(name = "createOrderPayloads")
    public Object[][] createOrderPayloads() {
        return new Object[][] {
            { 101, "Order #A100 - Wireless Gaming Mouse", "Priority Express Shipment - Quantity: 2" },
            { 102, "Order #A101 - Mechanical Keyboard RGB", "Standard Ground Shipment - Quantity: 1" },
            { 103, "Order #A102 - 27-Inch 4K Monitor", "Fragile Handling Shipment - Quantity: 1" },
            { 104, "Order #A103 - USB-C Docking Station", "Corporate Bulk Order - Quantity: 5" }
        };
    }

    @Test(priority = 5, dataProvider = "createOrderPayloads", groups = {"functional", "regression"},
          description = "TC09-TC12: Data-driven POST /posts with Bearer Token Auth creating 4 new orders")
    public void test05_CreateOrderWithTokenAuthDataDriven(int userId, String title, String body) {
        OrderPayload payload = new OrderPayload(userId, title, body);

        given()
            .spec(authRequestSpec)
            .body(payload)
        .when()
            .post("/posts")
        .then()
            .spec(responseSpec)
            .statusCode(201)
            .header("Content-Type", containsString("application/json"))
            .body("id", notNullValue())
            .body("userId", equalTo(userId))
            .body("title", equalTo(title))
            .body("body", equalTo(body));
    }

    @Test(priority = 6, groups = {"functional", "regression"},
          description = "TC13: Verify PUT /posts/{id} with Bearer Token Auth updates full resource")
    public void test06_UpdateOrderFullPutWithTokenAuth() {
        OrderPayload updatedPayload = new OrderPayload(
                1,
                "Updated Order #A100 - Pro Gaming Mouse",
                "Updated Shipping Address: Hyderabad Campus"
        );

        given()
            .spec(authRequestSpec)
            .pathParam("id", 1)
            .body(updatedPayload)
        .when()
            .put("/posts/{id}")
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("id", equalTo(1))
            .body("userId", equalTo(1))
            .body("title", equalTo("Updated Order #A100 - Pro Gaming Mouse"))
            .body("body", containsString("Hyderabad Campus"));
    }

    @Test(priority = 7, groups = {"functional", "regression"},
          description = "TC14: Verify PATCH /posts/{id} with Bearer Token Auth partially updates title")
    public void test07_PartialUpdateOrderPatchWithTokenAuth() {
        Map<String, Object> patchPayload = new HashMap<>();
        patchPayload.put("title", "Patched Order Status: DISPATCHED");

        given()
            .spec(authRequestSpec)
            .pathParam("id", 1)
            .body(patchPayload)
        .when()
            .patch("/posts/{id}")
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("id", equalTo(1))
            .body("title", equalTo("Patched Order Status: DISPATCHED"));
    }

    @Test(priority = 8, groups = {"functional", "regression"},
          description = "TC15: Verify DELETE /posts/{id} with Bearer Token Auth removes resource")
    public void test08_DeleteOrderWithTokenAuth() {
        given()
            .spec(authRequestSpec)
            .pathParam("id", 1)
        .when()
            .delete("/posts/{id}")
        .then()
            .spec(responseSpec)
            .statusCode(200);
    }
}
