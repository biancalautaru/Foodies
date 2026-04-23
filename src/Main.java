import models.*;
import service.*;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService(10);
        RestaurantService restaurantService = new RestaurantService(10);
        OrderService orderService = new OrderService(20, userService, restaurantService);

        System.out.println("--- ADDING CUSTOMERS ---");
        Customer customer1 = new Customer("C1", "John Doe", "john@email.com", "555-1234", 5, 20);
        Customer customer2 = new Customer("C2", "Jane Smith", "jane@email.com", "555-5678", 5, 20);
        userService.addCustomer(customer1);
        userService.addCustomer(customer2);

        System.out.println("\n--- ADDING DRIVERS ---");
        Driver driver1 = new Driver("D1", "Mike Johnson", "mike@email.com", "555-9999", VehicleType.SCOOTER);
        Driver driver2 = new Driver("D2", "Sarah Williams", "sarah@email.com", "555-8888", VehicleType.CAR);
        userService.addDriver(driver1);
        userService.addDriver(driver2);

        System.out.println("\n--- ADDING RESTAURANTS ---");
        Address pizzaPlaceAddr = new Address("123 Main St", "101", "New York");
        Address burgerPlaceAddr = new Address("456 Oak Ave", "202", "New York");

        Restaurant pizzaPlace = new Restaurant("R1", "Pizza Paradise", pizzaPlaceAddr, 10);
        Restaurant burgerPlace = new Restaurant("R2", "Burger Haven", burgerPlaceAddr, 10);
        restaurantService.addRestaurant(pizzaPlace);
        restaurantService.addRestaurant(burgerPlace);

        System.out.println("\n--- ADDING MENU ITEMS ---");
        MenuItem pizza1 = new MenuItem("M1", "Margherita Pizza", "Fresh mozzarella and basil", 14.99);
        MenuItem pizza2 = new MenuItem("M2", "Pepperoni Pizza", "Classic pepperoni pizza", 16.99);
        MenuItem pizza3 = new MenuItem("M3", "Caesar Salad", "Fresh green salad", 8.99);

        MenuItem burger1 = new MenuItem("M4", "Classic Burger", "Beef burger with lettuce and tomato", 12.99);
        MenuItem burger2 = new MenuItem("M5", "Deluxe Burger", "Double beef with cheese and bacon", 15.99);
        MenuItem fries = new MenuItem("M6", "French Fries", "Crispy golden fries", 4.99);

        restaurantService.addMenuItemToRestaurant("R1", pizza1);
        restaurantService.addMenuItemToRestaurant("R1", pizza2);
        restaurantService.addMenuItemToRestaurant("R1", pizza3);

        restaurantService.addMenuItemToRestaurant("R2", burger1);
        restaurantService.addMenuItemToRestaurant("R2", burger2);
        restaurantService.addMenuItemToRestaurant("R2", fries);

        System.out.println("\n--- BROWSING RESTAURANTS ---");
        restaurantService.displayAllRestaurants();

        System.out.println("--- BROWSING MENU: Pizza Paradise ---");
        restaurantService.displayRestaurantMenu("R1");

        System.out.println("--- BROWSING MENU: Burger Haven ---");
        restaurantService.displayRestaurantMenu("R2");

        System.out.println("\n--- CUSTOMER 1 PLACING ORDER ---");
        Cart cart1 = customer1.getCart();
        cart1.addItem(pizza1);
        cart1.addItem(pizza2);
        cart1.addItem(pizza3);

        Address deliveryAddr1 = new Address("789 Elm St", "500", "New York");
        customer1.addAddress(deliveryAddr1);
        orderService.placeOrder(customer1, deliveryAddr1);

        System.out.println("\n--- CUSTOMER 2 PLACING ORDER ---");
        Cart cart2 = customer2.getCart();
        cart2.addItem(burger1);
        cart2.addItem(burger2);
        cart2.addItem(fries);

        Address deliveryAddr2 = new Address("321 Pine Rd", "100", "New York");
        customer2.addAddress(deliveryAddr2);
        orderService.placeOrder(customer2, deliveryAddr2);

        System.out.println("\n--- ASSIGNING DRIVERS AND PROCESSING ORDERS ---");
        orderService.acceptOrder("#1");
        orderService.assignDriverToOrder("#1", "D1");

        orderService.acceptOrder("#2");
        orderService.assignDriverToOrder("#2", "D2");

        System.out.println("\n--- UPDATING ORDER STATUS: Order #1 ---");
        orderService.startOrderPreparation("#1");
        orderService.markOrderReady("#1");
        orderService.pickupOrder("#1");
        orderService.deliverOrder("#1");

        System.out.println("\n--- UPDATING ORDER STATUS: Order #2 ---");
        orderService.startOrderPreparation("#2");
        orderService.markOrderReady("#2");
        orderService.pickupOrder("#2");
        orderService.deliverOrder("#2");

        System.out.println("\n--- TESTING CANCELLATION WITH FEES ---");
        Cart cart3 = customer1.getCart();
        cart3.addItem(pizza1);
        Address deliveryAddr3 = new Address("999 Test St", "777", "New York");
        customer1.addAddress(deliveryAddr3);
        orderService.placeOrder(customer1, deliveryAddr3);

        orderService.acceptOrder("#3");
        orderService.assignDriverToOrder("#3", "D1");
        System.out.println("Cancelling order #3 after driver found (should charge 25.00 lei fee):");
        orderService.cancelOrder("#3");

        System.out.println("\n--- TESTING ERROR SCENARIOS ---");

        System.out.println("Attempting to place order with empty cart:");
        orderService.placeOrder(new Customer("C3", "Empty Cart User", "test@email.com", "555-0000", 5, 20), deliveryAddr1);

        System.out.println("\nAttempting to place order with mismatched delivery city:");
        Cart testCart = new Customer("C3", "Test", "test@email.com", "555-0000", 5, 20).getCart();
        testCart.addItem(pizza1);
        Customer testCustomer = new Customer("C3", "Test", "test@email.com", "555-0000", 5, 20);
        testCustomer.addAddress(new Address("100 Far St", "999", "Los Angeles"));
        orderService.placeOrder(testCustomer, new Address("100 Far St", "999", "Los Angeles"));

        System.out.println("\nAttempting to cancel order that's already out for delivery:");
        Cart cart4 = customer2.getCart();
        cart4.addItem(burger1);
        Address deliveryAddr4 = new Address("888 Cancel St", "666", "New York");
        customer2.addAddress(deliveryAddr4);
        orderService.placeOrder(customer2, deliveryAddr4);

        orderService.acceptOrder("#4");
        orderService.assignDriverToOrder("#4", "D2");
        orderService.startOrderPreparation("#4");
        orderService.markOrderReady("#4");
        orderService.pickupOrder("#4");
        System.out.println("Trying to cancel order out for delivery:");
        orderService.cancelOrder("#4");
    }
}
