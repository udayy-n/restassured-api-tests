package com.uday.api.tests;

import com.uday.api.base.BaseTest;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserAndCustomerApiTests extends BaseTest {

    @Test(priority = 1, groups = {"smoke", "functional", "regression"},
          description = "TC16: Verify GET /users returns 10 customer profiles with non-null emails")
    public void test09_GetAllCustomers() {
        given()
            .spec(requestSpec)
        .when()
            .get("/users")
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .header("Content-Type", containsString("application/json"))
            .body("size()", equalTo(10))
            .body("[0].email", containsString("@"))
            .body("[0].address.city", not(emptyOrNullString()));
    }

    @Test(priority = 2, groups = {"smoke", "functional", "regression"},
          description = "TC17: Validate GET /users/1 response against Customer JSON Schema")
    public void test10_ValidateCustomerJsonSchema() {
        InputStream schemaStream = getClass().getClassLoader()
                .getResourceAsStream("schemas/user-schema.json");

        given()
            .spec(requestSpec)
            .pathParam("id", 1)
        .when()
            .get("/users/{id}")
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body(JsonSchemaValidator.matchesJsonSchema(schemaStream));
    }

    @DataProvider(name = "customerData")
    public Object[][] customerData() {
        return new Object[][] {
            { 1, "Bret", "Sincere@april.biz" },
            { 2, "Antonette", "Shanna@melissa.tv" },
            { 3, "Samantha", "Nathan@yesenia.net" }
        };
    }

    @Test(priority = 3, dataProvider = "customerData", groups = {"functional", "regression"},
          description = "TC18-TC20: Data-driven verification of customer usernames and emails")
    public void test11_VerifyCustomerProfileDataDriven(int userId, String expectedUsername, String expectedEmail) {
        given()
            .spec(requestSpec)
            .pathParam("id", userId)
        .when()
            .get("/users/{id}")
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("id", equalTo(userId))
            .body("username", equalTo(expectedUsername))
            .body("email", equalTo(expectedEmail));
    }

    @Test(priority = 4, groups = {"functional", "regression"},
          description = "TC21: Verify query parameter filtering GET /posts?userId=1 returns only user 1 records")
    public void test12_FilterOrdersByCustomerId() {
        given()
            .spec(requestSpec)
            .queryParam("userId", 1)
        .when()
            .get("/posts")
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("size()", greaterThan(0))
            .body("userId", everyItem(equalTo(1)));
    }

    @Test(priority = 5, groups = {"functional", "regression"},
          description = "TC22: Verify nested resource GET /posts/1/comments returns associated review comments")
    public void test13_GetOrderCommentsNestedEndpoint() {
        given()
            .spec(requestSpec)
            .pathParam("id", 1)
        .when()
            .get("/posts/{id}/comments")
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("size()", equalTo(5))
            .body("postId", everyItem(equalTo(1)))
            .body("[0].email", containsString("@"));
    }
}
