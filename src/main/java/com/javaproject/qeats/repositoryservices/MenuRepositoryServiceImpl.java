package com.javaproject.qeats.repositoryservices;

import com.javaproject.qeats.dto.Menu;
import com.javaproject.qeats.models.MenuEntity;
import com.javaproject.qeats.repositories.MenuRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MenuRepositoryServiceImpl implements MenuRepositoryService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ModelMapper modelMapperProvider;

    public Menu findMenu(String restaurantId) {
        ModelMapper modelMapper = modelMapperProvider;
        Optional<MenuEntity> menuById = menuRepository.findMenuByRestaurantId(restaurantId);
        Menu menu = null;
        if (menuById.isPresent()) {
            menu = modelMapper.map(menuById.get(), Menu.class);
        }
        return menu;
    }
}