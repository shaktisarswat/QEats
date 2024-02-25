package com.javaproject.qeats.exchanges;

import io.micrometer.common.util.StringUtils;


public class DeleteCartRequest {
    private String cartId;
    private String itemId;
    private String restaurantId;

    public boolean isValidRequest() {
        return StringUtils.isNotEmpty(cartId)
                && StringUtils.isNotEmpty(itemId)
                && StringUtils.isNotEmpty(restaurantId);
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }
}