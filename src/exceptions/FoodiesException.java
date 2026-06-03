package exceptions;

public class FoodiesException extends RuntimeException {
    public FoodiesException(String message) {
        super(message);
    }

    public FoodiesException(String message, Throwable cause) {
        super(message, cause);
    }
}