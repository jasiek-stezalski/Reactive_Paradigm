package com.griddynamics.reactive.course.controller;

import com.griddynamics.reactive.course.dto.OrderInfo;
import com.griddynamics.reactive.course.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class RequestHandler {
    @Autowired
    private UserService userService;

    public Mono<ServerResponse> streamOrderInfo(String userId, String requestId) {
        return ServerResponse.ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(userService.getUserOrdersInfoById(userId, requestId), OrderInfo.class);
    }
}