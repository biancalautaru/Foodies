package models;

import interfaces.Displayable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order implements Displayable {
    private String id;
    private int number;
    private LocalDateTime date;
    private Customer customer;
    private Restaurant restaurant;
    private Address deliveryAddress;
    private Driver driver;
    private List<MenuItem> items;
    private OrderStatus status;
    private Review review;
    private LocalDateTime statusChangeTime;

    private static final double DELIVERY_FEE = 10;

    public Order() {
        this.items = new ArrayList<>();
    }

    public Order(String id, Customer customer, Restaurant restaurant, Address deliveryAddress) {
        this.id = id;
        this.date = LocalDateTime.now();
        this.customer = customer;
        this.restaurant = restaurant;
        this.deliveryAddress = deliveryAddress;
        this.items = new ArrayList<>();
        this.status = OrderStatus.PENDING;
        this.statusChangeTime = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public String getDisplayId() { return "#" + number; }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Restaurant getRestaurant() { return restaurant; }
    public void setRestaurant(Restaurant restaurant) { this.restaurant = restaurant; }

    public Address getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(Address deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public Driver getDriver() { return driver; }
    public void setDriver(Driver driver) { this.driver = driver; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getStatusChangeTime() { return statusChangeTime.format(DateTimeFormatter.ofPattern("HH:mm")); }

    public LocalDateTime getStatusChangeDateTime() { return statusChangeTime; }
    public void setStatusChangeTime(LocalDateTime statusChangeTime) { this.statusChangeTime = statusChangeTime; }

    public Review getReview() { return review; }
    public void setReview(Review review) { this.review = review; }

    public List<MenuItem> getItems() { return Collections.unmodifiableList(items); }
    public void setItems(List<MenuItem> items) { this.items = new ArrayList<>(items); }

    public double getDeliveryFee() { return DELIVERY_FEE; }

    public void addItem(MenuItem item) {
        items.add(item);
    }

    public double getSubtotal() {
        double subtotal = 0;
        for (MenuItem item : items)
            subtotal += item.getPrice();
        return subtotal;
    }

    public double getTotal() {
        return getSubtotal() + getDeliveryFee();
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
        if (status == OrderStatus.DELIVERED || status == OrderStatus.CANCELLED)
            return false;

        status = OrderStatus.CANCELLED;
        statusChangeTime = LocalDateTime.now();

        return true;
    }

    private boolean isValidStatusTransition(OrderStatus oldStatus, OrderStatus newStatus) {
        if (oldStatus == newStatus)
            return false;

        if (oldStatus == OrderStatus.CANCELLED || newStatus == OrderStatus.CANCELLED)
            return false;

        switch (oldStatus) {
            case PENDING: return newStatus == OrderStatus.PREPARING;
            case PREPARING: return newStatus == OrderStatus.READY_FOR_PICKUP;
            case READY_FOR_PICKUP: return newStatus == OrderStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY: return newStatus == OrderStatus.DELIVERED;
            default: return false;
        }
    }

    private Order(Order source) {
        this.id = source.id;
        this.date = source.date;
        this.customer = source.customer;
        this.restaurant = source.restaurant;
        this.deliveryAddress = source.deliveryAddress;
        this.driver = source.driver;
        this.items = new ArrayList<>(source.items);
        this.status = source.status;
        this.review = source.review;
        this.statusChangeTime = source.statusChangeTime;
    }

    public Order toNewOrder(String newId, Address newDeliveryAddress) {
        Order newOrder = new Order(this);
        newOrder.id = newId;
        newOrder.number = 0;
        newOrder.date = LocalDateTime.now();
        newOrder.deliveryAddress = newDeliveryAddress;
        newOrder.status = OrderStatus.PENDING;
        newOrder.statusChangeTime = LocalDateTime.now();
        newOrder.driver = null;
        newOrder.review = null;
        return newOrder;
    }

    @Override
    public String toDisplayString() {
        return "Comanda " + getDisplayId() + " | " +
               restaurant.getName() + " | " +
               status.getLabel() + " | " +
               String.format("%.2f", getTotal()) + " lei";
    }

    @Override
    public String toString() {
        return toDisplayString();
    }
}