package com.sub.services.product;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static io.restassured.RestAssured.given;

@Slf4j
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceApplicationTests {

    //  this annotation overrides the application.properties for the mongo eg: user, pass, etc
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup() {
        log.info("MongoDB URL: {}", mongoDBContainer.getReplicaSetUrl());
        log.info("MongoDB Host: {}", mongoDBContainer.getHost());
        log.info("MongoDB Port: {}", mongoDBContainer.getFirstMappedPort());
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    void shouldCreateProduct() {
        String requestBody = """
                {
                    "name": "Iphone 15",
                    "description": "Iphone 15 is smartphone from Apple",
                    "price": 1000
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/product")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .body("id", Matchers.notNullValue())
                .body("name", Matchers.equalTo("Iphone 15"))
                .body("description", Matchers.equalTo("Iphone 15 is smartphone from Apple"))
                .body("price", Matchers.equalTo(1000));

    }

}
