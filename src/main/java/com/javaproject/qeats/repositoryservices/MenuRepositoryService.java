package com.javaproject.qeats.repositoryservices;


import com.javaproject.qeats.dto.Menu;

public interface MenuRepositoryService {
    /**
     * Return the restaurant menu.
     * @param restaurantId id of the restaurant
     * @return the restaurant's menu
     */
    Menu findMenu(String restaurantId);
}
