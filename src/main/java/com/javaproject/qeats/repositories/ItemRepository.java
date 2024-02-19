
package com.javaproject.qeats.repositories;

import com.javaproject.qeats.models.ItemEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<ItemEntity, String> {

}

