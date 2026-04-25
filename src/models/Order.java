package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String id;
    private LocalDateTime date;
    private Customer customer;
    private Restaurant restaurant;
    private Address deliveryAddress;
    private Driver driver;
    private List<MenuItem> items;
    private OrderStatus status;
    private Review review;
    private LocalDateTime statusChangeTime;
    private double cancellationFee;
    private static final double DELIVERY_FEE = 6;
    private static final double CANCELLATION_FEE = 25;

    public Order(String id, Customer customer, Restaurant restaurant, Address deliveryAddress) {
        this.id = id;
        this.date = LocalDateTime.now();
        this.customer = customer;
        this.restaurant = restaurant;
        this.deliveryAddress = deliveryAddress;
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.statusChangeTime = LocalDateTime.now();
        this.cancellationFee = 0;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public boolean updateStatus(OrderStatus newStatus) {
        if (isValidStatusTransition(status, newStatus)) {
            status = newStatus;
            statusChangeTime = LocalDateTime.now();
            return true;
        }

        return false;
    }

    public boolean cancelOrder() {
        if (status == OrderStatus.OUT_FOR_DELIVERY || status == OrderStatus.DELIVERED)
            return false;

        if (status == OrderStatus.DRIVER_ASSIGNED || status == OrderStatus.PREPARING || status == OrderStatus.READY_FOR_PICKUP)
            cancellationFee = CANCELLATION_FEE;

        status = OrderStatus.CANCELLED;
        this.statusChangeTime = LocalDateTime.now();

        return true;
    }

    private boolean isValidStatusTransition(OrderStatus oldStatus, OrderStatus newStatus) {
        if (oldStatus == newStatus)
            return false;

        if (oldStatus == OrderStatus.CANCELLED || newStatus == OrderStatus.CANCELLED)
            return false;

        switch (oldStatus) {
            case PENDING:
                return newStatus == OrderStatus.ACCEPTED;
            case ACCEPTED:
                return newStatus == OrderStatus.DRIVER_ASSIGNED || newStatus == OrderStatus.PREPARING;
            case DRIVER_ASSIGNED:
                return newStatus == OrderStatus.PREPARING;
            case PREPARING:
                return newStatus == OrderStatus.READY_FOR_PICKUP;
            case READY_FOR_PICKUP:
                return newStatus == OrderStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY:
                return newStatus == OrderStatus.DELIVERED;
            case DELIVERED:
                return false;
            default:
                return false;
        }
    }

    public double getCancellationFee() {
        return cancellationFee;
    }

    public void addItem(MenuItem item) {
        items.add(item);
    }

    public double getSubtotal() {
        double subtotal = 0;
        for (MenuItem item : items)
            subtotal += item.getPrice();
        return subtotal;
    }

    public double getDeliveryFee() {
        return DELIVERY_FEE;
    }

    public double getTotal() {
        return getSubtotal() + getDeliveryFee();
    }

    public void printOrderSummary() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

        System.out.println("\n========== ORDER SUMMARY ==========");

        System.out.println("Order ID: " + id);
        System.out.println("Date: " + date.format(formatter));
        System.out.println("Customer: " + customer.getName());
        System.out.println("Restaurant: " + restaurant.getName());
        System.out.println("Status: " + status);

        System.out.println("\n--- Items ---");
        for (MenuItem item : items)
            System.out.println(item.getName() + " - " + String.format("%.2f", item.getPrice()) + " lei");

        System.out.println("\n--- Pricing ---");
        System.out.println("Subtotal: " + String.format("%.2f", getSubtotal()) + " lei");
        System.out.println("Delivery Fee: " + String.format("%.2f", getDeliveryFee()) + " lei");
        System.out.println("TOTAL: " + String.format("%.2f", getTotal()) + " lei");

        if (driver != null)
            System.out.println("Driver: " + driver.getName());

        System.out.println("==================================\n");
    }
}
