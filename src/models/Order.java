package models;

import interfaces.Displayable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order implements Displayable {
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

    private static final double DELIVERY_FEE = 10;

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

    public Address getDeliveryAddress() {
        return deliveryAddress;
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

    public String getStatusChangeTime() {
        return statusChangeTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
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

    public List<MenuItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(MenuItem item) {
        items.add(item);
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

        cancellationFee = computeCancellationFee();
        status = OrderStatus.CANCELLED;
        statusChangeTime = LocalDateTime.now();
        return true;
    }

    private double computeCancellationFee() {
        switch (status) {
            case READY_FOR_PICKUP:
                return 0.30 * getSubtotal();
            case OUT_FOR_DELIVERY:
                return getSubtotal() + DELIVERY_FEE;
            default:
                return 0;
        }
    }

    private boolean isValidStatusTransition(OrderStatus oldStatus, OrderStatus newStatus) {
        if (oldStatus == newStatus)
            return false;
        if (oldStatus == OrderStatus.CANCELLED || newStatus == OrderStatus.CANCELLED)
            return false;
        switch (oldStatus) {
            case PENDING:
                return newStatus == OrderStatus.PREPARING;
            case PREPARING:
                return newStatus == OrderStatus.READY_FOR_PICKUP;
            case READY_FOR_PICKUP:
                return newStatus == OrderStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY:
                return newStatus == OrderStatus.DELIVERED;
            default:
                return false;
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
        this.cancellationFee = source.cancellationFee;
    }

    public Order toNewOrder(String newId, Address newDeliveryAddress) {
        Order newOrder = new Order(this);
        newOrder.id = newId;
        newOrder.date = LocalDateTime.now();
        newOrder.deliveryAddress = newDeliveryAddress;
        newOrder.status = OrderStatus.PENDING;
        newOrder.statusChangeTime = LocalDateTime.now();
        newOrder.driver = null;
        newOrder.review = null;
        newOrder.cancellationFee = 0;
        return newOrder;
    }

    @Override
    public String toDisplayString() {
        return "Comanda " + id + " | " + restaurant.getName() + " | " +
                status.getLabel() + " | " + String.format("%.2f", getTotal()) + " lei";
    }

    @Override
    public String toString() {
        return toDisplayString();
    }
}