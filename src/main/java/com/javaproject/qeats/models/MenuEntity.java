package com.javaproject.qeats.models;


import com.javaproject.qeats.dto.Item;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "menus")
public class MenuEntity {
    @Id
    private String id;
    @NotNull
    private String restaurantId;
    @NotNull
    private List<Item> items = new ArrayList();

    public MenuEntity(String id, String restaurantId, List<Item> items) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
