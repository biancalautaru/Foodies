package service;

import models.*;

public class RestaurantService {
    private Restaurant[] restaurants;
    private int restaurantCount;

    public RestaurantService(int maxRestaurants) {
        this.restaurants = new Restaurant[maxRestaurants];
        this.restaurantCount = 0;
    }

    public void addRestaurant(Restaurant restaurant) {
        if (restaurantCount == restaurants.length) {
            System.out.println("Error: Maximum restaurants reached.");
            return;
        }

        restaurants[restaurantCount++] = restaurant;
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
        for (int i = 0; i < restaurantCount; i++)
            System.out.println(restaurants[i]);
        System.out.println("============================\n");
    }

    public void displayRestaurantMenu(String restaurantId) {
        Restaurant restaurant = findRestaurantById(restaurantId);
        if (restaurant == null) {
            System.out.println("Error: Restaurant not found.");
            return;
        }

        System.out.println("\n===== MENU: " + restaurant.getName() + " =====");
        MenuItem[] menu = restaurant.getMenu();
        for (int i = 0; i < restaurant.getMenuCount(); i++)
            System.out.println("  " + menu[i]);
        System.out.println("==========================\n");
    }

    public Restaurant findRestaurantById(String id) {
        for (int i = 0; i < restaurantCount; i++)
            if (restaurants[i].getId().equals(id))
                return restaurants[i];
        return null;
    }
}
