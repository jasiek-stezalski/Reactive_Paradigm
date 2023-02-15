package com.griddynamics.reactive.course.service;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.griddynamics.reactive.course.dto.OrderInfo;
import com.griddynamics.reactive.course.entity.Users;
import com.griddynamics.reactive.course.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.Silent.class)
class UserServiceTest {

    private static final String PHONE_NUMBER = "phoneNumber1";
    private static final String PRODUCT_CODE = "3852";
    private static final String USER_ID = "user1";
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
    private final OrderService orderService = new OrderService(webClient, productService);
    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    UserService userService = new UserService(orderService, userRepository);

    @Test
    void getUserOrdersInfoById() {
        Mockito.when(userRepository.findBy_id(USER_ID))
                .thenReturn(Mono.just(new Users(USER_ID, "userName", PHONE_NUMBER)));

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

        Flux<OrderInfo> orderInfos = userService.getUserOrdersInfoById(USER_ID, "requestId1");

        StepVerifier.create(orderInfos)
                .assertNext(order -> {
                    assertEquals("Order_1", order.getOrderNumber());
                    assertEquals("userName", order.getUserName());
                    assertEquals(PHONE_NUMBER, order.getPhoneNumber());
                    assertEquals("3852", order.getProductCode());
                    assertEquals("IceCream", order.getProductName());
                    assertEquals("111", order.getProductId());
                })
                .verifyComplete();

    }
}