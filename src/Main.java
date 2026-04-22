import models.*;
import service.FoodDeliveryService;

public class Main {
    public static void main(String[] args) {
        FoodDeliveryService service = new FoodDeliveryService(10, 10, 10);

        Customer customer1 = new Customer("1", "Ion Popescu", "ion.popescu@email.com", "0712345678", 5, 10);
        Address address1 = new Address("Strada Victoriei", "10", "București");
        customer1.addAddress(address1);
        service.addCustomer(customer1);

        Customer customer2 = new Customer("2", "Maria Ionescu", "maria.ionescu@email.com", "0712345679", 5, 10);
        Address address2 = new Address("Calea București", "15", "Cluj-Napoca");
        customer2.addAddress(address2);
        service.addCustomer(customer2);

        Driver driver1 = new Driver("1", "Vasile Georgescu", "vasile.georgescu@email.com", "0712345680", VehicleType.CAR);
        service.addDriver(driver1);

        Driver driver2 = new Driver("2", "Ana Dumitrescu", "ana.dumitrescu@email.com", "0712345681", VehicleType.SCOOTER);
        service.addDriver(driver2);

        Restaurant restaurant1 = new Restaurant("1", "Pizza Romana", new Address("Calea Victoriei", "20", "București"), 10);
        MenuItem pizzaMargherita = new MenuItem("1", "Pizza Margherita", "Pizza cu mozzarella și roșii", 25.0);
        MenuItem pizzaPepperoni = new MenuItem("2", "Pizza Pepperoni", "Pizza cu pepperoni și mozzarella", 30.0);
        restaurant1.addMenuItem(pizzaMargherita);
        restaurant1.addMenuItem(pizzaPepperoni);
        service.addRestaurant(restaurant1);

        Restaurant restaurant2 = new Restaurant("2", "Sushi Express", new Address("Strada Lipscani", "5", "București"), 10);
        MenuItem sushiRoll = new MenuItem("3", "Sushi Roll", "Rol de sushi cu somon", 40.0);
        restaurant2.addMenuItem(sushiRoll);
        service.addRestaurant(restaurant2);

        System.out.println("\n=== Restaurants ===");
        service.displayAllRestaurants();

        System.out.println("\n=== **Pizza Romana** Menu ===");
        service.displayRestaurantMenu("1");

        System.out.println("\n=== Adding orders ===");
        customer1.getCart().addItem(pizzaMargherita);
        customer1.getCart().addItem(pizzaPepperoni);
        service.placeOrder(customer1, address1);

        customer2.getCart().addItem(sushiRoll);
        service.placeOrder(customer2, address2);

        service.assignDriverToOrder("1", "1");
        service.assignDriverToOrder("2", "2");

        service.updateOrderStatus("1", OrderStatus.OUT_FOR_DELIVERY);
        service.updateOrderStatus("1", OrderStatus.DELIVERED);
        service.updateOrderStatus("2", OrderStatus.DELIVERED);

        System.out.println("\n=== Order history for *Ion Popescu* ===");
        service.getCustomerOrderHistory("1");
    }
}
