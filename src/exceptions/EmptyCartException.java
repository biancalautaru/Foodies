package exceptions;

public class EmptyCartException extends FoodiesException {
    public EmptyCartException() {
        super("Cart is empty.");
    }
}
