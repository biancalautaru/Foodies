package exceptions;

public class OrderNotFoundException extends FoodiesException {
    public OrderNotFoundException(String orderId) {
        super("Order not found: " + orderId);
    }
}
