package exceptions;

public class MixedRestaurantCartException extends FoodiesException {
    public MixedRestaurantCartException(String cartRestaurantName, String itemRestaurantName) {
        super("Nu se poate adăuga produs de la '" + itemRestaurantName + "' - coșul conține deja produse de la '" + cartRestaurantName + "'.");
    }
}
