package exceptions;

public class DeliveryAddressMismatchException extends FoodiesException {
    public DeliveryAddressMismatchException(String restaurantCity, String deliveryCity) {
        super("Delivery address city '" + deliveryCity + "' does not match restaurant city '" + restaurantCity + "'.");
    }
}
