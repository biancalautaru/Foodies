package service;

import exceptions.EntityNotFoundException;
import interfaces.IRestaurantService;
import models.MenuItem;
import models.Restaurant;
import repository.MenuItemRepository;
import repository.RestaurantRepository;
import repository.ReviewRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class RestaurantService implements IRestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final ReviewRepository reviewRepository;

    public RestaurantService(RestaurantRepository restaurantRepository,
                             MenuItemRepository menuItemRepository,
                             ReviewRepository reviewRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void addRestaurant(Restaurant restaurant) {
        if (restaurant.getAddress().getId() == null)
            restaurant.getAddress().setId(UUID.randomUUID().toString());
        restaurantRepository.create(restaurant);
        AuditService.getInstance().log("addRestaurant");
    }

    @Override
    public void addMenuItemToRestaurant(String restaurantId, MenuItem item) {
        Restaurant restaurant = findRestaurantById(restaurantId);
        item.setRestaurant(restaurant);
        menuItemRepository.create(item);
        AuditService.getInstance().log("addMenuItemToRestaurant");
    }

    @Override
    public List<Restaurant> getRestaurantsSortedByName() {
        List<Restaurant> sorted = loadAllWithReviews();
        Collections.sort(sorted);
        return sorted;
    }

    @Override
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

    @Override
    public Restaurant findRestaurantById(String id) {
        Restaurant restaurant = restaurantRepository.read(id);
        if (restaurant == null)
            throw new EntityNotFoundException("Restaurantul " + id + " nu a fost găsit.");
        restaurant.setReviews(reviewRepository.readByRestaurant(id));
        return restaurant;
    }
}