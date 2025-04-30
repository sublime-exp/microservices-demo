package com.sub.microservices.order.stubs;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class InventoryClientStub {

    public static void stubInventoryCall(String skuCode, Integer quantity) {
        String uri = String.format("/api/inventory?skuCode=%s&quantity=%s", skuCode, quantity);
        if (quantity <= 100) {
            stubFor(get(urlEqualTo(uri))
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.SC_OK)
                            .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString())
                            .withBody("true")));
        } else {
            stubFor(get(urlEqualTo(uri))
                    .willReturn(aResponse()
                            .withStatus(HttpStatus.SC_OK)
                            .withHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString())
                            .withBody("false")));
        }
    }
}
