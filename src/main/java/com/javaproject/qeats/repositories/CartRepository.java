package com.javaproject.qeats.repositories;

import com.javaproject.qeats.models.CartEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CartRepository extends MongoRepository<CartEntity, String> {

    Optional<CartEntity> findCartByUserId(String userId);

    Optional<CartEntity> findCartById(String cartId);

}