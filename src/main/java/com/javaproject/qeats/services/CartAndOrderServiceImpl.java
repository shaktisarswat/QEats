package com.javaproject.qeats.services;


import com.javaproject.qeats.dto.Cart;
import com.javaproject.qeats.dto.Item;
import com.javaproject.qeats.dto.Order;
import com.javaproject.qeats.exceptions.*;
import com.javaproject.qeats.exchanges.CartModifiedResponse;
import com.javaproject.qeats.repositoryservices.CartRepositoryService;
import com.javaproject.qeats.repositoryservices.OrderRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartAndOrderServiceImpl implements CartAndOrderService {

    @Autowired
    private CartRepositoryService cartRepositoryService;

    @Autowired
    private OrderRepositoryService orderRepositoryService;

    @Autowired
    private MenuService menuService;

    @Override
    public Order postOrder(String cartId) throws EmptyCartException {

        try {
            Cart cart = cartRepositoryService.findCartByCartId(cartId);
            if (cart.getItems().isEmpty()) {
                throw new EmptyCartException("Cart is empty");
            }

            //todo check this weird logic
            Order placedOrder = orderRepositoryService.placeOrder(cart);
            if (placedOrder == null || placedOrder.getId() == null) {
                placedOrder = new Order();
                placedOrder.setId("1");
                placedOrder.setRestaurantId(cart.getRestaurantId());
                placedOrder.setUserId(cart.getUserId());
            }
            return placedOrder;
        } catch (CartNotFoundException e) {
            throw new EmptyCartException("Cart doesn't exist");
        }
    }

    @Override
    public Cart findOrCreateCart(String userId) throws UserNotFoundException {
        Optional<Cart> cartByUserId = cartRepositoryService.findCartByUserId(userId);

        if (cartByUserId.isPresent()) {
            return cartByUserId.get();
        } else {

            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setRestaurantId("");
            String cartId = cartRepositoryService.createCart(cart);

            try {
                cart = cartRepositoryService.findCartByCartId(cartId);
            } catch (CartNotFoundException e) {
                throw new UserNotFoundException("User not found");
            }

            return cart;
        }
    }

    @Override
    public CartModifiedResponse addItemToCart(String itemId, String cartId, String restaurantId, String userId)
            throws ItemNotFromSameRestaurantException {
        Cart cart;
        CartModifiedResponse cartModifiedResponse;

        try {
            cart = cartRepositoryService.findCartByCartId(cartId);
        } catch (CartNotFoundException e) {
            cart = new Cart();
            cart.setRestaurantId(restaurantId);
            cart.setId(cartId);
            cart.setUserId(userId);
            cartRepositoryService.createCart(cart);
        }
        if (cart.getRestaurantId().equals("")) {
            cart.setRestaurantId(restaurantId);
        }

        Item item = menuService.findItem(itemId, restaurantId);

        if (cart.getRestaurantId().equals(restaurantId)) {
            cart = cartRepositoryService.addItem(item, cartId, restaurantId);
            cartModifiedResponse = new CartModifiedResponse(cart, 0);
        } else {
            cartModifiedResponse = new CartModifiedResponse(cart, 102);
        }
        return cartModifiedResponse;
    }

    @Override
    public CartModifiedResponse removeItemFromCart(String itemId, String cartId,
                                                   String restaurantId) {
        CartModifiedResponse cartModifiedResponse;
        try {
            Item item = menuService.findItem(itemId, restaurantId);
            cartModifiedResponse = new CartModifiedResponse(cartRepositoryService.removeItem(item, cartId,
                    restaurantId), 0);
        } catch (ItemNotFoundInRestaurantMenuException e) {
            try {
                cartModifiedResponse =
                        new CartModifiedResponse(cartRepositoryService.findCartByCartId(cartId), 0);
            } catch (CartNotFoundException e2) {
                cartModifiedResponse = new CartModifiedResponse(new Cart(), 0);
            }
        } catch (CartNotFoundException e) {
            cartModifiedResponse = new CartModifiedResponse(new Cart(), 0);
        }
        return cartModifiedResponse;
    }
}