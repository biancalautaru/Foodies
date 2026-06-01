package main;

import service.OrderService;
import service.RestaurantService;
import service.UserService;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService();
        RestaurantService restaurantService = new RestaurantService();
        OrderService orderService = new OrderService(userService);

        new ConsoleApp(userService, restaurantService, orderService).start();
    }
}