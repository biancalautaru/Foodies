package exceptions;

public class EmptyCartException extends FoodiesException {
    public EmptyCartException() {
        super("Coșul este gol.");
    }
}
