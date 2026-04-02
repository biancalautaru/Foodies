package service;

import models.*;

public class FoodDeliveryService {
    private Customer[] customers;
    private int customerCount;

    private Driver[] drivers;
    private int driverCount;

    private Restaurant[] restaurants;
    private int restaurantCount;

    private Order[] orders;
    private int orderCount;

    public FoodDeliveryService(int maxUsers, int maxRestaurants, int maxOrders) {
        this.customers = new Customer[maxUsers];
        this.customerCount = 0;

        this.drivers = new Driver[maxUsers];
        this.driverCount = 0;

        this.restaurants = new Restaurant[maxUsers];
        this.restaurantCount = 0;

        this.orders = new Order[maxOrders];
        this.orderCount = 0;
    }

    public void addCustomer(Customer customer) {
        if (customerCount == customers.length) {
            System.out.println("Maximum customers reached");
            return;
        }

        customers[customerCount++] = customer;
        System.out.println("Customer added: " + customer.getName());
    }

    public void addDriver(Driver driver) {
        if (driverCount == drivers.length) {
            System.out.println("Maximum drivers reached");
            return;
        }

        drivers[driverCount++] = driver;
        System.out.println("Driver added: " + driver.getName());
    }

    public void addRestaurant(Restaurant restaurant) {
        if (restaurantCount == restaurants.length) {
            System.out.println("Maximum restaurants reached");
            return;
        }

        restaurants[restaurantCount++] = restaurant;
        System.out.println("Restaurant added: " + restaurant.getName());
    }

    public void addMenuItemToRestaurant(String restaurantId, MenuItem item) {
        Restaurant restaurant = findRestaurantById(restaurantId);
        if (restaurant == null) {
            System.out.println("Restaurant not found");
            return;
        }

        restaurant.addMenuItem(item);
        System.out.println("Item '" + item.getName() + "' added to restaurant " + restaurant.getName());
    }

    public void displayAllRestaurants() {
        for (int i = 0; i < restaurantCount; i++)
            System.out.println(restaurants[i]);
    }

    public void displayRestaurantMenu(String restaurantId) {
        Restaurant restaurant = findRestaurantById(restaurantId);
        if (restaurant == null) {
            System.out.println("Restaurant not found");
            return;
        }

        MenuItem[] menu = restaurant.getMenu();
        for (int i = 0; i < restaurant.getMenuCount(); i++)
            System.out.println(menu[i]);
    }

    public void placeOrder(Order order) {
        if  (orderCount == orders.length) {
            System.out.println("Maximum orders reached");
            return;
        }

        orders[orderCount++] = order;
        System.out.println("Order placed successfully. ID: " + order.getId());
    }

    public void assignDriverToOrder(String orderId, String driverId) {
        Order order = findOrderById(orderId);
        if (order == null) {
            System.out.println("Order not found");
            return;
        }

        Driver driver = findDriverById(driverId);
        if (driver == null) {
            System.out.println("Driver not found");
            return;
        }

        if (!driver.isAvailable()) {
            System.out.println("Driver is not available");
            return;
        }

        order.setDriver(driver);
        order.setStatus(OrderStatus.ACCEPTED);
        driver.setAvailable(false);
        System.out.println("Driver " + driver.getName() + " assigned to order " + order.getId());
    }

    public void updateOrderStatus(String orderId, OrderStatus newStatus) {
        Order order = findOrderById(orderId);
        if (order == null) {
            System.out.println("Order not found");
            return;
        }

        order.setStatus(newStatus);
        System.out.println("Order " + order.getId() + " status updated to " + newStatus);
    }

    public void getCustomerOrderHistory(String customerId) {
        boolean foundOrders = false;
        for (int i = 0; i < orderCount; i++)
            if (orders[i].getCustomer().getId().equals(customerId)) {
                System.out.println("Restaurant " + orders[i].getRestaurant().getName() + ", Date: " + orders[i].getDate());
                foundOrders = true;
            }

        if (!foundOrders)
            System.out.println("No orders found");
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
}
