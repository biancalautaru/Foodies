import models.*;
import service.FoodDeliveryService;

public class Main {
    public static void main(String[] args) {
        FoodDeliveryService service = new FoodDeliveryService(10, 10, 20);

        System.out.println("--- ADDING CUSTOMERS ---");
        Customer customer1 = new Customer("C1", "John Doe", "john@email.com", "555-1234", 5, 20);
        Customer customer2 = new Customer("C2", "Jane Smith", "jane@email.com", "555-5678", 5, 20);
        service.addCustomer(customer1);
        service.addCustomer(customer2);

        System.out.println("\n--- ADDING DRIVERS ---");
        Driver driver1 = new Driver("D1", "Mike Johnson", "mike@email.com", "555-9999", VehicleType.SCOOTER);
        Driver driver2 = new Driver("D2", "Sarah Williams", "sarah@email.com", "555-8888", VehicleType.CAR);
        service.addDriver(driver1);
        service.addDriver(driver2);

        System.out.println("\n--- ADDING RESTAURANTS ---");
        Address pizzaPlaceAddr = new Address("123 Main St", "101", "New York");
        Address burgerPlaceAddr = new Address("456 Oak Ave", "202", "New York");

        Restaurant pizzaPlace = new Restaurant("R1", "Pizza Paradise", pizzaPlaceAddr, 10);
        Restaurant burgerPlace = new Restaurant("R2", "Burger Haven", burgerPlaceAddr, 10);
        service.addRestaurant(pizzaPlace);
        service.addRestaurant(burgerPlace);

        System.out.println("\n--- ADDING MENU ITEMS ---");
        MenuItem pizza1 = new MenuItem("M1", "Margherita Pizza", "Fresh mozzarella and basil", 14.99);
        MenuItem pizza2 = new MenuItem("M2", "Pepperoni Pizza", "Classic pepperoni pizza", 16.99);
        MenuItem pizza3 = new MenuItem("M3", "Caesar Salad", "Fresh green salad", 8.99);

        MenuItem burger1 = new MenuItem("M4", "Classic Burger", "Beef burger with lettuce and tomato", 12.99);
        MenuItem burger2 = new MenuItem("M5", "Deluxe Burger", "Double beef with cheese and bacon", 15.99);
        MenuItem fries = new MenuItem("M6", "French Fries", "Crispy golden fries", 4.99);

        service.addMenuItemToRestaurant("R1", pizza1);
        service.addMenuItemToRestaurant("R1", pizza2);
        service.addMenuItemToRestaurant("R1", pizza3);

        service.addMenuItemToRestaurant("R2", burger1);
        service.addMenuItemToRestaurant("R2", burger2);
        service.addMenuItemToRestaurant("R2", fries);

        System.out.println("\n--- BROWSING RESTAURANTS ---");
        service.displayAllRestaurants();

        System.out.println("--- BROWSING MENU: Pizza Paradise ---");
        service.displayRestaurantMenu("R1");

        System.out.println("--- BROWSING MENU: Burger Haven ---");
        service.displayRestaurantMenu("R2");

        System.out.println("\n--- CUSTOMER 1 PLACING ORDER ---");
        Cart cart1 = customer1.getCart();
        cart1.addItem(pizza1);
        cart1.addItem(pizza2);
        cart1.addItem(pizza3);

        Address deliveryAddr1 = new Address("789 Elm St", "500", "New York");
        customer1.addAddress(deliveryAddr1);
        service.placeOrder(customer1, deliveryAddr1);

        System.out.println("\n--- CUSTOMER 2 PLACING ORDER ---");
        Cart cart2 = customer2.getCart();
        cart2.addItem(burger1);
        cart2.addItem(burger2);
        cart2.addItem(fries);

        Address deliveryAddr2 = new Address("321 Pine Rd", "100", "New York");
        customer2.addAddress(deliveryAddr2);
        service.placeOrder(customer2, deliveryAddr2);

        System.out.println("\n--- ASSIGNING DRIVERS ---");
        service.assignDriverToOrder("1", "D1");
        service.assignDriverToOrder("2", "D2");

        System.out.println("\n--- VIEWING ORDER DETAILS ---");
        service.displayOrderDetails("1");
        service.displayOrderDetails("2");

        System.out.println("\n--- UPDATING ORDER STATUS ---");
        service.updateOrderStatus("1", OrderStatus.PREPARING);
        service.updateOrderStatus("1", OrderStatus.OUT_FOR_DELIVERY);
        service.updateOrderStatus("1", OrderStatus.DELIVERED);

        service.updateOrderStatus("2", OrderStatus.PREPARING);
        service.updateOrderStatus("2", OrderStatus.OUT_FOR_DELIVERY);
        service.updateOrderStatus("2", OrderStatus.DELIVERED);

        System.out.println("\n--- SUBMITTING REVIEWS ---");
        service.submitReview("1", 5, "Excellent pizza! Fresh ingredients and fast delivery.");
        service.submitReview("2", 4, "Great burgers, but fries were a bit cold.");

        System.out.println("\n--- VIEWING RESTAURANT REVIEWS ---");
        service.displayRestaurantReviews("R1");
        service.displayRestaurantReviews("R2");

        System.out.println("--- VIEWING CUSTOMER ORDER HISTORY ---");
        service.getCustomerOrderHistory("C1");
        service.getCustomerOrderHistory("C2");

        System.out.println("\n--- TESTING ERROR SCENARIOS ---");

        System.out.println("Attempting to place order with empty cart:");
        service.placeOrder(new Customer("C3", "Empty Cart User", "test@email.com", "555-0000", 5, 20), deliveryAddr1);

        System.out.println("\nAttempting to place order with mismatched delivery city:");
        Cart testCart = new Customer("C3", "Test", "test@email.com", "555-0000", 5, 20).getCart();
        testCart.addItem(pizza1);
        Customer testCustomer = new Customer("C3", "Test", "test@email.com", "555-0000", 5, 20);
        testCustomer.addAddress(new Address("100 Far St", "999", "Los Angeles"));
        service.placeOrder(testCustomer, new Address("100 Far St", "999", "Los Angeles"));

        System.out.println("\nAttempting to submit review for pending order:");
        service.submitReview("1", 5, "Should not work - order already delivered");

        System.out.println("\nAttempting to submit review with invalid rating:");
        service.submitReview("1", 10, "Rating too high");
    }
}
