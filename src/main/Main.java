package main;

import repository.*;
import service.*;

public class Main {
    public static void main(String[] args) {
        CustomerRepository customerRepository = new CustomerRepository();
        DriverRepository driverRepository = new DriverRepository();
        RestaurantRepository restaurantRepository = new RestaurantRepository();
        MenuItemRepository menuItemRepository = new MenuItemRepository();
        OrderRepository orderRepository = new OrderRepository();
        ReviewRepository reviewRepository = new ReviewRepository();

        UserService userService = new UserService(customerRepository, driverRepository);
        RestaurantService restaurantService = new RestaurantService(restaurantRepository, menuItemRepository, reviewRepository);
        MenuService menuService = new MenuService(restaurantService, menuItemRepository);
        OrderService orderService = new OrderService(orderRepository, restaurantRepository, reviewRepository, driverRepository, userService);

        new ConsoleApp(userService, restaurantService, menuService, orderService).start();
    }
}