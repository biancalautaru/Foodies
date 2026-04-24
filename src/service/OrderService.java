package service;

import models.*;
import java.time.format.DateTimeFormatter;

public class OrderService {
    private Order[] orders;
    private int orderCount;
    private Review[] reviews;
    private int reviewCount;
    private UserService userService;
    private RestaurantService restaurantService;

    public OrderService(int maxOrders, UserService userService, RestaurantService restaurantService) {
        this.orders = new Order[maxOrders];
        this.orderCount = 0;
        this.reviews = new Review[maxOrders * 2];
        this.reviewCount = 0;
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

        if (orderCount == orders.length) {
            System.out.println("Error: Maximum orders reached");
            return;
        }

        Order order = new Order("#" + String.valueOf(orderCount + 1), customer, restaurant, address, cart.getItemsCount());

        MenuItem[] cartItems = cart.getItems();
        for (int i = 0; i < cart.getItemsCount(); i++)
            order.addItem(cartItems[i]);

        orders[orderCount++] = order;
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

        if (reviewCount == reviews.length) {
            System.out.println("Error: Maximum reviews reached.");
            return;
        }

        Review review = new Review(String.valueOf(reviewCount + 1), order.getCustomer(), order, rating, comment);
        order.setReview(review);
        reviews[reviewCount++] = review;

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
        for (int i = 0; i < orderCount; i++)
            if (orders[i].getCustomer().getId().equals(customerId)) {
                System.out.println("Order " + orders[i].getId() + " | " + orders[i].getDate().format(formatter) +  " | " + orders[i].getRestaurant().getName() +  " | " + orders[i].getStatus() + " | " + String.format("%.2f", orders[i].getTotal()) + " lei");
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

        for (int i = 0; i < reviewCount; i++)
            if (reviews[i].getOrder().getRestaurant().getId().equals(restaurantId)) {
                System.out.println(reviews[i].getCustomer().getName() + ": " + reviews[i].getRating() + "/5 stars - " + reviews[i].getComment());
                totalRating += reviews[i].getRating();
                count++;
            }

        if (count > 0)
            System.out.println("Average Rating: " + String.format("%.1f", totalRating / count) + "/5");
        else
            System.out.println("No reviews yet.");

        System.out.println("==================================\n");
    }

    private void assignDriversToPendingOrders() {
        for (int i = 0; i < orderCount; i++) {
            Order order = orders[i];

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
        for (int i = 0; i < orderCount; i++)
            if (orders[i].getId().equals(id))
                return orders[i];
        return null;
    }
}
