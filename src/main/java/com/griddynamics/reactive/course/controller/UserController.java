package com.griddynamics.reactive.course.controller;


import com.griddynamics.reactive.course.dto.UserInfo;
import com.griddynamics.reactive.course.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/userInfoService")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping(value = "/user")
    public Mono<UserInfo> getUserById(@RequestParam String userId,
                                      @RequestHeader(name = "requestId") String requestId) {
        return userService.getUserData(userId, requestId);
    }
}