package com.griddynamics.reactive.course.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Product {
    private String productId;
    private String productCode;
    private String productName;
    private double score;
}