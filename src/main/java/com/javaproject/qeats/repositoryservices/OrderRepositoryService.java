package com.javaproject.qeats.repositoryservices;


import com.javaproject.qeats.dto.Cart;
import com.javaproject.qeats.dto.Order;

public interface OrderRepositoryService {


    /**
     * COMPLETED: CRIO_TASK_MODULE_MENUAPI - Implement placeOrder.
     * Place order based on the cart.
     * @param cart - cart to use for placing order.
     * @return return - the order that was just placed.
     */
    Order placeOrder(Cart cart);

}