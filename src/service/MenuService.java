package service;

import models.MenuItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MenuService {
    private final RestaurantService restaurantService;

    public MenuService(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    public List<MenuItem> getMenuSortedByName(String restaurantId) {
        List<MenuItem> sorted = new ArrayList<>(restaurantService.findRestaurantById(restaurantId).getMenu());
        Collections.sort(sorted);
        return sorted;
    }

    public List<MenuItem> getMenuSortedByPrice(String restaurantId) {
        List<MenuItem> sorted = new ArrayList<>(restaurantService.findRestaurantById(restaurantId).getMenu());
        sorted.sort(MenuItem.BY_PRICE);
        return sorted;
    }

}