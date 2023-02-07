package com.griddynamics.reactive.course.service;

import com.griddynamics.reactive.course.dto.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.util.context.Context;

import static com.griddynamics.reactive.course.logging.Logger.logOnError;
import static com.griddynamics.reactive.course.logging.Logger.logOnNext;

@Slf4j
@Service
public class ProductService {

    private final WebClient webClient;

    public ProductService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Flux<Product> getProductByProductCode(String productCode, String requestId) {
        var uri = UriComponentsBuilder.fromUriString("http://localhost:8082/productInfoService/product/names")
                .queryParam("productCode", productCode)
                .buildAndExpand()
                .toUriString();

        return webClient.get().uri(uri)
                .retrieve()
                .bodyToFlux(Product.class)
                .doOnEach(logOnNext(product -> log.info("Found Product {}", product)))
                .doOnEach(logOnError(e -> log.error("Error when searching products", e)))
                .contextWrite(Context.of("CONTEXT_KEY", requestId))
                .onErrorReturn(new Product());
    }

}
