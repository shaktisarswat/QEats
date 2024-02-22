package com.javaproject.qeats.services;

import com.javaproject.qeats.dto.Restaurant;
import com.javaproject.qeats.exchanges.GetRestaurantsRequest;
import com.javaproject.qeats.exchanges.GetRestaurantsResponse;
import com.javaproject.qeats.repositoryservices.RestaurantRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final Double peakHoursServingRadiusInKms = 3.0;
    private final Double normalHoursServingRadiusInKms = 5.0;
    @Autowired
    private RestaurantRepositoryService restaurantRepositoryService;


    @Override
    public GetRestaurantsResponse findAllRestaurantsCloseBy(
            GetRestaurantsRequest getRestaurantsRequest, LocalTime currentTime) {

        // For peak hours: 8AM - 10AM, 1PM-2PM, 7PM-9PM service radius is 3KMs.
        // All other times, serving radius is 5KMs.
        List<Restaurant> restaurant;
        int h = currentTime.getHour();
        int m = currentTime.getMinute();

        if ((h >= 8 && h <= 9) || (h == 10 && m == 0) || (h == 13) || (h == 14 && m == 0)
                || (h >= 19 && h <= 20) || (h == 21 && m == 0)) {
            restaurant =
                    restaurantRepositoryService.findAllRestaurantsCloseBy(getRestaurantsRequest.getLatitude(),
                            getRestaurantsRequest.getLongitude(), currentTime, peakHoursServingRadiusInKms);
        } else {
            restaurant =
                    restaurantRepositoryService.findAllRestaurantsCloseBy(getRestaurantsRequest.getLatitude(),
                            getRestaurantsRequest.getLongitude(), currentTime, normalHoursServingRadiusInKms);
        }

        for (Restaurant res : restaurant) {
            String sanitizedName = res.getName().replaceAll("[Â©éí]", "e");
            res.setName(sanitizedName);
        }

        GetRestaurantsResponse response = new GetRestaurantsResponse(restaurant);

        // log.info(response);
        return response;
    }


    // Implement findRestaurantsBySearchQuery. The request object has the search string.
    // We have to combine results from multiple sources:
    // 1. Restaurants by name (exact and inexact)
    // 2. Restaurants by cuisines (also called attributes)
    // 3. Restaurants by food items it serves
    // 4. Restaurants by food item attributes (spicy, sweet, etc)
    // Remember, a restaurant must be present only once in the resulting list.
    // Check RestaurantService.java file for the interface contract.
    @Override
    public GetRestaurantsResponse findRestaurantsBySearchQuery(
            GetRestaurantsRequest getRestaurantsRequest, LocalTime currentTime) {
        if (getRestaurantsRequest == null || getRestaurantsRequest.getSearchFor().equals("")) {
            return findAllRestaurantsCloseBy(getRestaurantsRequest, currentTime);
        }

        List<Restaurant> restaurants = new ArrayList<>();
        final Double latitude = getRestaurantsRequest.getLatitude();
        final Double longitude = getRestaurantsRequest.getLongitude();
        final String searchQuery = getRestaurantsRequest.getSearchFor();
        Double servingRadiusInKms = normalHoursServingRadiusInKms;
        if (isPeekHour(currentTime)) {
            servingRadiusInKms = peakHoursServingRadiusInKms;
        }

        final Double finalServingRadiusInKms = servingRadiusInKms;


        // Restaurants by Name
        List<Restaurant> completableFuture1 =
                getRestaurantsByName(currentTime, latitude, longitude, searchQuery,
                        finalServingRadiusInKms);

        // Restaurants by Cuisines (Attributes)
        List<Restaurant> completableFuture2 =
                getRestaurantsByAttributes(currentTime, latitude, longitude, searchQuery,
                        finalServingRadiusInKms);

        // Restaurants by Food Item
        List<Restaurant> completableFuture3 =
                getRestaurantsByItemName(currentTime, latitude, longitude, searchQuery,
                        finalServingRadiusInKms);

        // Restaurants by Food Item Attributes
        List<Restaurant> completableFuture4 =
                getRestaurantsByItemAttributes(currentTime, latitude, longitude, searchQuery,
                        finalServingRadiusInKms);

        restaurants.addAll(completableFuture1);
        restaurants.addAll(completableFuture2);
        restaurants.addAll(completableFuture3);
        restaurants.addAll(completableFuture4);

        for (Restaurant res : restaurants) {
            String sanitizedName = res.getName().replaceAll("[Â©éí]", "e");
            res.setName(sanitizedName);
        }

        // restaurants.forEach(restaurant -> {
        //   restaurant.setName(StringUtils.stripAccents(restaurant.getName()));
        // });
        return new GetRestaurantsResponse(restaurants);
    }

    private List<Restaurant> getRestaurantsByItemAttributes(
            LocalTime currentTime, Double latitude, Double longitude, String searchQuery,
            Double finalServingRadiusInKms) {
        return restaurantRepositoryService
                .findRestaurantsByItemAttributes(latitude, longitude, searchQuery, currentTime,
                        finalServingRadiusInKms);
    }

    private List<Restaurant> getRestaurantsByItemName(
            LocalTime currentTime, Double latitude, Double longitude, String searchQuery,
            Double finalServingRadiusInKms) {
        return restaurantRepositoryService
                .findRestaurantsByItemName(latitude, longitude, searchQuery, currentTime,
                        finalServingRadiusInKms);
    }

    private List<Restaurant> getRestaurantsByAttributes(
            LocalTime currentTime, Double latitude, Double longitude, String searchQuery,
            Double finalServingRadiusInKms) {
        return restaurantRepositoryService
                .findRestaurantsByAttributes(latitude, longitude, searchQuery, currentTime,
                        finalServingRadiusInKms);
    }

    private List<Restaurant> getRestaurantsByName(
            LocalTime currentTime, Double latitude, Double longitude, String searchQuery,
            Double finalServingRadiusInKms) {
        return restaurantRepositoryService
                .findRestaurantsByName(latitude, longitude, searchQuery, currentTime,
                        finalServingRadiusInKms);
    }


    private boolean isPeekHour(LocalTime currentTime) {
        LocalTime s1 = LocalTime.of(8, 0);
        LocalTime e1 = LocalTime.of(10, 0);

        LocalTime s2 = LocalTime.of(13, 0);
        LocalTime e2 = LocalTime.of(14, 0);

        LocalTime s3 = LocalTime.of(19, 0);
        LocalTime e3 = LocalTime.of(21, 0);

        return (currentTime.isAfter(s1) && currentTime.isBefore(e1))
                || (currentTime.isAfter(s2) && currentTime.isBefore(e2))
                || (currentTime.isAfter(s3) && currentTime.isBefore(e3))
                || currentTime.equals(s1) || currentTime.equals(e1)
                || currentTime.equals(s2) || currentTime.equals(e2)
                || currentTime.equals(s3) || currentTime.equals(e3);
    }

    // COMPLETED: CRIO_TASK_MODULE_MULTITHREADING: Implement multi-threaded version of
    // RestaurantSearch.
    // Implement variant of findRestaurantsBySearchQuery which is at least 1.5x time faster than
    // findRestaurantsBySearchQuery.

    public GetRestaurantsResponse findRestaurantsBySearchQueryMt(
            GetRestaurantsRequest getRestaurantsRequest, LocalTime currentTime) {

        if (getRestaurantsRequest.getSearchFor() == null) {
            return findAllRestaurantsCloseBy(getRestaurantsRequest, currentTime);
        }

        List<Restaurant> restaurants = new ArrayList<>();
        final Double latitude = getRestaurantsRequest.getLatitude();
        final Double longitude = getRestaurantsRequest.getLongitude();
        final String searchQuery = getRestaurantsRequest.getSearchFor();
        Double servingRadiusInKms = normalHoursServingRadiusInKms;
        if (isPeekHour(currentTime)) {
            servingRadiusInKms = peakHoursServingRadiusInKms;
        }

        // Restaurants by Name
        restaurants.addAll(restaurantRepositoryService.findRestaurantsByName(latitude, longitude,
                searchQuery, currentTime, servingRadiusInKms));

        // Restaurants by Cuisines (Attributes)
        restaurants.addAll(restaurantRepositoryService.findRestaurantsByAttributes(latitude,
                longitude, searchQuery, currentTime, servingRadiusInKms));

        // Restaurants by Food Item
        restaurants.addAll(restaurantRepositoryService.findRestaurantsByItemName(latitude,
                longitude, searchQuery, currentTime, servingRadiusInKms));

        // Restaurants by Food Item Attributes
        restaurants.addAll(restaurantRepositoryService.findRestaurantsByItemAttributes(latitude,
                longitude, searchQuery, currentTime, servingRadiusInKms));
        return new GetRestaurantsResponse(restaurants);
    }

}

