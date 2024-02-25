package com.javaproject.qeats.exchanges;

import com.javaproject.qeats.dto.Cart;


public class CartModifiedResponse {
    private Cart cart;
    private int cartResponseType;

    public CartModifiedResponse(Cart cart, int cartResponseType) {
        this.cart = cart;
        this.cartResponseType = cartResponseType;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public int getCartResponseType() {
        return cartResponseType;
    }

    public void setCartResponseType(int cartResponseType) {
        this.cartResponseType = cartResponseType;
    }
}
