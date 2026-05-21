package service;

import exceptions.EntityNotFoundException;
import interfaces.IRestaurantService;
import models.MenuItem;
import models.Restaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RestaurantService implements IRestaurantService {
    private Map<String, Restaurant> restaurants;

    public RestaurantService() {
        this.restaurants = new LinkedHashMap<>();
    }

    @Override
    public void addRestaurant(Restaurant restaurant) {
        restaurants.put(restaurant.getId(), restaurant);
        AuditService.getInstance().log("addRestaurant");
    }

    @Override
    public void addMenuItemToRestaurant(String restaurantId, MenuItem item) {
        Restaurant restaurant = findRestaurantById(restaurantId);
        restaurant.addMenuItem(item);
        AuditService.getInstance().log("addMenuItemToRestaurant");
    }

    @Override
    public List<Restaurant> getRestaurantsSortedByName() {
        List<Restaurant> sorted = new ArrayList<>(restaurants.values());
        Collections.sort(sorted);
        return sorted;
    }

    @Override
    public List<Restaurant> getRestaurantsSortedByRating() {
        List<Restaurant> sorted = new ArrayList<>(restaurants.values());
        sorted.sort(Restaurant.BY_RATING);
        return sorted;
    }

    @Override
    public Restaurant findRestaurantById(String id) {
        Restaurant restaurant = restaurants.get(id);
        if (restaurant == null)
            throw new EntityNotFoundException("Restaurantul " + id + " nu a fost găsit.");
        return restaurant;
    }
}