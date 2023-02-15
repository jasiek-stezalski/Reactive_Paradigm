package com.griddynamics.reactive.course.service;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.griddynamics.reactive.course.dto.Product;
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

class ProductServiceTest {

    private static final String PRODUCT_CODE = "productCode1";

    public static WireMockRule wireMockRuleProduct = new WireMockRule(wireMockConfig().port(8082));

    @BeforeAll
    public static void beforeAll() {
        wireMockRuleProduct.start();
    }

    @AfterAll
    public static void afterAll() {
        wireMockRuleProduct.stop();
    }

    @AfterEach
    public void afterEach() {
        wireMockRuleProduct.resetAll();
    }

    WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8082/productInfoService/product/names")
            .build();
    ProductService productService = new ProductService(webClient);

    @Test
    void getProductByProductCode() {
        wireMockRuleProduct.stubFor(get(urlEqualTo("/productInfoService/product/names?productCode=" + PRODUCT_CODE))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                "[{\"productId\":\"111\",\"productCode\":\"productCode1\",\"productName\":\"IceCream\",\"score\":4903.59}," +
                                        "{\"productId\":\"222\",\"productCode\":\"productCode1\",\"productName\":\"Milk\",\"score\":4198.92}]"
                        )
                )
        );

        Flux<Product> products = productService.getProductByProductCode(PRODUCT_CODE, "requestId1");

        StepVerifier.create(products)
                .assertNext(product -> assertEquals("IceCream", product.getProductName()))
                .assertNext(product -> assertEquals("Milk", product.getProductName()))
                .verifyComplete();
    }

}