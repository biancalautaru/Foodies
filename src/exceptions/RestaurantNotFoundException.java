package exceptions;

public class RestaurantNotFoundException extends FoodiesException {
    public RestaurantNotFoundException(String restaurantId) {
        super("Restaurantul " + restaurantId + " nu a fost găsit.");
    }
}
