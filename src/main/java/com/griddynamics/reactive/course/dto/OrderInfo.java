package com.griddynamics.reactive.course.dto;

import com.griddynamics.reactive.course.entity.Users;
import lombok.Getter;

@Getter
public class OrderInfo {
    private final String orderNumber;
    private final String userName;
    private final String phoneNumber;
    private final String productCode;
    private final String productName;
    private final String productId;

    public OrderInfo(Users user, OrderWithProducts order) {
        this.orderNumber = order.getOrderNumber();
        this.userName = user.getName();
        this.phoneNumber = order.getPhoneNumber();
        this.productCode = order.getProductCode();
        this.productName = order.getProductName();
        this.productId = order.getProductId();
    }
}