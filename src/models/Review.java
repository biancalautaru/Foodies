package models;

public class Review {
    private String id;
    private Customer customer;
    private Order order;
    private int rating;
    private String comment;

    public Review(String id, Customer customer, Order order, int rating, String comment) {
        this.id = id;
        this.customer = customer;
        this.order = order;
        this.rating = rating;
        this.comment = comment;
    }

    public String getId() {
        return id;
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
        return "Review{" +
                "customer='" + customer.getName() + '\'' +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                '}';
    }
}

