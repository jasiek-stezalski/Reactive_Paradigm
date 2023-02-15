package com.griddynamics.reactive.course.service;

import com.griddynamics.reactive.course.dto.OrderInfo;
import com.griddynamics.reactive.course.dto.OrderWithProducts;
import com.griddynamics.reactive.course.repository.UserRepository;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class UserService {
    private final OrderService orderService;
    private final UserRepository userRepository;

    public UserService(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @SneakyThrows
    public Flux<OrderInfo> getUserOrdersInfoById(String userId, String requestId) {
        return userRepository.findBy_id(userId)
                .flatMapMany(user -> {
                    Flux<OrderWithProducts> ordersWithProducts = orderService.getOrdersWithProducts(user.getPhone(), requestId);
                    return ordersWithProducts.map(orders -> new OrderInfo(user, orders));
                });
    }
}