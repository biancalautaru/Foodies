package service;

import models.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class RestaurantService {
    private Map<String, Restaurant> restaurants;

    public RestaurantService() {
        this.restaurants = new LinkedHashMap<>();
    }

    public void addRestaurant(Restaurant restaurant) {
        restaurants.put(restaurant.getId(), restaurant);
        System.out.println("Restaurant added: " + restaurant.getName());
    }

    public void addMenuItemToRestaurant(String restaurantId, MenuItem item) {
        Restaurant restaurant = findRestaurantById(restaurantId);
        if (restaurant == null) {
            System.out.println("Error: Restaurant not found.");
            return;
        }

        restaurant.addMenuItem(item);
        System.out.println("Item '" + item.getName() + "' added to restaurant " + restaurant.getName() + ".");
    }

    public void displayAllRestaurants() {
        System.out.println("\n===== ALL RESTAURANTS =====");
        for (Restaurant restaurant : restaurants.values())
            System.out.println(restaurant);
        System.out.println("============================\n");
    }

    public void displayRestaurantMenu(String restaurantId) {
        Restaurant restaurant = findRestaurantById(restaurantId);
        if (restaurant == null) {
            System.out.println("Error: Restaurant not found.");
            return;
        }

        System.out.println("\n===== MENU: " + restaurant.getName() + " =====");
        for (MenuItem item : restaurant.getMenu())
            System.out.println("  " + item);
        System.out.println("==========================\n");
    }

    public Restaurant findRestaurantById(String id) {
        return restaurants.get(id);
    }
}
