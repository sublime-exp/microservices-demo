package com.sub.microservices.inventory;

import io.restassured.RestAssured;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@Slf4j
@Testcontainers // 💥 This automatically handles container start/stop
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class InventoryServiceApplicationTests {

    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.3.0")
            .withDatabaseName("inventory_service")
            .withUsername("root")
            .withPassword("mysql");

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setup() {
        log.info("MySQL URL: {}", mySQLContainer.getJdbcUrl());
        log.info("MySQL Host: {}", mySQLContainer.getHost());
        log.info("MySQL Port: {}", mySQLContainer.getFirstMappedPort());
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    @Test
    void shouldReadInventory() {
        given()
                .when()
                .get("/api/inventory?skuCode=iphone_15&quantity=1")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body(is("true"));

        given()
                .when()
                .get("/api/inventory?skuCode=iphone_15&quantity=1000")
                .then()
                .log().all()
                .statusCode(HttpStatus.OK.value())
                .body(is("false"));
    }

}
