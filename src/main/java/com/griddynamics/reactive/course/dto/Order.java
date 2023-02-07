package com.griddynamics.reactive.course.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {
    private String phoneNumber;
    private String orderNumber;
    private String productCode;
}