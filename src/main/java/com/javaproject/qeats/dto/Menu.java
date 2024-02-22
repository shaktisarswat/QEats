package com.javaproject.qeats.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Menu {

    @NotNull
    private String restaurantId;

    @NotNull
    private List<Item> items = new ArrayList();

    public Menu(String restaurantId, List<Item> items) {
        this.restaurantId = restaurantId;
        this.items = items;
    }

    public Menu() {
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
