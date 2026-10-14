package com.uday.api.tests;

import com.uday.api.base.BaseTest;
import com.uday.api.models.OrderPayload;
import com.uday.api.utils.TokenManager;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BoundaryAndNegativeApiTests extends BaseTest {

    @Test(priority = 1, groups = {"boundary", "regression"},
          description = "TC23: Boundary test - Verify minimum limit=_limit=1 returns exactly 1 record")
    public void test14_BoundaryMinimumLimitOne() {
        given()
            .spec(requestSpec)
            .queryParam("_limit", 1)
        .when()
            .get("/posts")
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("size()", equalTo(1));
    }

    @Test(priority = 2, groups = {"boundary", "regression"},
          description = "TC24: Boundary test - Verify pagination limit=_limit=5 returns exactly 5 records")
    public void test15_BoundaryLimitFivePagination() {
        given()
            .spec(requestSpec)
            .queryParam("_limit", 5)
        .when()
            .get("/posts")
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("size()", equalTo(5));
    }

    @Test(priority = 3, groups = {"boundary", "regression"},
          description = "TC25: Boundary test - Verify POST /posts accepts long 250-char title payload")
    public void test16_BoundaryLongTitlePayload() {
        String longTitle = "B".repeat(250);
        OrderPayload payload = new OrderPayload(1, longTitle, "Boundary length validation test");

        given()
            .spec(authRequestSpec)
            .body(payload)
        .when()
            .post("/posts")
        .then()
            .spec(responseSpec)
            .statusCode(201)
            .body("title.length()", equalTo(250));
    }

    @Test(priority = 4, groups = {"negative", "regression"},
          description = "TC26: Negative test - Verify GET /posts/999999 for non-existent ID returns 404 Not Found")
    public void test17_NegativeNonExistentOrderReturns404() {
        given()
            .spec(requestSpec)
            .pathParam("id", 999999)
        .when()
            .get("/posts/{id}")
        .then()
            .spec(responseSpec)
            .statusCode(404);
    }

    @Test(priority = 5, groups = {"negative", "regression"},
          description = "TC27: Negative test - Verify GET /users/999999 for non-existent customer returns 404 Not Found")
    public void test18_NegativeNonExistentCustomerReturns404() {
        given()
            .spec(requestSpec)
            .pathParam("id", 999999)
        .when()
            .get("/users/{id}")
        .then()
            .spec(responseSpec)
            .statusCode(404);
    }

    @Test(priority = 6, groups = {"negative", "regression"},
          description = "TC28: Negative test - Verify invalid endpoint URI returns 404 Not Found")
    public void test19_NegativeInvalidEndpointReturns404() {
        given()
            .spec(requestSpec)
        .when()
            .get("/invalid_endpoint_path_xyz")
        .then()
            .spec(responseSpec)
            .statusCode(404);
    }

    @Test(priority = 7, groups = {"negative", "regression"},
          description = "TC29: Negative test - Verify filtering with non-existent userId=99999 returns empty list []")
    public void test20_NegativeFilterNonExistentUserIdReturnsEmptyList() {
        given()
            .spec(requestSpec)
            .queryParam("userId", 99999)
        .when()
            .get("/posts")
        .then()
            .spec(responseSpec)
            .statusCode(200)
            .body("size()", equalTo(0));
    }

    @Test(priority = 8, groups = {"smoke", "regression"},
          description = "TC30: Verify TokenManager generates non-empty JWT-formatted Bearer token")
    public void test21_VerifyBearerTokenStructure() {
        String token = TokenManager.getBearerToken();
        Assert.assertNotNull(token, "Bearer token must not be null");
        Assert.assertTrue(token.split("\\.").length == 3, "Bearer token must follow 3-part JWT structure");
    }
}
