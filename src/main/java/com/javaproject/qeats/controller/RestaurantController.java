package com.javaproject.qeats.controller;


import com.javaproject.qeats.exchanges.GetRestaurantsRequest;
import com.javaproject.qeats.exchanges.GetRestaurantsResponse;
import com.javaproject.qeats.services.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;

@RestController
@RequestMapping(RestaurantController.RESTAURANT_API_ENDPOINT)
//@Log4j2
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
//        log.info("getRestaurants returned {}", getRestaurantsResponse);
        System.out.println("getRestaurants returned with {}" + getRestaurantsRequest);
        return ResponseEntity.ok().body(getRestaurantsResponse);
    }
}

