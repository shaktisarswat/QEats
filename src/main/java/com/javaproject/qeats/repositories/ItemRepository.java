
package com.javaproject.qeats.repositories;

import com.javaproject.qeats.models.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<ItemEntity, String> {

}

