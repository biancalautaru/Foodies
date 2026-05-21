package interfaces;

import models.MenuItem;
import models.Restaurant;

import java.util.List;

public interface IRestaurantService {
    void addRestaurant(Restaurant restaurant);
    void addMenuItemToRestaurant(String restaurantId, MenuItem item);
    List<Restaurant> getRestaurantsSortedByName();
    List<Restaurant> getRestaurantsSortedByRating();
    Restaurant findRestaurantById(String id);
}