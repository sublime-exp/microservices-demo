package com.sub.microservices.order;

import com.sub.microservices.order.stubs.InventoryClientStub;
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
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.MatcherAssert.assertThat;

@Slf4j
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class OrderServiceApplicationTests {

    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.3.0")
            .withDatabaseName("order_service")
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
    void shouldSubmitOrder() {
        String submitOrderJson = """
                {
                    "skuCode": "iphone_15",
                    "quantity": 1,
                    "price": 1000,
                    "userDetails": {
                       "email": "John@gmail.com",
                       "firstName": "John",
                       "lastName": "Doe"
                    }
                }
                """;

        InventoryClientStub.stubInventoryCall("iphone_15", 1);

        var responseBodyString = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(submitOrderJson)
                .when()
                .post("/api/order")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .body()
                .asString();

        assertThat(responseBodyString, Matchers.is("Order Placed Successfully!"));
    }

    @Test
    void shouldFailOrderWhenProductIsNotInStock() {
        String submitOrderJson = """
                {
                     "skuCode": "iphone_15",
                     "price": 1000,
                     "quantity": 1000,
                     "userDetails": {
                        "email": "John@gmail.com",
                        "firstName": "John",
                        "lastName": "Doe"
                    }
                }
                """;
        InventoryClientStub.stubInventoryCall("iphone_15", 1000);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(submitOrderJson)
                .when()
                .post("/api/order")
                .then()
                .log().all()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

}
