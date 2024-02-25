package com.javaproject.qeats.repositoryservices;

import com.javaproject.qeats.dto.Cart;
import com.javaproject.qeats.dto.Item;
import com.javaproject.qeats.exceptions.CartNotFoundException;
import com.javaproject.qeats.models.CartEntity;
import com.javaproject.qeats.repositories.CartRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartRepositoryServiceImpl implements CartRepositoryService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ModelMapper modelMapperProvider;

    @Override
    public String createCart(Cart cart) {
        CartEntity cartEntity = cartRepository.save(modelMapperProvider.map(cart, CartEntity.class));
        return cartEntity.getId();
    }

    @Override
    public Optional<Cart> findCartByUserId(String userId) {

        Optional<CartEntity> cartByUserId = cartRepository.findCartByUserId(userId);

        if (cartByUserId.isPresent()) {
            Cart cart = modelMapperProvider.map(cartByUserId.get(), Cart.class);
            return Optional.of(cart);
        }

        return Optional.empty();
    }

    @Override
    public Cart findCartByCartId(String cartId) throws CartNotFoundException {

        Optional<CartEntity> cartById = cartRepository.findCartById(cartId);

        if (cartById.isPresent()) {
            return modelMapperProvider.map(cartById.get(), Cart.class);
        }
        throw new CartNotFoundException();
    }

    @Override
    public Cart addItem(Item item, String cartId, String restaurantId) throws CartNotFoundException {
        Optional<CartEntity> cartById = cartRepository.findCartById(cartId);

        if (cartById.isPresent()) {
            cartById.get().addItem(item);
            cartById.get().setRestaurantId(restaurantId);
            CartEntity cartEntity = cartRepository.save(cartById.get());
            return modelMapperProvider.map(cartEntity, Cart.class);
        }

        throw new CartNotFoundException();
    }

    @Override
    public Cart removeItem(Item item, String cartId, String restaurantId)
            throws CartNotFoundException {
        Optional<CartEntity> cartById = cartRepository.findCartById(cartId);

        if (cartById.isPresent()) {
            cartById.get().removeItem(item);
            if (cartById.get().getItems().size() == 0) {
                cartById.get().setRestaurantId("");
            }
            CartEntity cartEntity = cartRepository.save(cartById.get());
            return modelMapperProvider.map(cartEntity, Cart.class);
        }

        throw new CartNotFoundException();
    }
}