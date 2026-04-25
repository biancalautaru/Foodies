package exceptions;

public class OrderCancellationException extends FoodiesException {
    public OrderCancellationException(String orderId) {
        super("Cannot cancel order " + orderId + " because it is currently out for delivery.");
    }
}
