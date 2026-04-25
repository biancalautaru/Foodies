package service;

import exceptions.RestaurantNotFoundException;
import models.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
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
        restaurant.addMenuItem(item);
        System.out.println("Item '" + item.getName() + "' added to restaurant " + restaurant.getName() + ".");
    }

    public void displayAllRestaurants() {
        System.out.println("\n===== ALL RESTAURANTS =====");
        for (Restaurant restaurant : restaurants.values())
            System.out.println(restaurant);
        System.out.println("============================\n");
    }

    public List<Restaurant> getRestaurantsSortedByName() {
        List<Restaurant> sorted = new ArrayList<>(restaurants.values());
        Collections.sort(sorted);
        return sorted;
    }

    public List<Restaurant> getRestaurantsSortedByRating() {
        List<Restaurant> sorted = new ArrayList<>(restaurants.values());
        sorted.sort(Restaurant.BY_RATING);
        return sorted;
    }

    public void displayRestaurantsSortedByName() {
        System.out.println("\n===== ALL RESTAURANTS (A-Z) =====");
        for (Restaurant restaurant : getRestaurantsSortedByName())
            System.out.println(restaurant);
        System.out.println("=================================\n");
    }

    public void displayRestaurantsSortedByRating() {
        System.out.println("\n===== ALL RESTAURANTS (top rated first) =====");
        for (Restaurant restaurant : getRestaurantsSortedByRating())
            System.out.println(restaurant);
        System.out.println("=============================================\n");
    }

    public void displayRestaurantMenu(String restaurantId) {
        Restaurant restaurant = findRestaurantById(restaurantId);
        System.out.println("\n===== MENU: " + restaurant.getName() + " =====");
        for (MenuItem item : restaurant.getMenu())
            System.out.println("  " + item);
        System.out.println("==========================\n");
    }

    public Restaurant findRestaurantById(String id) {
        Restaurant restaurant = restaurants.get(id);
        if (restaurant == null)
            throw new RestaurantNotFoundException(id);
        return restaurant;
    }
}
