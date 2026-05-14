package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record Review(String id, Customer customer, String orderId, int rating, String comment, LocalDateTime date) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

    public Review(String id, Customer customer, String orderId, int rating, String comment) {
        this(id, customer, orderId, rating, comment, LocalDateTime.now());
    }

    @Override
    public String toString() {
        return customer.getName() + " [" + date.format(FORMATTER) + "]: " + rating + "/5 stele - " + comment;
    }
}