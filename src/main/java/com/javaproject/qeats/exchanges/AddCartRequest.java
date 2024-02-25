package com.javaproject.qeats.exchanges;

import io.micrometer.common.util.StringUtils;

public class AddCartRequest {
    private String cartId;
    private String itemId;
    private String restaurantId;

    private String userId;

    public boolean isValidRequest() {
        return StringUtils.isNotEmpty(cartId)
                && StringUtils.isNotEmpty(itemId)
                && StringUtils.isNotEmpty(restaurantId)
                && StringUtils.isNotEmpty(userId);
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}