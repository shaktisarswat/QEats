/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.javaproject.qeats.repositories;

import com.javaproject.qeats.models.RestaurantEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends MongoRepository<RestaurantEntity, String> {

    @Query("{ 'name' : ?0 }")
    Optional<List<RestaurantEntity>> findByName(String name);
}

