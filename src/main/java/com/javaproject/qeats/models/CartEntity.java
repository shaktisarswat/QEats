package com.javaproject.qeats.models;

import com.javaproject.qeats.dto.Item;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "carts")
public class CartEntity {

    @Id
    private String id;

    @NotNull
    private String restaurantId;

    @NotNull
    private String userId;

    @NotNull
    private List<Item> items = new ArrayList();

    @NotNull
    private int total = 0;

    public void addItem(Item item) {
        items.add(item);
        total += item.getPrice();
    }

    public void removeItem(Item item) {
        // this will check the instance reference of Item so deleting the reference is not good idea
        // rather than delete by item id

//        boolean removed = items.remove(item);
        boolean removed = false;
        for(Item it : items)
        {
            if(it.getItemId()!=null && it.getItemId().equals(item.getItemId()))
            {
                items.remove(it);
                removed=true;
                break;
            }
        }

        if (removed) {
            total -= item.getPrice();
        }
    }

    public void clearCart() {
        if (items.size() > 0) {
            items.clear();
            total = 0;
        }
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}