package service;

import models.MenuItem;
import models.Restaurant;

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

    public void displayMenuSortedByName(String restaurantId) {
        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);
        System.out.println("\n===== MENU (A-Z): " + restaurant.getName() + " =====");
        for (MenuItem item : getMenuSortedByName(restaurantId))
            System.out.println("  " + item);
        System.out.println("==========================\n");
    }

    public void displayMenuSortedByPrice(String restaurantId) {
        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);
        System.out.println("\n===== MENU (cheapest first): " + restaurant.getName() + " =====");
        for (MenuItem item : getMenuSortedByPrice(restaurantId))
            System.out.println("  " + item);
        System.out.println("==========================\n");
    }
}
