
package com.crio.qeats.services;

import com.crio.qeats.utils.FixtureHelpers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaproject.qeats.dto.Restaurant;
import com.javaproject.qeats.exchanges.GetRestaurantsRequest;
import com.javaproject.qeats.exchanges.GetRestaurantsResponse;
import com.javaproject.qeats.repositoryservices.RestaurantRepositoryService;
import com.javaproject.qeats.services.RestaurantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceMockitoTestStub {

  protected static final String FIXTURES = "fixtures/exchanges";

  protected ObjectMapper objectMapper = new ObjectMapper();

  protected Restaurant restaurant1;
  protected Restaurant restaurant2;
  protected Restaurant restaurant3;
  protected Restaurant restaurant4;
  protected Restaurant restaurant5;

  @InjectMocks
  protected RestaurantServiceImpl restaurantService;

  @Mock
  protected RestaurantRepositoryService restaurantRepositoryServiceMock;

  @BeforeEach
  public void initializeRestaurantObjects() throws IOException {
    String fixture = FixtureHelpers.fixture(FIXTURES + "/mocking_list_of_restaurants.json");
    Restaurant[] restaurants = objectMapper.readValue(fixture, Restaurant[].class);
    restaurant1 = restaurants[0];
    restaurant2 = restaurants[1];
    restaurant3 = restaurants[2];
    restaurant4 = restaurants[3];
    restaurant5 = restaurants[4];
  }



  @Test
  public void testFindNearbyWithin5km() throws IOException {
    doReturn(Arrays.asList(restaurant1,restaurant2)).when(restaurantRepositoryServiceMock).findAllRestaurantsCloseBy(
        any(Double.class), any(Double.class), any(LocalTime.class), eq(5.0));

    GetRestaurantsResponse allRestaurantsCloseBy = restaurantService
        .findAllRestaurantsCloseBy(new GetRestaurantsRequest(20.0, 30.0), LocalTime.of(3, 0));

    assertEquals(2, allRestaurantsCloseBy.getRestaurants().size());
    assertEquals("11", allRestaurantsCloseBy.getRestaurants().get(0).getRestaurantId());
    assertEquals("12", allRestaurantsCloseBy.getRestaurants().get(1).getRestaurantId());

    ArgumentCaptor<Double> servingRadiusInKms = ArgumentCaptor.forClass(Double.class);
    verify(restaurantRepositoryServiceMock, times(1)).findAllRestaurantsCloseBy(any(Double.class),
        any(Double.class), any(LocalTime.class), servingRadiusInKms.capture());

  }


  @Test
  public void testFindNearbyWithin3km() throws IOException {

    List<Restaurant> restaurantList1 = new ArrayList<>();
    List<Restaurant> restaurantList2 = new ArrayList<>();
    // Initialize these two lists above such that I will match with the assert statements
    // defined below.

    restaurantList1.add(restaurant1);
    restaurantList1.add(restaurant4);

    restaurantList2.add(restaurant2);
    restaurantList2.add(restaurant4);


    lenient().doReturn(restaurantList1).when(restaurantRepositoryServiceMock)
        .findAllRestaurantsCloseBy(eq(20.0), eq(30.2), eq(LocalTime.of(3, 0)), eq(5.0));

    lenient().doReturn(restaurantList2).when(restaurantRepositoryServiceMock)
        .findAllRestaurantsCloseBy(eq(21.0), eq(31.1), eq(LocalTime.of(19, 0)), eq(3.0));

    GetRestaurantsResponse allRestaurantsCloseByOffPeakHours;
    GetRestaurantsResponse allRestaurantsCloseByPeakHours;

    allRestaurantsCloseByOffPeakHours = restaurantService
        .findAllRestaurantsCloseBy(new GetRestaurantsRequest(20.0, 30.2), LocalTime.of(3, 0));
    allRestaurantsCloseByPeakHours = restaurantService
        .findAllRestaurantsCloseBy(new GetRestaurantsRequest(21.0, 31.1), LocalTime.of(19, 0));

    assertEquals(2, allRestaurantsCloseByOffPeakHours.getRestaurants().size());
    assertEquals("11", allRestaurantsCloseByOffPeakHours.getRestaurants().get(0).getRestaurantId());
    assertEquals("14", allRestaurantsCloseByOffPeakHours.getRestaurants().get(1).getRestaurantId());


    assertEquals(2, allRestaurantsCloseByPeakHours.getRestaurants().size());
    assertEquals("12", allRestaurantsCloseByPeakHours.getRestaurants().get(0).getRestaurantId());
    assertEquals("14", allRestaurantsCloseByPeakHours.getRestaurants().get(1).getRestaurantId());
  }

}

