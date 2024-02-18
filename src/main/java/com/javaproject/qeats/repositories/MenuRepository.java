
/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.javaproject.qeats.repositories;

import com.javaproject.qeats.models.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<MenuEntity, String> {
//
//  Optional<MenuEntity> findMenuByRestaurantId(String restaurantId);
//
//  Optional<List<MenuEntity>> findMenusByItemsItemIdIn(List<String> itemIdList);

}
