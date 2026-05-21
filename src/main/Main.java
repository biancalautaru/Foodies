package main;

import service.*;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService();
        RestaurantService restaurantService = new RestaurantService();
        MenuService menuService = new MenuService(restaurantService);
        OrderService orderService = new OrderService(userService);

        new ConsoleApp(userService, restaurantService, menuService, orderService).start();
    }
}