package exceptions;

public class DeliveryAddressMismatchException extends FoodiesException {
    public DeliveryAddressMismatchException(String restaurantCity, String deliveryCity) {
        super("Orașul adresei de livrare '" + deliveryCity + "' nu corespunde cu orașul restaurantului '" + restaurantCity + "'.");
    }
}
