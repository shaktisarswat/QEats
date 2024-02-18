/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.javaproject.qeats.repositories;

import com.javaproject.qeats.models.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantEntity, String> {

//    @Query("{ 'name' : ?0 }")
    Optional<List<RestaurantEntity>> findByName(String name);
}

