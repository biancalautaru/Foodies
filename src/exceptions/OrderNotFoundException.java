package exceptions;

public class OrderNotFoundException extends FoodiesException {
    public OrderNotFoundException(String orderId) {
        super("Comanda " + orderId + " nu a fost găsită.");
    }
}
