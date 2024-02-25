package com.javaproject.qeats.controller;


import com.javaproject.qeats.dto.Cart;
import com.javaproject.qeats.dto.Order;
import com.javaproject.qeats.exceptions.EmptyCartException;
import com.javaproject.qeats.exchanges.*;
import com.javaproject.qeats.services.CartAndOrderService;
import com.javaproject.qeats.services.MenuService;
import com.javaproject.qeats.services.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.Optional;

@RestController
@RequestMapping(RestaurantController.RESTAURANT_API_ENDPOINT)
@Validated
public class RestaurantController {

    public static final String RESTAURANT_API_ENDPOINT = "/qeats/v1";
    public static final String RESTAURANTS_API = "/restaurants";
    public static final String MENU_API = "/menu";
    public static final String CART_API = "/cart";
    public static final String CART_ITEM_API = "/cart/item";
    public static final String CART_CLEAR_API = "/cart/clear";
    public static final String POST_ORDER_API = "/order";
    public static final String GET_ORDERS_API = "/orders";

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private CartAndOrderService cartAndOrderService;


    @GetMapping(RESTAURANTS_API)
    public ResponseEntity<GetRestaurantsResponse> getRestaurants(@Valid GetRestaurantsRequest getRestaurantsRequest) {

//        log.info("getRestaurants called with {}", getRestaurantsRequest);
        System.out.println("getRestaurants called with {}" + getRestaurantsRequest);
        GetRestaurantsResponse getRestaurantsResponse;

        if (getRestaurantsRequest.getSearchFor() != null) {
            getRestaurantsResponse = restaurantService.findRestaurantsBySearchQuery(getRestaurantsRequest, LocalTime.now());
        } else {
            getRestaurantsResponse = restaurantService.findAllRestaurantsCloseBy(getRestaurantsRequest, LocalTime.now());
        }
        System.out.println("getRestaurants returned with {}" + getRestaurantsRequest);
        return ResponseEntity.ok().body(getRestaurantsResponse);
    }

