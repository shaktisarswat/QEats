package com.javaproject.qeats.repositories;

import com.javaproject.qeats.models.MenuEntity;
import com.javaproject.qeats.models.RestaurantEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends MongoRepository<MenuEntity, String> {
  Optional<MenuEntity> findMenuByRestaurantId(String restaurantId);

  @Query(value = "[{$unwind: '$items'}, {$match: {'items.name': ?0}}, {$project: {restaurantId: '$restaurantId'}}]")
  Optional<List<RestaurantEntity>> findRestaurantsByItemName(String searchString);
  @Query(value = "[{$unwind: '$items'}, {$match: {'items.attributes': ?0}}, {$project: {restaurantId: '$restaurantId'}}]")
  Optional<List<RestaurantEntity>> findRestaurantIdsByItemAttributes(String attribute);


}
