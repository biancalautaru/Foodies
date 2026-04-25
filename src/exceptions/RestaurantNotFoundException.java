package exceptions;

public class RestaurantNotFoundException extends FoodiesException {
    public RestaurantNotFoundException(String restaurantId) {
        super("Restaurant not found: " + restaurantId);
    }
}
