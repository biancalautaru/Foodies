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

    public Order(String id, Customer customer, Restaurant restaurant, Address deliveryAddress, int maxItems) {
        this.id = id;
        this.date = LocalDateTime.now();
        this.customer = customer;
        this.restaurant = restaurant;
        this.deliveryAddress = deliveryAddress;
        this.items = new MenuItem[maxItems];
        this.itemsCount = 0;
        this.status = OrderStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
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

    public void setItems(MenuItem[] items) {
        this.items = items;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public void setItemsCount(int itemsCount) {
        this.itemsCount = itemsCount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void addItem(MenuItem item) {
        if (itemsCount == items.length) {
            System.out.println("Error: Maximum items reached.");
            return;
        }

        items[itemsCount++] = item;
    }
}
