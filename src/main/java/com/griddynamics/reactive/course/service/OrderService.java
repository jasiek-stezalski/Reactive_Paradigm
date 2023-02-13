package com.griddynamics.reactive.course.service;

import com.griddynamics.reactive.course.dto.Order;
import com.griddynamics.reactive.course.dto.OrderWithProducts;
import com.griddynamics.reactive.course.dto.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.util.context.Context;

import java.util.Comparator;

import static com.griddynamics.reactive.course.logging.Logger.logOnError;
import static com.griddynamics.reactive.course.logging.Logger.logOnNext;

@Service
@Slf4j
public class OrderService {

    private final WebClient webClient;

    private final ProductService productService;

    public OrderService(WebClient webClient, ProductService productService) {
        this.webClient = webClient;
        this.productService = productService;
    }

    public Flux<OrderWithProducts> getOrdersWithProducts(String phoneNumber, String requestId) {
        Flux<Order> ordersByPhoneNumber = getOrdersByPhoneNumber(phoneNumber, requestId);
        return ordersByPhoneNumber.flatMap(order -> {
            Flux<Product> productByProductCode = productService.getProductByProductCode(order.getProductCode(), requestId);

            return productByProductCode
                    .collectList()
                    .map(products -> {
                        products.sort(Comparator.comparing(Product::getScore).reversed());
                        return new OrderWithProducts(order, products.get(0));
                    });
        });
    }

    private Flux<Order> getOrdersByPhoneNumber(String phoneNumber, String requestId) {
        var uri = UriComponentsBuilder.fromUriString("http://localhost:8081/orderSearchService/order/phone")
                .queryParam("phoneNumber", phoneNumber)
                .buildAndExpand()
                .toUriString();

        return webClient.get().uri(uri)
                .retrieve()
                .bodyToFlux(Order.class)
                .doOnEach(logOnNext(order -> log.info("Found Order {}", order)))
                .doOnEach(logOnError(e -> log.error("Error when searching orders", e)))
                .contextWrite(Context.of("CONTEXT_KEY", requestId));
    }
}
