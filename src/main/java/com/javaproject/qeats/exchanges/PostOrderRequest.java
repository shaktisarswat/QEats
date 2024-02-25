package com.javaproject.qeats.exchanges;

import io.micrometer.common.util.StringUtils;


public class PostOrderRequest {
    private String cartId;

    public boolean isValidRequest() {
        return StringUtils.isNotEmpty(cartId);
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }
}
