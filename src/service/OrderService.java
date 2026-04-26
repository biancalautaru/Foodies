package service;

import exceptions.*;
import models.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderService {
    private Map<String, Order> orders;
    private List<Review> reviews;
    private int nextOrderId;
    private UserService userService;
    private RestaurantService restaurantService;

    public OrderService(UserService userService, RestaurantService restaurantService) {
        this.orders = new LinkedHashMap<>();
        this.reviews = new ArrayList<>();
        this.nextOrderId = 1;
        this.userService = userService;
        this.restaurantService = restaurantService;
    }

    public void placeOrder(Customer customer, Address address) {
        Cart cart = customer.getCart();
        if (cart.isEmpty())
            throw new EmptyCartException();

        Restaurant restaurant = cart.getRestaurant();
        String restaurantCity = restaurant.getAddress().city();
        String deliveryCity = address.city();

        if (!restaurantCity.equalsIgnoreCase(deliveryCity))
            throw new DeliveryAddressMismatchException(restaurantCity, deliveryCity);

        Cart snapshot = cart.clone();
        cart.clearCart();

        String orderId = "#" + nextOrderId++;
        Order order = new Order(orderId, customer, restaurant, address);

        for (MenuItem item : snapshot.getItems())
            order.addItem(item);

        orders.put(orderId, order);
        System.out.println("\nOrder " + order.getId() + " placed successfully.");
        order.printOrderSummary();
    }

    public void reorder(Customer customer, String originalOrderId, Address deliveryAddress) {
        Order original = findOrderById(originalOrderId);

        if (!original.getCustomer().getId().equals(customer.getId()))
            throw new OrderNotFoundException("Order " + originalOrderId + " does not belong to customer " + customer.getName() + ".");

        if (original.getStatus() != OrderStatus.DELIVERED)
            throw new InvalidOrderStateException("Can only reorder from a delivered order. Order " + originalOrderId + " is " + original.getStatus() + ".");

        String restaurantCity = original.getRestaurant().getAddress().city();
        String deliveryCity = deliveryAddress.city();
        if (!restaurantCity.equalsIgnoreCase(deliveryCity))
            throw new DeliveryAddressMismatchException(restaurantCity, deliveryCity);

        String newOrderId = "#" + nextOrderId++;
        Order newOrder = original.toNewOrder(newOrderId, deliveryAddress);
        orders.put(newOrderId, newOrder);

        System.out.println("\nReorder " + newOrder.getId() + " created from order " + originalOrderId + ".");
        newOrder.printOrderSummary();
    }

    public void acceptOrder(String orderId) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.PENDING)
            throw new InvalidOrderStateException("Only pending orders can be accepted. Order " + orderId + " is " + order.getStatus() + ".");

        if (!order.updateStatus(OrderStatus.ACCEPTED))
            throw new InvalidOrderStateException("Cannot accept order " + orderId + ".");

        System.out.println("Order " + order.getId() + " accepted by restaurant " + order.getRestaurant().getName() + ".");
        assignDriversToPendingOrders();
    }

    public void startOrderPreparation(String orderId) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.ACCEPTED && order.getStatus() != OrderStatus.DRIVER_ASSIGNED)
            throw new InvalidOrderStateException("Only accepted or driver-assigned orders can start preparation. Order " + orderId + " is " + order.getStatus() + ".");

        if (!order.updateStatus(OrderStatus.PREPARING))
            throw new InvalidOrderStateException("Cannot start preparation for order " + orderId + ".");

        System.out.println("Order " + order.getId() + " is now being prepared.");
    }

    public void markOrderReady(String orderId) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.PREPARING)
            throw new InvalidOrderStateException("Only preparing orders can be marked as ready. Order " + orderId + " is " + order.getStatus() + ".");

        if (!order.updateStatus(OrderStatus.READY_FOR_PICKUP))
            throw new InvalidOrderStateException("Cannot mark order " + orderId + " as ready.");

        System.out.println("Order " + order.getId() + " is ready for pickup.");
    }

    public void pickupOrder(String orderId) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.READY_FOR_PICKUP)
            throw new InvalidOrderStateException("Only ready orders can be picked up. Order " + orderId + " is " + order.getStatus() + ".");

        if (order.getDriver() == null)
            throw new InvalidOrderStateException("Order " + orderId + " has no assigned driver.");

        if (!order.updateStatus(OrderStatus.OUT_FOR_DELIVERY))
            throw new InvalidOrderStateException("Cannot pick up order " + orderId + ".");

        System.out.println("Order " + order.getId() + " is now out for delivery with driver " + order.getDriver().getName() + ".");
    }

    public void deliverOrder(String orderId) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.OUT_FOR_DELIVERY)
            throw new InvalidOrderStateException("Only orders out for delivery can be delivered. Order " + orderId + " is " + order.getStatus() + ".");

        if (!order.updateStatus(OrderStatus.DELIVERED))
            throw new InvalidOrderStateException("Cannot deliver order " + orderId + ".");

        Driver driver = order.getDriver();
        if (driver != null)
            driver.setAvailable(true);

        System.out.println("Order " + order.getId() + " delivered successfully.");
        assignDriversToPendingOrders();
    }

    public void cancelOrder(String orderId) {
        Order order = findOrderById(orderId);

        if (!order.cancelOrder())
            throw new OrderCancellationException(orderId);

        Driver driver = order.getDriver();
        if (driver != null)
            driver.setAvailable(true);

        String message = "Order " + order.getId() + " cancelled.";
        if (order.getCancellationFee() > 0)
            message += " Cancellation fee: " + String.format("%.2f", order.getCancellationFee()) + " lei.";
        System.out.println(message);
    }

    public void submitReview(String orderId, int rating, String comment) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.DELIVERED)
            throw new InvalidReviewException("Can only review delivered orders. Order " + orderId + " is " + order.getStatus() + ".");

        if (order.getReview() != null)
            throw new InvalidReviewException("Order " + orderId + " has already been reviewed.");

        if (rating < 1 || rating > 5)
            throw new InvalidReviewException("Rating must be between 1 and 5, got: " + rating + ".");

        Review review = new Review(String.valueOf(reviews.size() + 1), order.getCustomer(), order, rating, comment);
        order.setReview(review);
        reviews.add(review);

        Restaurant restaurant = order.getRestaurant();
        int newCount = restaurant.getReviewCount() + 1;
        double newStars = (restaurant.getStars() * restaurant.getReviewCount() + rating) / newCount;
        restaurant.setReviewCount(newCount);
        restaurant.setStars(newStars);

        System.out.println("Review submitted for order " + orderId + " (" + restaurant.getName() + "): " + rating + "/5 stars - " + comment);
    }

    public void getCustomerOrderHistory(String customerId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

        System.out.println("\n===== ORDER HISTORY =====");
        boolean foundOrders = false;
        for (Order order : orders.values())
            if (order.getCustomer().getId().equals(customerId)) {
                System.out.println("Order " + order.getId() + " | " + order.getDate().format(formatter) + " | " + order.getRestaurant().getName() + " | " + order.getStatus() + " | " + String.format("%.2f", order.getTotal()) + " lei");
                foundOrders = true;
            }

        if (!foundOrders)
            System.out.println("No orders found.");
        System.out.println("=========================\n");
    }

    public void displayOrderDetails(String orderId) {
        Order order = findOrderById(orderId);
        order.printOrderSummary();

        if (order.getReview() == null)
            System.out.println("No review yet.");
        else {
            System.out.println("Rating: " + order.getReview().rating() + "/5 stars");
            System.out.println("Comment: " + order.getReview().comment());
        }
    }

    public void displayRestaurantReviews(String restaurantId) {
        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);

        System.out.println("\n===== REVIEWS FOR " + restaurant.getName() + " =====");
        int count = 0;
        double totalRating = 0;

        for (Review review : reviews)
            if (review.order().getRestaurant().getId().equals(restaurantId)) {
                System.out.println(review.customer().getName() + ": " + review.rating() + "/5 stars - " + review.comment());
                totalRating += review.rating();
                count++;
            }

        if (count > 0)
            System.out.println("Average Rating: " + String.format("%.1f", totalRating / count) + "/5");
        else
            System.out.println("No reviews yet.");

        System.out.println("==================================\n");
    }

    private void assignDriversToPendingOrders() {
        for (Order order : orders.values()) {
            if (order.getStatus() == OrderStatus.ACCEPTED && order.getDriver() == null) {
                Driver driver = userService.findAvailableDriver();
                if (driver == null)
                    break;

                order.setDriver(driver);
                driver.setAvailable(false);
                order.updateStatus(OrderStatus.DRIVER_ASSIGNED);

                System.out.println("Driver " + driver.getName() + " auto-assigned to order " + order.getId());
            }
        }
    }

    private Order findOrderById(String id) {
        Order order = orders.get(id);
        if (order == null)
            throw new OrderNotFoundException(id);
        return order;
    }
}
