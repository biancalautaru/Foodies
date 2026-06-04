package exceptions;

public class RepositoryException extends FoodiesException {
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}