package com.crio.qeats.repositoryservices;

import ch.hsr.geohash.GeoHash;
import com.crio.qeats.utils.FixtureHelpers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaproject.qeats.QeatsApplication;
import com.javaproject.qeats.configs.RedisConfiguration;
import com.javaproject.qeats.dto.Restaurant;
import com.javaproject.qeats.models.RestaurantEntity;
import com.javaproject.qeats.repositories.RestaurantRepository;
import com.javaproject.qeats.repositoryservices.RestaurantRepositoryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import redis.clients.jedis.Jedis;
import redis.embedded.RedisServer;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {QeatsApplication.class})
@DirtiesContext
@ActiveProfiles("test")
class RestaurantRepositoryServiceCacheTest {

    private static final String FIXTURES = "fixtures/exchanges";

    @Autowired
    private RestaurantRepositoryService restaurantRepositoryService;
    @Autowired
    private RedisConfiguration redisConfiguration;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${spring.redis.port}")
    private int redisPort;

    private RedisServer server = null;

    @MockBean
    private RestaurantRepository mockRestaurantRepository;

    @AfterEach
    void teardown() {
        redisConfiguration.destroyCache();
    }


    @Test
    void restaurantsCloseByFromWarmCache(@Autowired MongoTemplate mongoTemplate) throws IOException {
        assertNotNull(mongoTemplate);
        assertNotNull(restaurantRepositoryService);

        when(mockRestaurantRepository.findAll()).thenReturn(listOfRestaurants());

        Jedis jedis = redisConfiguration.getJedisPool().getResource();

        // call it twice
        List<Restaurant> allRestaurantsCloseBy = restaurantRepositoryService
                .findAllRestaurantsCloseBy(20.0, 30.0, LocalTime.of(18, 1), 3.0);
        allRestaurantsCloseBy = restaurantRepositoryService
                .findAllRestaurantsCloseBy(20.0, 30.0, LocalTime.of(18, 1), 3.0);
        GeoHash geoHash = GeoHash.withCharacterPrecision(20.0, 30.0, 7);

        verify(mockRestaurantRepository, times(1)).findAll();
        assertNotNull(jedis.get(geoHash.toBase32()));
        assertEquals(2, allRestaurantsCloseBy.size());
        assertEquals("11", allRestaurantsCloseBy.get(0).getRestaurantId());
        assertEquals("12", allRestaurantsCloseBy.get(1).getRestaurantId());
    }

    private List<RestaurantEntity> listOfRestaurants() throws IOException {
        String fixture =
                FixtureHelpers.fixture(FIXTURES + "/initial_data_set_restaurants.json");

        return objectMapper.readValue(fixture, new TypeReference<List<RestaurantEntity>>() {
        });
    }
}