    @GetMapping(MENU_API)
    public ResponseEntity<GetMenuResponse> getMenu(@RequestParam("restaurantId") Optional<String> restaurantId) {

        if (!restaurantId.isPresent() || StringUtils.isEmpty(restaurantId.get())) {
            return ResponseEntity.badRequest().build();
        }
        GetMenuResponse getMenuResponse = menuService.findMenu(restaurantId.get());

        if (getMenuResponse == null || getMenuResponse.getMenu() == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().body(getMenuResponse);
    }

    // COMPLETED: CRIO_TASK_MODULE_MENUAPI - Implement GET Cart for the given userId.
    // API URI: /qeats/v1/cart?userId=arun
    // Method: GET
    // Query Params: userId
    // Success Output:
    // 1). If userId is present return user's cart
    //     - If user has an active cart, then return it
    //     - otherwise return an empty cart
    //
    // 2). If userId is not present then respond with BadHttpRequest.
    //
    // HTTP Code: 200
    // {
    //  "id": "10",
    //  "items": [
    //    {
    //      "attributes": [
    //        "South Indian"
    //      ],
    //      "id": "1",
    //      "imageUrl": "www.google.com",
    //      "itemId": "10",
    //      "name": "Idly",
    //      "price": 45
    //    }
    //  ],
    //  "restaurantId": "11",
    //  "total": 45,
    //  "userId": "arun"
    // }
    // Error Response:
    // HTTP Code: 4xx, if client side error.
    //          : 5xx, if server side error.
    // Eg:
    // curl -X GET "http://localhost:8081/qeats/v1/cart?userId=arun"
    @GetMapping(CART_API)
    public ResponseEntity<Cart> getCart(GetCartRequest getCartRequest) {
        System.out.println("getCart {}" + getCartRequest);
        if (StringUtils.isEmpty(getCartRequest.getUserId())) {
            return ResponseEntity.badRequest().build();
        }

        // If user found then return Cart else BAD_REQUEST
        Cart cart;
        try {
            cart = cartAndOrderService.findOrCreateCart(getCartRequest.getUserId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(cart);
    }


    // COMPLETED: CRIO_TASK_MODULE_MENUAPI: Implement add item to cart
    // API URI: /qeats/v1/cart/item
    // Method: POST
    // Request Body format:
//      {
//        "cartId": "1",
//        "itemId": "10",
//        "restaurantId": "11",
//        "userID":"Ram"
//      }
    //
    // Success Output:
    // 1). If user has an active cart, add item to the cart.
    // 2). Otherwise create an empty cart and add the given item.
    // 3). If item to be added is not from same restaurant the 'cartResponseType' should be
    //     QEatsException.ITEM_NOT_FOUND_IN_RESTAURANT_MENU.
    //
    // HTTP Code: 200
    // Response body contains
    //  {
    //    "cart": {
    //      "id": "1",
    //      "items": [
    //        {
    //          "attributes": [
    //            "South Indian"
    //          ],
    //          "id": "1",
    //          "imageUrl": "www.google.com",
    //          "itemId": "10",
    //          "name": "Idly",
    //          "price": 45
    //        }
    //      ],
    //      "restaurantId": "11",
    //      "total": 45,
    //      "userId": "arun"
    //     },
    //     "cartResponseType": 0
    //  }
    // Error Response:
    // HTTP Code: 4xx, if client side error.
    //          : 5xx, if server side error.
    // curl -X GET "http://localhost:8081/qeats/v1/cart/item"
    @PostMapping(CART_ITEM_API)
    public ResponseEntity<CartModifiedResponse> addItem(@RequestBody AddCartRequest addCartRequest) {
        System.out.println("addItem {}" + addCartRequest);
        if (StringUtils.isEmpty(!addCartRequest.isValidRequest())) {
            return ResponseEntity.badRequest().build();
        }

        // If user found then return Cart else BAD_REQUEST
        try {
            String itemId = addCartRequest.getItemId();
            String restaurantId = addCartRequest.getRestaurantId();
            String cartId = addCartRequest.getCartId();
            String userId=addCartRequest.getUserId();
            CartModifiedResponse cart = cartAndOrderService.addItemToCart(itemId, cartId, restaurantId, userId);
            //      if (cart.getCartResponseType() == ITEM_NOT_FOUND_IN_RESTAURANT_MENU) {
            //        return ResponseEntity
            //            .badRequest()
            //            .body(cart);
            //      }
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }


    }


    // COMPLETED: CRIO_TASK_MODULE_MENUAPI: Implement remove item from given cartId
    // API URI: /qeats/v1/cart/item
    // Method: DELETE
    // Request Body format:
    //  {
    //    "cartId": "1",
    //    "itemId": "10",
    //    "restaurantId": "11"
    //  }
    //
    // Success Output:
    // 1). If item is present in user cart, then remove it.
    // 2). Otherwise, do nothing.
    //
    // HTTP Code: 200
    // Response body contains
    //  {
    //    "cart" : {
    //      "id": "1",
    //      "items": [ ],
    //      "restaurantId": "",
    //      "total": 0,
    //      "userId": "arun"
    //     },
    //     "cartResponseType": 0
    //  }
    // Error Response:
    // HTTP Code: 4xx, if client side error.
    //          : 5xx, if server side error.
    // curl -X GET "http://localhost:8081/qeats/v1/cart/item"
    @DeleteMapping(CART_ITEM_API)
    public ResponseEntity<CartModifiedResponse> deleteItem(@RequestBody DeleteCartRequest deleteCartRequest) {
        System.out.println("deleteItem {}" + deleteCartRequest);
        if (StringUtils.isEmpty(!deleteCartRequest.isValidRequest())) {
            return ResponseEntity.badRequest().build();
        }

        String itemId = deleteCartRequest.getItemId();
        String restaurantId = deleteCartRequest.getRestaurantId();
        String cartId = deleteCartRequest.getCartId();

        CartModifiedResponse response = cartAndOrderService.removeItemFromCart(itemId, cartId, restaurantId);

        return ResponseEntity.ok(response);
    }


    // COMPLETED: CRIO_TASK_MODULE_MENUAPI: Place order for the given cartId.
    // API URI: /qeats/v1/order
    // Method: POST
    // Request Body format:
//      {
//        "cartId": "1"
//      }
    //
    // Success Output:
    // 1). Place order for the given cartId and clear the cart.
    // 2). If cart is empty then response should be Bad Http Request.
    //
    // HTTP Code: 200
    // Response body contains
    //  {
    //    "id": "1",
    //    "items": [
    //      {
    //        "attributes": [
    //          "South Indian"
    //        ],
    //        "id": "1",
    //        "imageUrl": "www.google.com",
    //        "itemId": "10",
    //        "name": "Idly",
    //        "price": 45
    //      }
    //    ],
    //    "restaurantId": "11",
    //    "total": 45,
    //    "userId": "arun"
    //  }
    // Error Response:
    // HTTP Code: 4xx, if client side error.
    //          : 5xx, if server side error.
    // curl -X GET "http://localhost:8081/qeats/v1/order"
    @PostMapping(POST_ORDER_API)
    public ResponseEntity<Order> placeOrder(@RequestBody PostOrderRequest postOrderRequest) {
        System.out.println("placeOrder {}" + postOrderRequest);
        if (!postOrderRequest.isValidRequest()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            return ResponseEntity.ok(cartAndOrderService.postOrder(postOrderRequest.getCartId()));
        } catch (EmptyCartException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

