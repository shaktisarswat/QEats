package com.javaproject.qeats.services;


import com.javaproject.qeats.dto.Item;
import com.javaproject.qeats.dto.Menu;
import com.javaproject.qeats.exceptions.ItemNotFoundInRestaurantMenuException;
import com.javaproject.qeats.exchanges.GetMenuResponse;
import com.javaproject.qeats.repositoryservices.MenuRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MenuServiceImpl implements MenuService {

    @Autowired
    MenuRepositoryService menuRepositoryService;

    @Override
    public GetMenuResponse findMenu(String restaurantId) {
        return new GetMenuResponse(menuRepositoryService.findMenu(restaurantId));
    }

    @Override
    public Item findItem(String itemId, String restaurantId) throws ItemNotFoundInRestaurantMenuException {
        Menu menu = menuRepositoryService.findMenu(restaurantId);
        if (menu != null) {
            for (Item item : menu.getItems()) {
                if (itemId.equals(item.getItemId())) {
                    return item;
                }
            }
        }
        throw new ItemNotFoundInRestaurantMenuException("Item not found for given ItemId");
    }
}