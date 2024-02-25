package com.javaproject.qeats.repositories;

import com.javaproject.qeats.models.OrderEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<OrderEntity, String> {

}