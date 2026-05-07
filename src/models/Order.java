package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Order implements Cloneable {
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
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

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

    public double getCancellationFee() {
        return cancellationFee;
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

        switch (status) {
            case PENDING:
            case PREPARING:
                cancellationFee = 0;
                break;
            case READY_FOR_PICKUP:
                cancellationFee = 0.30 * getSubtotal();
                break;
            case OUT_FOR_DELIVERY:
                cancellationFee = getSubtotal() + DELIVERY_FEE;
                break;
            default:
                break;
        }

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

    private String statusToRomanian() {
        switch (status) {
            case PENDING:          return "În așteptare";
            case PREPARING:        return "În preparare";
            case READY_FOR_PICKUP: return "Gata de ridicare";
            case OUT_FOR_DELIVERY: return "În livrare";
            case DELIVERED:        return "Livrată";
            case CANCELLED:        return "Anulată";
            default:               return status.toString();
        }
    }

    public String toSummaryString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========== REZUMAT COMANDĂ ==========\n");
        sb.append("ID comandă: ").append(id).append("\n");
        sb.append("Data: ").append(date.format(FORMATTER)).append("\n");
        sb.append("Client: ").append(customer.getName()).append("\n");
        sb.append("Restaurant: ").append(restaurant.getName()).append("\n");
        sb.append("Livrare la: ").append(deliveryAddress).append("\n");
        sb.append("Stare: ").append(statusToRomanian()).append("\n");

        sb.append("\n--- Produse ---\n");
        for (MenuItem item : items)
            sb.append(item.getName()).append(" - ").append(String.format("%.2f", item.getPrice())).append(" lei\n");

        sb.append("\n--- Costuri ---\n");
        sb.append("Valoare produse: ").append(String.format("%.2f", getSubtotal())).append(" lei\n");
        sb.append("Taxă livrare: ").append(String.format("%.2f", getDeliveryFee())).append(" lei\n");
        sb.append("TOTAL: ").append(String.format("%.2f", getTotal())).append(" lei\n");

        if (status == OrderStatus.CANCELLED)
            sb.append("Taxă anulare: ").append(String.format("%.2f", cancellationFee)).append(" lei\n");

        if (driver != null)
            sb.append("Curier: ").append(driver.getName()).append("\n");

        sb.append("==================================\n");
        return sb.toString();
    }

    @Override
    protected Order clone() {
        try {
            Order cloned = (Order) super.clone();
            cloned.items = new ArrayList<>(this.items);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Clasa Order este Cloneable", e);
        }
    }

    public Order toNewOrder(String newId, Address newDeliveryAddress) {
        Order newOrder = clone();
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
}