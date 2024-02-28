/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.javaproject.qeats.repositoryservices;


import ch.hsr.geohash.GeoHash;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaproject.qeats.configs.RedisConfiguration;
import com.javaproject.qeats.dto.Restaurant;
import com.javaproject.qeats.globals.GlobalConstants;
import com.javaproject.qeats.models.RestaurantEntity;
import com.javaproject.qeats.repositories.MenuRepository;
import com.javaproject.qeats.repositories.RestaurantRepository;
import com.javaproject.qeats.utils.GeoLocation;
import com.javaproject.qeats.utils.GeoUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class RestaurantRepositoryServiceImpl implements RestaurantRepositoryService {

    @Autowired
    RestaurantRepository restaurantRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private RedisConfiguration redisConfiguration;

    @Autowired
    private MenuRepository menuRepository;

    private boolean isOpenNow(LocalTime time, RestaurantEntity res) {
        LocalTime openingTime = LocalTime.parse(res.getOpensAt());
        LocalTime closingTime = LocalTime.parse(res.getClosesAt());

        return time.isAfter(openingTime) && time.isBefore(closingTime);
    }

    public List<Restaurant> findAllRestaurantsCloseBy(Double latitude, Double longitude,
                                                      LocalTime currentTime, Double servingRadiusInKms) {
        // We want to use cache to speed things up. Write methods that perform the same functionality,
        // but using the cache if it is present and reachable.
        // Remember, you must ensure that if cache is not present, the queries are directed at the
        // database instead.
        List<Restaurant> restaurants = null;
        if (redisConfiguration.isCacheAvailable()) {
            restaurants =
                    findAllRestaurantsCloseByFromCache(latitude, longitude, currentTime, servingRadiusInKms);
        } else {
            restaurants =
                    findAllRestaurantsCloseFromDb(latitude, longitude, currentTime, servingRadiusInKms);
        }
        return restaurants;
    }


    private List<Restaurant> findAllRestaurantsCloseByFromCache(Double latitude, Double longitude,
                                                                LocalTime currentTime, Double servingRadiusInKms) {
        List<Restaurant> restaurantList = new ArrayList<>();

        GeoLocation geoLocation = new GeoLocation(latitude, longitude);
        GeoHash geoHash =
                GeoHash.withCharacterPrecision(geoLocation.getLatitude(), geoLocation.getLongitude(), 7);

        try (Jedis jedis = redisConfiguration.getJedisPool().getResource()) {
            String jsonStringFromCache = jedis.get(geoHash.toBase32());

            if (jsonStringFromCache == null) {
                // Cache needs to be updated.
                String createdJsonString = "";
                try {
                    restaurantList = findAllRestaurantsCloseFromDb(geoLocation.getLatitude(),
                            geoLocation.getLongitude(), currentTime, servingRadiusInKms);
                    createdJsonString = new ObjectMapper().writeValueAsString(restaurantList);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                // Do operations with jedis resource
                jedis.setex(geoHash.toBase32(), GlobalConstants.REDIS_ENTRY_EXPIRY_IN_SECONDS,
                        createdJsonString);
            } else {
                try {
                    restaurantList = new ObjectMapper().readValue(jsonStringFromCache,
                            new TypeReference<List<Restaurant>>() {
                            });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return restaurantList;
    }

    private List<Restaurant> findAllRestaurantsCloseFromDb(Double latitude, Double longitude,
                                                           LocalTime currentTime, Double servingRadiusInKms) {
        List<RestaurantEntity> restaurantEntities = restaurantRepository.findAll();
        List<Restaurant> restaurants = new ArrayList<Restaurant>();
        for (RestaurantEntity restaurantEntity : restaurantEntities) {
            if (isRestaurantCloseByAndOpen(restaurantEntity, currentTime, latitude, longitude,
                    servingRadiusInKms)) {
                restaurants.add(modelMapper.map(restaurantEntity, Restaurant.class));
            }
        }
        return restaurants;
    }

    // Objective:
    // Find restaurants whose names have an exact or partial match with the search query.
    @Override
    public List<Restaurant> findRestaurantsByName(Double latitude, Double longitude,
                                                  String searchString, LocalTime currentTime, Double servingRadiusInKms) {
        Optional<List<RestaurantEntity>> restaurantEntities =
                restaurantRepository.findRestaurantsByNameExact(searchString);
        List<Restaurant> restaurants = new ArrayList<>();
        if (restaurantEntities.get() != null) {
            for (RestaurantEntity restaurantEntity : restaurantEntities.get()) {
                if (isRestaurantCloseByAndOpen(restaurantEntity, currentTime, latitude, longitude,
                        servingRadiusInKms)) {
                    restaurants.add(modelMapper.map(restaurantEntity, Restaurant.class));
                }
            }
        }

        return restaurants;
    }


    // Objective:
    // Find restaurants whose attributes (cuisines) intersect with the search query.
    @Override
    public List<Restaurant> findRestaurantsByAttributes(Double latitude, Double longitude,
                                                        String searchString, LocalTime currentTime, Double servingRadiusInKms) {

        Optional<List<RestaurantEntity>> restaurantEntities =
                restaurantRepository.findRestaurantsByAttribute(searchString);

        List<Restaurant> restaurants = new ArrayList<>();
        if (restaurantEntities.get() != null) {
            for (RestaurantEntity restaurantEntity : restaurantEntities.get()) {
                if (isRestaurantCloseByAndOpen(restaurantEntity, currentTime, latitude, longitude,
                        servingRadiusInKms)) {
                    restaurants.add(modelMapper.map(restaurantEntity, Restaurant.class));
                }
            }
        }
        return restaurants;
    }


    // Objective:
    // Find restaurants which serve food items whose names form a complete or partial match
    // with the search query.

    @Override
    public List<Restaurant> findRestaurantsByItemName(Double latitude, Double longitude,
                                                      String searchString, LocalTime currentTime,
                                                      Double servingRadiusInKms) {
        Optional<List<RestaurantEntity>> restaurantIds =
                menuRepository.findRestaurantsByItemName(searchString);
        List<Restaurant> restaurants = new ArrayList<>();
        if (restaurantIds.isPresent()) {
            for (RestaurantEntity id : restaurantIds.get()) {
                Optional<RestaurantEntity> restaurant = restaurantRepository.findRestaurantsByRestaurantId(id.getRestaurantId());
                if (restaurant.isPresent()) {
                    if (isRestaurantCloseByAndOpen(restaurant.get(), currentTime, latitude, longitude,
                            servingRadiusInKms)) {
                        restaurants.add(modelMapper.map(restaurant.get(), Restaurant.class));
                    }
                }
            }
        }
        return restaurants;
    }

    // Objective:
    // Find restaurants which serve food items whose attributes intersect with the search query.
    @Override
    public List<Restaurant> findRestaurantsByItemAttributes(Double latitude, Double longitude,
                                                            String searchString, LocalTime currentTime, Double servingRadiusInKms) {
        Optional<List<RestaurantEntity>> restaurantIds =
                menuRepository.findRestaurantIdsByItemAttributes(searchString);
        List<Restaurant> restaurants = new ArrayList<>();
        if (restaurantIds.isPresent()) {
            for (RestaurantEntity id : restaurantIds.get()) {
                Optional<RestaurantEntity> restaurant = restaurantRepository.findRestaurantsByRestaurantId(id.getRestaurantId());
                if (restaurant.isPresent()) {
                    if (isRestaurantCloseByAndOpen(restaurant.get(), currentTime, latitude, longitude,
                            servingRadiusInKms)) {
                        restaurants.add(modelMapper.map(restaurant.get(), Restaurant.class));
                    }
                }
            }
        }
        return restaurants;
    }


    /**
     * Utility method to check if a restaurant is within the serving radius at a given time.
     *
     * @return boolean True if restaurant falls within serving radius and is open, false otherwise
     */
    private boolean isRestaurantCloseByAndOpen(RestaurantEntity restaurantEntity,
                                               LocalTime currentTime, Double latitude, Double longitude, Double servingRadiusInKms) {
        if (isOpenNow(currentTime, restaurantEntity)) {
            return GeoUtils.findDistanceInKm(latitude, longitude,
                    restaurantEntity.getLatitude(), restaurantEntity.getLongitude())
                    < servingRadiusInKms;
        }
        return false;
    }


}

