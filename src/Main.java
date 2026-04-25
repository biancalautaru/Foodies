import exceptions.*;
import models.*;
import service.*;

public class Main {
    public static void main(String[] args) {
        UserService userService = new UserService();
        RestaurantService restaurantService = new RestaurantService();
        OrderService orderService = new OrderService(userService, restaurantService);

        System.out.println("--- ADDING CUSTOMERS ---");
        Customer customer1 = new Customer("C1", "John Doe", "john@email.com", "555-1234");
        Customer customer2 = new Customer("C2", "Jane Smith", "jane@email.com", "555-5678");
        userService.addCustomer(customer1);
        userService.addCustomer(customer2);

        System.out.println("\n--- ADDING DRIVERS ---");
        Driver driver1 = new Driver("D1", "Mike Johnson", "mike@email.com", "555-9999");
        Driver driver2 = new Driver("D2", "Sarah Williams", "sarah@email.com", "555-8888");
        userService.addDriver(driver1);
        userService.addDriver(driver2);

        System.out.println("\n--- ADDING RESTAURANTS ---");
        Address pizzaPlaceAddr = new Address("123 Main St", "101", "New York");
        Address burgerPlaceAddr = new Address("456 Oak Ave", "202", "New York");

        Restaurant pizzaPlace = new Restaurant("R1", "Pizza Paradise", pizzaPlaceAddr);
        Restaurant burgerPlace = new Restaurant("R2", "Burger Haven", burgerPlaceAddr);
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
        orderService.placeOrder(customer1, deliveryAddr1);

        System.out.println("\n--- CUSTOMER 2 PLACING ORDER ---");
        Cart cart2 = customer2.getCart();
        cart2.addItem(burger1);
        cart2.addItem(burger2);
        cart2.addItem(fries);

        Address deliveryAddr2 = new Address("321 Pine Rd", "100", "New York");
        orderService.placeOrder(customer2, deliveryAddr2);

        System.out.println("\n--- PROCESSING ORDERS ---");
        orderService.acceptOrder("#1");
        orderService.acceptOrder("#2");

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

        System.out.println("\n--- SUBMITTING REVIEWS ---");
        orderService.submitReview("#1", 5, "Amazing pizza, fast delivery!");
        orderService.submitReview("#2", 4, "Great burgers but fries were cold.");

        System.out.println("\n--- ORDER DETAILS WITH REVIEWS ---");
        orderService.displayOrderDetails("#1");
        orderService.displayOrderDetails("#2");

        System.out.println("\n--- RESTAURANT REVIEWS ---");
        orderService.displayRestaurantReviews("R1");
        orderService.displayRestaurantReviews("R2");

        System.out.println("\n--- CUSTOMER ORDER HISTORY ---");
        orderService.getCustomerOrderHistory("C1");
        orderService.getCustomerOrderHistory("C2");

        System.out.println("\n--- TESTING CANCELLATION WITH FEE ---");
        Cart cart3 = customer1.getCart();
        cart3.addItem(pizza1);
        orderService.placeOrder(customer1, new Address("999 Test St", "777", "New York"));
        orderService.acceptOrder("#3");
        System.out.println("Cancelling order #3 after driver assigned (should charge 25.00 lei fee):");
        orderService.cancelOrder("#3");

        System.out.println("\n--- TESTING ERROR SCENARIOS ---");

        System.out.println("Attempting to submit duplicate review for order #1:");
        try {
            orderService.submitReview("#1", 3, "Changing my mind...");
        } catch (InvalidReviewException e) {
            System.out.println("Caught InvalidReviewException: " + e.getMessage());
        }

        System.out.println("\nAttempting to review an order not yet delivered:");
        Cart cart4 = customer1.getCart();
        cart4.addItem(pizza2);
        orderService.placeOrder(customer1, new Address("555 Review St", "333", "New York"));
        orderService.acceptOrder("#4");
        try {
            orderService.submitReview("#4", 5, "Too early to review!");
        } catch (InvalidReviewException e) {
            System.out.println("Caught InvalidReviewException: " + e.getMessage());
        }

        System.out.println("\nAttempting to place order with empty cart:");
        try {
            orderService.placeOrder(new Customer("C3", "Empty Cart User", "test@email.com", "555-0000"), deliveryAddr1);
        } catch (EmptyCartException e) {
            System.out.println("Caught EmptyCartException: " + e.getMessage());
        }

        System.out.println("\nAttempting to place order with mismatched delivery city:");
        Customer testCustomer = new Customer("C4", "Test", "test@email.com", "555-0000");
        testCustomer.getCart().addItem(pizza1);
        try {
            orderService.placeOrder(testCustomer, new Address("100 Far St", "999", "Los Angeles"));
        } catch (DeliveryAddressMismatchException e) {
            System.out.println("Caught DeliveryAddressMismatchException: " + e.getMessage());
        }

        System.out.println("\nAttempting to add items from different restaurants to the same cart:");
        Customer mixedCartCustomer = new Customer("C5", "Mixed Cart User", "mixed@email.com", "555-1111");
        mixedCartCustomer.getCart().addItem(pizza1);
        try {
            mixedCartCustomer.getCart().addItem(burger1);
        } catch (MixedRestaurantCartException e) {
            System.out.println("Caught MixedRestaurantCartException: " + e.getMessage());
        }

        System.out.println("\nAttempting to cancel order that's already out for delivery:");
        Cart cart5 = customer2.getCart();
        cart5.addItem(burger1);
        orderService.placeOrder(customer2, new Address("888 Cancel St", "666", "New York"));
        orderService.acceptOrder("#5");
        orderService.startOrderPreparation("#5");
        orderService.markOrderReady("#5");
        orderService.pickupOrder("#5");
        try {
            orderService.cancelOrder("#5");
        } catch (OrderCancellationException e) {
            System.out.println("Caught OrderCancellationException: " + e.getMessage());
        }
    }
}
