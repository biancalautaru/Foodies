package service;

import models.*;
import java.time.format.DateTimeFormatter;

public class FoodDeliveryService {
    private Customer[] customers;
    private int customerCount;
    private Driver[] drivers;
    private int driverCount;
    private Restaurant[] restaurants;
    private int restaurantCount;
    private Order[] orders;
    private int orderCount;
    private Review[] reviews;
    private int reviewCount;

    public FoodDeliveryService(int maxUsers, int maxRestaurants, int maxOrders) {
        this.customers = new Customer[maxUsers];
        this.customerCount = 0;

        this.drivers = new Driver[maxUsers];
        this.driverCount = 0;

        this.restaurants = new Restaurant[maxUsers];
        this.restaurantCount = 0;

        this.orders = new Order[maxOrders];
        this.orderCount = 0;

        this.reviews = new Review[maxOrders * 2];
        this.reviewCount = 0;
    }

    public void addCustomer(Customer customer) {
        if (customerCount == customers.length) {
            System.out.println("Error: Maximum customers reached.");
            return;
        }

        customers[customerCount++] = customer;
        System.out.println("Customer added: " + customer.getName());
    }

    public void addDriver(Driver driver) {
        if (driverCount == drivers.length) {
            System.out.println("Error: Maximum drivers reached.");
            return;
        }

        drivers[driverCount++] = driver;
        System.out.println("Driver added: " + driver.getName());
    }

    public void addRestaurant(Restaurant restaurant) {
        if (restaurantCount == restaurants.length) {
            System.out.println("Error: Maximum restaurants reached.");
            return;
        }

        restaurants[restaurantCount++] = restaurant;
        System.out.println("Restaurant added: " + restaurant.getName());
    }

    public void addMenuItemToRestaurant(String restaurantId, MenuItem item) {
        Restaurant restaurant = findRestaurantById(restaurantId);
        if (restaurant == null) {
            System.out.println("Error: Restaurant not found.");
            return;
        }

        restaurant.addMenuItem(item);
        System.out.println("Item '" + item.getName() + "' added to restaurant " + restaurant.getName() + ".");
    }

    public void displayAllRestaurants() {
        System.out.println("\n===== ALL RESTAURANTS =====");
        for (int i = 0; i < restaurantCount; i++)
            System.out.println(restaurants[i]);
        System.out.println("============================\n");
    }

    public void displayRestaurantMenu(String restaurantId) {
        Restaurant restaurant = findRestaurantById(restaurantId);
        if (restaurant == null) {
            System.out.println("Error: Restaurant not found.");
            return;
        }

        System.out.println("\n===== MENU: " + restaurant.getName() + " =====");
        MenuItem[] menu = restaurant.getMenu();
        for (int i = 0; i < restaurant.getMenuCount(); i++)
            System.out.println("  " + menu[i]);
        System.out.println("==========================\n");
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

        Order order = new Order(String.valueOf(orderCount + 1), customer, restaurant, address, cart.getItemsCount());

        MenuItem[] cartItems = cart.getItems();
        for (int i = 0; i < cart.getItemsCount(); i++) {
            if (cartItems[i] != null) {
                order.addItem(cartItems[i]);
            }
        }

        orders[orderCount++] = order;
        System.out.println("\nOrder #" + order.getId() + " placed successfully.");
        order.printOrderSummary();

        cart.clearCart();
    }

    public void assignDriverToOrder(String orderId, String driverId) {
        Order order = findOrderById(orderId);
        if (order == null) {
            System.out.println("Error: Order not found.");
            return;
        }

        Driver driver = findDriverById(driverId);
        if (driver == null) {
            System.out.println("Error: Driver not found.");
            return;
        }

        if (!driver.isAvailable()) {
            System.out.println("Error: Driver is not available.");
            return;
        }

        order.setDriver(driver);
        order.setStatus(OrderStatus.ACCEPTED);
        driver.setAvailable(false);
        System.out.println("Driver " + driver.getName() + " assigned to order #" + order.getId() + ".");
    }

    public void updateOrderStatus(String orderId, OrderStatus newStatus) {
        Order order = findOrderById(orderId);
        if (order == null) {
            System.out.println("Error: Order not found.");
            return;
        }

        order.setStatus(newStatus);
        System.out.println("Order #" + order.getId() + " status updated to " + newStatus + ".");

        if (newStatus == OrderStatus.DELIVERED) {
            Driver driver = order.getDriver();
            if (driver != null)
                driver.setAvailable(true);

            assignDriversToPendingOrders();
        }
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
        System.out.println("Review submitted for order #" + orderId + ": " + rating + " stars - " + comment);
    }

    public void displayRestaurantReviews(String restaurantId) {
        Restaurant restaurant = findRestaurantById(restaurantId);
        if (restaurant == null) {
            System.out.println("Error: Restaurant not found.");
            return;
        }

        System.out.println("\n===== REVIEWS FOR " + restaurant.getName() + " =====");
        int count = 0;
        double totalRating = 0;

        for (int i = 0; i < reviewCount; i++)
            if (reviews[i].getOrder().getRestaurant().getId().equals(restaurantId)) {
                System.out.println("  " + reviews[i].getRating() + "/5 stars - " + reviews[i].getComment() + " (by " + reviews[i].getCustomer().getName() + ")");
                totalRating += reviews[i].getRating();
                count++;
            }

        if (count > 0)
            System.out.println("  Average Rating: " + String.format("%.1f", totalRating / count) + "/5");
        else
            System.out.println("  No reviews yet.");
        
        System.out.println("==================================\n");
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
            System.out.println("Review: " + order.getReview().getRating() + "/5 stars");
            System.out.println("Comment: " + order.getReview().getComment());
        }
    }

    public void getCustomerOrderHistory(String customerId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

        System.out.println("\n===== ORDER HISTORY =====");
        boolean foundOrders = false;
        for (int i = 0; i < orderCount; i++)
            if (orders[i].getCustomer().getId().equals(customerId)) {
                System.out.println("  Order #" + orders[i].getId() + " | " + orders[i].getDate().format(formatter) +
                        " | Restaurant: " + orders[i].getRestaurant().getName() +
                        " | Status: " + orders[i].getStatus() +
                        " | Total: $" + String.format("%.2f", orders[i].getTotal()));
                foundOrders = true;
            }

        if (!foundOrders)
            System.out.println("  No orders found.");
        System.out.println("=========================\n");
    }

    private void assignDriversToPendingOrders() {
        for (int i = 0; i < orderCount; i++) {
            Order order = orders[i];

            if (order.getStatus() == OrderStatus.PENDING && order.getDriver() == null) {
                Driver driver = findAvailableDriver();
                if (driver == null)
                    break;

                order.setDriver(driver);
                order.setStatus(OrderStatus.ACCEPTED);
                driver.setAvailable(false);

                System.out.println("Driver " + driver.getName() + " assigned to order " + order.getId());
            }
        }
    }

    private Restaurant findRestaurantById(String id) {
        for (int i = 0; i < restaurantCount; i++)
            if (restaurants[i].getId().equals(id))
                return restaurants[i];
        return null;
    }

    private Driver findDriverById(String id) {
        for (int i = 0; i < driverCount; i++)
            if (drivers[i].getId().equals(id))
                return drivers[i];
        return null;
    }

    private Order findOrderById(String id) {
        for (int i = 0; i < orderCount; i++)
            if (orders[i].getId().equals(id))
                return orders[i];
        return null;
    }

    private Driver findAvailableDriver() {
        for (int i = 0; i < driverCount; i++)
            if (drivers[i].isAvailable())
                return drivers[i];

        return null;
    }
}
