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
        System.out.println("Restaurant adăugat: " + restaurant.getName());
    }

    public void addMenuItemToRestaurant(String restaurantId, MenuItem item) {
        Restaurant restaurant = findRestaurantById(restaurantId);
        restaurant.addMenuItem(item);
        System.out.println("Produsul '" + item.getName() + "' a fost adăugat la restaurantul " + restaurant.getName() + ".");
    }

    public void displayAllRestaurants() {
        System.out.println("\n===== TOATE RESTAURANTELE =====");
        int index = 1;
        for (Restaurant restaurant : restaurants.values())
            System.out.println(index++ + ". " + restaurant);
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
        System.out.println("\n===== TOATE RESTAURANTELE (A-Z) =====");
        int index = 1;
        for (Restaurant restaurant : getRestaurantsSortedByName())
            System.out.println(index++ + ". " + restaurant);
        System.out.println("=================================\n");
    }

    public void displayRestaurantsSortedByRating() {
        System.out.println("\n===== TOATE RESTAURANTELE (cel mai bine evaluate primele) =====");
        int index = 1;
        for (Restaurant restaurant : getRestaurantsSortedByRating())
            System.out.println(index++ + ". " + restaurant);
        System.out.println("=============================================\n");
    }

    public void displayRestaurantMenu(String restaurantId) {
        Restaurant restaurant = findRestaurantById(restaurantId);
        System.out.println("\n===== MENIU: " + restaurant.getName() + " =====");
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
