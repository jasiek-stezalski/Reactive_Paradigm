package com.griddynamics.reactive.course.dto;

import lombok.Getter;

@Getter
public class OrderWithProducts {
    private final String phoneNumber;
    private final String orderNumber;
    private String productId;
    private String productCode;
    private String productName;
    private double score;

    public OrderWithProducts(Order order, Product product) {
        this.phoneNumber = order.getPhoneNumber();
        this.orderNumber = order.getOrderNumber();
        this.productId = product.getProductId();
        this.productCode = product.getProductCode();
        this.productName = product.getProductName();
        this.score = product.getScore();
    }

    public OrderWithProducts(Order order) {
        this.phoneNumber = order.getPhoneNumber();
        this.orderNumber = order.getOrderNumber();
    }
}