package com.griddynamics.reactive.course.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class Config {
    @Bean
    WebClient webClient(WebClient.Builder builder) {
        HttpClient client = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(5));

        return builder
                .clientConnector(new ReactorClientHttpConnector(client))
                .build();
    }
}