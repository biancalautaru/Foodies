package service;

import interfaces.IMenuService;
import interfaces.IRestaurantService;
import models.MenuItem;
import models.Restaurant;
import repository.MenuItemRepository;

import java.util.List;

public class MenuService implements IMenuService {
    private final IRestaurantService restaurantService;
    private final MenuItemRepository menuItemRepository;

    public MenuService(IRestaurantService restaurantService, MenuItemRepository menuItemRepository) {
        this.restaurantService = restaurantService;
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public List<MenuItem> getMenu(String restaurantId) {
        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);
        List<MenuItem> items = menuItemRepository.readByRestaurant(restaurantId);
        items.forEach(item -> item.setRestaurant(restaurant));
        return items;
    }
}