package com.javaproject.qeats.repositoryservices;

import com.javaproject.qeats.dto.Cart;
import com.javaproject.qeats.dto.Order;
import com.javaproject.qeats.models.CartEntity;
import com.javaproject.qeats.models.OrderEntity;
import com.javaproject.qeats.repositories.CartRepository;
import com.javaproject.qeats.repositories.OrderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class OrderRepositoryServiceImpl implements OrderRepositoryService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Override
    public Order placeOrder(Cart cart) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setRestaurantId(cart.getRestaurantId());
        orderEntity.setUserId(cart.getUserId());
        orderEntity.setTotal(cart.getTotal());
        orderEntity.setItems(cart.getItems());
        orderEntity.setPlacedTime(LocalTime.now().toString());
        System.out.println("Order Entity {}" + orderEntity);
        Order order = modelMapper.map(orderRepository.save(orderEntity), Order.class);
        CartEntity cartEntity = modelMapper.map(cart, CartEntity.class);
        cartEntity.clearCart();
        cartRepository.save(cartEntity);
        return order;
    }
}
