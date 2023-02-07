package com.griddynamics.reactive.course.repository;

import com.griddynamics.reactive.course.entity.Users;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<Users, String> {
    Mono<Users> findBy_id(String id);
}