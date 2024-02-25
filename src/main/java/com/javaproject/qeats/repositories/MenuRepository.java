package com.javaproject.qeats.repositories;

import com.javaproject.qeats.models.MenuEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends MongoRepository<MenuEntity, String> {
  Optional<MenuEntity> findMenuByRestaurantId(String restaurantId);
}
