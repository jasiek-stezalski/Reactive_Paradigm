package com.griddynamics.reactive.course.dto;

import com.griddynamics.reactive.course.entity.Users;
import lombok.Getter;

import java.util.List;

@Getter
public class UserInfo {
    private final String _id;
    private final String name;
    private final List<OrderWithProducts> orderWithProducts;

    public UserInfo(Users user, List<OrderWithProducts> orderWithProducts) {
        this._id = user.get_id();
        this.name = user.getName();
        this.orderWithProducts = orderWithProducts;
    }
}