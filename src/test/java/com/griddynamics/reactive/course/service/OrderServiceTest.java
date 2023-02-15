package com.griddynamics.reactive.course.service;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.griddynamics.reactive.course.dto.OrderWithProducts;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderServiceTest {
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String PRODUCT_CODE = "3852";
    public static WireMockRule wireMockRuleOrder = new WireMockRule(wireMockConfig().port(8081));
    public static WireMockRule wireMockRuleProduct = new WireMockRule(wireMockConfig().port(8082));

    @BeforeAll
    public static void beforeAll() {
        wireMockRuleOrder.start();
        wireMockRuleProduct.start();
    }

    @AfterAll
    public static void afterAll() {
        wireMockRuleOrder.stop();
        wireMockRuleProduct.stop();
    }

    @AfterEach
    public void afterEach() {
        wireMockRuleOrder.resetAll();
        wireMockRuleProduct.resetAll();
    }

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8081/orderSearchService/order/phone")
            .build();

    WebClient webClient2 = WebClient.builder()
            .baseUrl("http://localhost:8082/productInfoService/product/names")
            .build();

    private final ProductService productService = new ProductService(webClient2);

    OrderService orderService = new OrderService(webClient, productService);

    @Test
    void getOrdersWithProducts() {
        wireMockRuleOrder.stubFor(get(urlEqualTo("/orderSearchService/order/phone?phoneNumber=" + PHONE_NUMBER))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                "{\"phoneNumber\":\"phoneNumber1\",\"orderNumber\":\"Order_1\",\"productCode\":\"3852\"}"
                        )
                )
        );

        wireMockRuleProduct.stubFor(get(urlEqualTo("/productInfoService/product/names?productCode=" + PRODUCT_CODE))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                "[{\"productId\":\"111\",\"productCode\":\"3852\",\"productName\":\"IceCream\",\"score\":4903.59}," +
                                        "{\"productId\":\"222\",\"productCode\":\"3852\",\"productName\":\"Milk\",\"score\":4198.92}]"
                        )
                )
        );

        Flux<OrderWithProducts> orders = orderService.getOrdersWithProducts(PHONE_NUMBER, "requestId1");

        StepVerifier.create(orders)
                .assertNext(order -> {
                    assertEquals("Order_1", order.getOrderNumber());
                    assertEquals("IceCream", order.getProductName());
                    assertEquals(4903.59, order.getScore());
                })
                .verifyComplete();
    }
}

