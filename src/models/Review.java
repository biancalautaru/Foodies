package models;

import interfaces.Displayable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Review implements Displayable {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

    private String id;
    private Customer customer;
    private String orderId;
    private int rating;
    private String comment;
    private LocalDateTime date;

    public Review() {}

    public Review(String id, Customer customer, String orderId, int rating, String comment) {
        this(id, customer, orderId, rating, comment, LocalDateTime.now());
    }

    public Review(String id, Customer customer, String orderId, int rating, String comment, LocalDateTime date) {
        this.id = id;
        this.customer = customer;
        this.orderId = orderId;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    @Override
    public String toDisplayString() {
        return customer.getName() + " [" + date.format(FORMATTER) + "]: " + rating + "/5 stele - " + comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Review review))
            return false;
        return Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return toDisplayString();
    }
}