package exceptions;

public class OrderCancellationException extends FoodiesException {
    public OrderCancellationException(String orderId) {
        super("Nu se poate anula comanda " + orderId + " - este deja în curs de livrare.");
    }
}
