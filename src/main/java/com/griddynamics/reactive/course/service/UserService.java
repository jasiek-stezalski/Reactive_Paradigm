package com.griddynamics.reactive.course.service;

import com.griddynamics.reactive.course.dto.OrderWithProducts;
import com.griddynamics.reactive.course.dto.UserInfo;
import com.griddynamics.reactive.course.entity.Users;
import com.griddynamics.reactive.course.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class UserService {
    private final OrderService orderService;
    private final UserRepository userRepository;


    public UserService(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    public Mono<UserInfo> getUserData(String userId, String requestId) {
        Mono<Users> usersMono = userRepository.findBy_id(userId);
        return usersMono.flatMap(user -> {
            Mono<List<OrderWithProducts>> ordersWithProducts = orderService.getOrdersWithProducts(user.getPhone(), requestId);
            return ordersWithProducts.map(orders -> new UserInfo(user, orders));
        });
    }
}