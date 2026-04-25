package exceptions;

public class MixedRestaurantCartException extends FoodiesException {
    public MixedRestaurantCartException(String cartRestaurantName, String itemRestaurantName) {
        super("Cannot add item from '" + itemRestaurantName + "' - cart already contains items from '" + cartRestaurantName + "'.");
    }
}
