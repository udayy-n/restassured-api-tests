package com.uday.api.base;

import com.uday.api.utils.TokenManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;

import static org.hamcrest.Matchers.lessThan;

/**
 * BaseTest initializes centralized RequestSpecification (standard and Bearer-authenticated)
 * and ResponseSpecification for reusable API test execution.
 */
public class BaseTest {

    protected static RequestSpecification requestSpec;
    protected static RequestSpecification authRequestSpec;
    protected static ResponseSpecification responseSpec;

    @BeforeClass(alwaysRun = true)
    public void setUpSpecifications() {
        String baseUri = System.getProperty("base.uri", "https://jsonplaceholder.typicode.com");
        RestAssured.baseURI = baseUri;

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(baseUri)
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addHeader("X-Client-Platform", "RestAssured-TestNG-Framework")
                .log(LogDetail.METHOD)
                .log(LogDetail.URI)
                .build();

        authRequestSpec = new RequestSpecBuilder()
                .addRequestSpecification(requestSpec)
                .addHeader("Authorization", "Bearer " + TokenManager.getBearerToken())
                .build();

        responseSpec = new ResponseSpecBuilder()
                .expectResponseTime(lessThan(5000L))
                .build();
    }
}
