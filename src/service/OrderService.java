package service;

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
        if (cart.isEmpty()) {
            System.out.println("Error: Cart is empty.");
            return;
        }

        Restaurant restaurant = cart.getRestaurant();
        String restaurantCity = restaurant.getAddress().getCity();
        String deliveryCity = address.getCity();

        if (!restaurantCity.equalsIgnoreCase(deliveryCity)) {
            System.out.println("Error: Delivery address must be in the same city as restaurant.");
            return;
        }

        String orderId = "#" + nextOrderId++;
        Order order = new Order(orderId, customer, restaurant, address);

        for (MenuItem item : cart.getItems())
            order.addItem(item);

        orders.put(orderId, order);
        System.out.println("\nOrder " + order.getId() + " placed successfully.");
        order.printOrderSummary();

        cart.clearCart();
    }

    public void acceptOrder(String orderId) {
        Order order = findOrderById(orderId);
        if (order == null) {
            System.out.println("Error: Order not found.");
            return;
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            System.out.println("Error: Only pending orders can be accepted.");
            return;
        }

        if (order.updateStatus(OrderStatus.ACCEPTED)) {
            System.out.println("Order " + order.getId() + " accepted by restaurant " + order.getRestaurant().getName() + ".");
            assignDriversToPendingOrders();
        }
        else
            System.out.println("Error: Cannot accept order " + order.getId() + ".");
    }

    public void startOrderPreparation(String orderId) {
        Order order = findOrderById(orderId);
        if (order == null) {
            System.out.println("Error: Order not found.");
            return;
        }

        if (order.getStatus() != OrderStatus.ACCEPTED && order.getStatus() != OrderStatus.DRIVER_ASSIGNED) {
            System.out.println("Error: Only accepted or driver-assigned orders can start preparation.");
            return;
        }

        if (order.updateStatus(OrderStatus.PREPARING))
            System.out.println("Order " + order.getId() + " is now being prepared.");
        else
            System.out.println("Error: Cannot start preparation for order " + order.getId() + ".");
    }

    public void markOrderReady(String orderId) {
        Order order = findOrderById(orderId);
        if (order == null) {
            System.out.println("Error: Order not found.");
            return;
        }

        if (order.getStatus() != OrderStatus.PREPARING) {
            System.out.println("Error: Only preparing orders can be marked as ready.");
            return;
        }

        if (order.updateStatus(OrderStatus.READY_FOR_PICKUP))
            System.out.println("Order " + order.getId() + " is ready for pickup.");
        else
            System.out.println("Error: Cannot mark order " + order.getId() + " as ready.");
    }

    public void pickupOrder(String orderId) {
        Order order = findOrderById(orderId);
        if (order == null) {
            System.out.println("Error: Order not found.");
            return;
        }

        if (order.getStatus() != OrderStatus.READY_FOR_PICKUP) {
            System.out.println("Error: Only ready orders can be picked up.");
            return;
        }

        if (order.getDriver() == null) {
            System.out.println("Error: Order has no assigned driver.");
            return;
        }

        if (order.updateStatus(OrderStatus.OUT_FOR_DELIVERY))
            System.out.println("Order " + order.getId() + " is now out for delivery with driver " + order.getDriver().getName() + ".");
        else
            System.out.println("Error: Cannot pick up order " + order.getId() + ".");
    }

    public void deliverOrder(String orderId) {
        Order order = findOrderById(orderId);
        if (order == null) {
            System.out.println("Error: Order not found.");
            return;
        }

        if (order.getStatus() != OrderStatus.OUT_FOR_DELIVERY) {
            System.out.println("Error: Only orders out for delivery can be delivered.");
            return;
        }

        if (order.updateStatus(OrderStatus.DELIVERED)) {
            Driver driver = order.getDriver();
            if (driver != null)
                driver.setAvailable(true);
            System.out.println("Order " + order.getId() + " delivered successfully.");

            assignDriversToPendingOrders();
        }
        else
            System.out.println("Error: Cannot deliver order " + order.getId() + ".");
    }

    public void cancelOrder(String orderId) {
        Order order = findOrderById(orderId);
        if (order == null) {
            System.out.println("Error: Order not found.");
            return;
        }

        if (!order.cancelOrder()) {
            System.out.println("Error: Cannot cancel order " + order.getId() + " because it's being delivered.");
            return;
        }

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
        if (order == null) {
            System.out.println("Error: Order not found.");
            return;
        }

        if (order.getStatus() != OrderStatus.DELIVERED) {
            System.out.println("Error: Can only review delivered orders.");
            return;
        }

        if (order.getReview() != null) {
            System.out.println("Error: Order " + orderId + " has already been reviewed.");
            return;
        }

        if (rating < 1 || rating > 5) {
            System.out.println("Error: Rating must be between 1 and 5.");
            return;
        }

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
        if (order == null) {
            System.out.println("Error: Order not found.");
            return;
        }

        order.printOrderSummary();

        if (order.getReview() == null)
            System.out.println("No review yet.");
        else {
            System.out.println("Rating: " + order.getReview().getRating() + "/5 stars");
            System.out.println("Comment: " + order.getReview().getComment());
        }
    }

    public void displayRestaurantReviews(String restaurantId) {
        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);
        if (restaurant == null) {
            System.out.println("Error: Restaurant not found.");
            return;
        }

        System.out.println("\n===== REVIEWS FOR " + restaurant.getName() + " =====");
        int count = 0;
        double totalRating = 0;

        for (Review review : reviews)
            if (review.getOrder().getRestaurant().getId().equals(restaurantId)) {
                System.out.println(review.getCustomer().getName() + ": " + review.getRating() + "/5 stars - " + review.getComment());
                totalRating += review.getRating();
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
        return orders.get(id);
    }
}
