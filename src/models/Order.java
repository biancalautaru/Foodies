package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order {
    private String id;
    private LocalDateTime date;
    private Customer customer;
    private Restaurant restaurant;
    private Address deliveryAddress;
    private Driver driver;
    private MenuItem[] items;
    private int itemsCount;
    private OrderStatus status;
    private Review review;
    private static final double DELIVERY_FEE = 5.99;

    public Order(String id, Customer customer, Restaurant restaurant, Address deliveryAddress, int maxItems) {
        this.id = id;
        this.date = LocalDateTime.now();
        this.customer = customer;
        this.restaurant = restaurant;
        this.deliveryAddress = deliveryAddress;
        this.items = new MenuItem[maxItems];
        this.itemsCount = 0;
        this.status = OrderStatus.PENDING;
        this.review = null;
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

    public MenuItem[] getItems() {
        return items;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public void addItem(MenuItem item) {
        if (itemsCount == items.length) {
            System.out.println("Error: Maximum items reached.");
            return;
        }

        items[itemsCount++] = item;
    }

    public double getSubtotal() {
        double subtotal = 0;
        for (int i = 0; i < itemsCount; i++)
            subtotal += items[i].getPrice();
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
        for (int i = 0; i < itemsCount; i++)
            System.out.println(items[i].getName() + " - $" + String.format("%.2f", items[i].getPrice()));
        
        System.out.println("\n--- Pricing ---");
        System.out.println("Subtotal: $" + String.format("%.2f", getSubtotal()));
        System.out.println("Delivery Fee: $" + String.format("%.2f", getDeliveryFee()));
        System.out.println("TOTAL: $" + String.format("%.2f", getTotal()));

        if (driver != null)
            System.out.println("Driver: " + driver.getName());

        System.out.println("==================================\n");
    }
}
