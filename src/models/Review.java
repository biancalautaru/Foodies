package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Review {
    private String id;
    private Customer customer;
    private Order order;
    private int rating;
    private String comment;
    private LocalDateTime date;

    public Review(String id, Customer customer, Order order, int rating, String comment) {
        this.id = id;
        this.customer = customer;
        this.order = order;
        this.rating = rating;
        this.comment = comment;
        this.date = LocalDateTime.now();
    }

    public Customer getCustomer() {
        return customer;
    }

    public Order getOrder() {
        return order;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        return customer.getName() + " [" + date.format(formatter) + "]: " + rating + "/5 stars - " + comment;
    }
}