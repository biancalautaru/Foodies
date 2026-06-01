package service;

import exceptions.EntityNotFoundException;
import models.MenuItem;
import models.Restaurant;
import repository.MenuItemRepository;
import repository.RestaurantRepository;
import repository.ReviewRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final ReviewRepository reviewRepository;

    public RestaurantService() {
        this.restaurantRepository = RestaurantRepository.getInstance();
        this.menuItemRepository = MenuItemRepository.getInstance();
        this.reviewRepository = ReviewRepository.getInstance();
    }

    public void addRestaurant(Restaurant restaurant) {
        if (restaurant.getAddress().getId() == null)
            restaurant.getAddress().setId(UUID.randomUUID().toString());
        restaurantRepository.create(restaurant);
        AuditService.getInstance().log("addRestaurant");
    }

    public void addMenuItemToRestaurant(String restaurantId, MenuItem item) {
        Restaurant restaurant = findRestaurantById(restaurantId);
        item.setRestaurant(restaurant);
        menuItemRepository.create(item);
        AuditService.getInstance().log("addMenuItemToRestaurant");
    }

    public List<MenuItem> getMenu(String restaurantId) {
        Restaurant restaurant = findRestaurantById(restaurantId);
        List<MenuItem> items = menuItemRepository.readByRestaurant(restaurantId);
        items.forEach(item -> item.setRestaurant(restaurant));
        return items;
    }

    public List<Restaurant> getRestaurantsSortedByName() {
        List<Restaurant> sorted = loadAllWithReviews();
        Collections.sort(sorted);
        return sorted;
    }

    public List<Restaurant> getRestaurantsSortedByRating() {
        List<Restaurant> sorted = loadAllWithReviews();
        sorted.sort(Restaurant.BY_RATING);
        return sorted;
    }

    private List<Restaurant> loadAllWithReviews() {
        List<Restaurant> restaurants = new ArrayList<>(restaurantRepository.readAll());
        for (Restaurant r : restaurants)
            r.setReviews(reviewRepository.readByRestaurant(r.getId()));
        return restaurants;
    }

    public Restaurant findRestaurantById(String id) {
        Restaurant restaurant = restaurantRepository.read(id);
        if (restaurant == null)
            throw new EntityNotFoundException("Restaurantul " + id + " nu a fost găsit.");
        restaurant.setReviews(reviewRepository.readByRestaurant(id));
        return restaurant;
    }
}