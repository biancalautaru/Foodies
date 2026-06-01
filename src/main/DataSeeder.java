package main;

import models.*;
import service.OrderService;
import service.RestaurantService;
import service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DataSeeder {

    private DataSeeder() {}

    private record Customers(Customer customer1, Customer customer2, Customer customer3) {}

    private record Restaurants(Restaurant restaurant1, Restaurant restaurant2, Restaurant restaurant3,
                               Restaurant restaurant4, Restaurant restaurant5) {}

    private record OrderItems(MenuItem item1, MenuItem item2, MenuItem item3,
                              MenuItem item4, MenuItem item5, MenuItem item6,
                              MenuItem item7, MenuItem item8, MenuItem item9) {}

    public static void seed(UserService userService, RestaurantService restaurantService,
                            OrderService orderService) {
        seedTestAccount(userService);
        seedDrivers(userService);
        Customers customers = seedCustomers(userService);
        Restaurants restaurants = seedRestaurants(restaurantService);
        OrderItems orderItems = seedMenus(restaurantService, restaurants);
        seedOrders(orderService, customers, orderItems);
    }

    private static void seedTestAccount(UserService userService) {
        if (userService.findCustomerByEmail("email@test.com") == null)
            userService.addCustomer(new Customer("TEST", "Test", "email@test.com", "pass"));
    }

    private static void seedDrivers(UserService userService) {
        addDriverIfMissing(userService, new Driver("D1", "Andrei Dumitrescu", "andrei.d@gmail.com"));
        addDriverIfMissing(userService, new Driver("D2", "Elena Vasile", "elena.v@yahoo.com"));
        addDriverIfMissing(userService, new Driver("D3", "Mihai Popa", "mihai.p@gmail.com"));
    }

    private static void addDriverIfMissing(UserService userService, Driver driver) {
        if (userService.findDriverById(driver.getId()) == null)
            userService.addDriver(driver);
    }

    private static Customers seedCustomers(UserService userService) {
        Customer customer1 = new Customer("C1", "Maria Ionescu", "maria.ionescu@gmail.com", "parola1");
        Customer customer2 = new Customer("C2", "Radu Munteanu", "radu.m@yahoo.com", "parola2");
        Customer customer3 = new Customer("C3", "Ioana Constantin", "ioana.c@gmail.com", "parola3");
        addCustomerIfMissing(userService, customer1);
        addCustomerIfMissing(userService, customer2);
        addCustomerIfMissing(userService, customer3);
        return new Customers(customer1, customer2, customer3);
    }

    private static void addCustomerIfMissing(UserService userService, Customer customer) {
        if (userService.findCustomerByEmail(customer.getEmail()) == null)
            userService.addCustomer(customer);
    }

    private static Restaurants seedRestaurants(RestaurantService restaurantService) {
        Restaurant restaurant1 = new Restaurant("R1", "Pizza La Mama", new Address("Str. Amzei", "12", "București"));
        Restaurant restaurant2 = new Restaurant("R2", "Grill & Burger", new Address("Bd. Unirii", "45", "București"));
        Restaurant restaurant3 = new Restaurant("R3", "Sakura Sushi", new Address("Str. Calea Victoriei", "88", "București"));
        Restaurant restaurant4 = new Restaurant("R4", "Verde & Sănătos", new Address("Str. Episcopiei", "5", "București"));
        Restaurant restaurant5 = new Restaurant("R5", "El Rancho Tacos", new Address("Bd. Magheru", "31", "București"));
        Set<String> existing = new HashSet<>();
        for (Restaurant existingRestaurant : restaurantService.getRestaurantsSortedByName())
            existing.add(existingRestaurant.getId());
        addRestaurantIfMissing(restaurantService, existing, restaurant1);
        addRestaurantIfMissing(restaurantService, existing, restaurant2);
        addRestaurantIfMissing(restaurantService, existing, restaurant3);
        addRestaurantIfMissing(restaurantService, existing, restaurant4);
        addRestaurantIfMissing(restaurantService, existing, restaurant5);
        return new Restaurants(restaurant1, restaurant2, restaurant3, restaurant4, restaurant5);
    }

    private static void addRestaurantIfMissing(RestaurantService restaurantService, Set<String> existing, Restaurant restaurant) {
        if (!existing.contains(restaurant.getId()))
            restaurantService.addRestaurant(restaurant);
    }

    private static OrderItems seedMenus(RestaurantService restaurantService, Restaurants rs) {
        addItems(restaurantService, rs.restaurant1(),
            new MenuItem("M1", "Pizza Margherita", "Blat pizza, sos de roșii, mozzarella, busuioc", 36.99),
            new MenuItem("M2", "Pizza Diavola",  "Blat pizza, sos de roșii, mozzarella, salam picant", 42.99),
            new MenuItem("M3", "Salată Caesar",  "Salată verde, dressing Caesar", 28.99),
            new MenuItem("M7", "Calzone Prosciutto", "Blat pizza împăturit, sos de roșii, șuncă, mozzarella, ciuperci", 44.99),
            new MenuItem("M8", "Tiramisu", "Desert italian clasic cu mascarpone", 22.99)
        );

        MenuItem item1 = new MenuItem("M4", "Burger Clasic", "Vită, salată, roșii",  32.99);
        MenuItem item2 = new MenuItem("M5", "Burger Deluxe", "Dublu cheddar, bacon", 45.99);
        MenuItem item3 = new MenuItem("M6", "Cartofi prăjiți", "Porție mare, crocant", 16.99);
        addItems(restaurantService, rs.restaurant2(),
            item1, item2, item3,
            new MenuItem("M9",  "Aripioare BBQ", "Aripioare de pui glazurate, sos BBQ afumat", 38.99),
            new MenuItem("M10", "Coleslaw", "Varză albă, morcov, maioneză ușoară", 12.99)
        );

        MenuItem item4 = new MenuItem("M11", "Salmon Nigiri (6 buc)", "Somon proaspăt pe orez, sos soia", 39.99);
        MenuItem item5 = new MenuItem("M12", "Tuna Roll (8 buc)", "Ton, avocado, castraveți, nori",  43.99);
        MenuItem item6 = new MenuItem("M13", "Supă Miso", "Tofu, alge wakame, ceapă verde", 18.99);
        addItems(restaurantService, rs.restaurant3(),
            item4, item5, item6,
            new MenuItem("M14", "Edamame", "Soia fiartă cu sare grunjoasă", 14.99),
            new MenuItem("M15", "Ebi Tempura (4 buc)", "Creveți în aluat crocant, sos ponzu", 49.99)
        );

        MenuItem item7 = new MenuItem("M16", "Buddha Bowl Vegan", "Quinoa, năut, avocado, legume la cuptor", 37.99);
        MenuItem item8 = new MenuItem("M17", "Smoothie Verde", "Spanac, banană, ghimbir, lapte de cocos", 19.99);
        MenuItem item9 = new MenuItem("M18", "Humus cu Pită", "Humus de casă, pită integrală, boia afumată", 24.99);
        addItems(restaurantService, rs.restaurant4(),
            item7, item8, item9,
            new MenuItem("M19", "Falafel Wrap", "Falafel crocant, tahini, salată, roșii", 31.99),
            new MenuItem("M20", "Tort Raw Vegan", "Dată, nuci, cacao crudă, strat mango", 26.99)
        );

        addItems(restaurantService, rs.restaurant5(),
            new MenuItem("M21", "Taco Carne Asada", "Vită marinată, guacamole, coriandru", 34.99),
            new MenuItem("M22", "Taco Pollo", "Pui la grătar, salsa verde, smântână", 31.99),
            new MenuItem("M23", "Nachos cu Chili", "Tortilla, fasole neagră, cheddar topit", 27.99),
            new MenuItem("M24", "Quesadilla Mixta", "Pui, ardei, cheddar, smântână", 33.99),
            new MenuItem("M25", "Churros cu Ciocolată", "Churros prăjiți, sos de ciocolată neagră", 21.99)
        );

        return new OrderItems(item1, item2, item3, item4, item5, item6, item7, item8, item9);
    }

    private static void seedOrders(OrderService orderService, Customers c, OrderItems items) {
        if (orderService.getOrdersByCustomer(c.customer1().getId()).isEmpty()) {
            c.customer1().getCart().addItem(items.item1());
            c.customer1().getCart().addItem(items.item2());
            c.customer1().getCart().addItem(items.item3());
            orderService.placeOrder(c.customer1(), new Address("Str. Floreasca", "7", "București"));
        }

        if (orderService.getOrdersByCustomer(c.customer2().getId()).isEmpty())
            seedDeliveredOrder(orderService, c.customer2(),
                new Address("Str. Ion Câmpineanu", "10", "București"),
                List.of(items.item4(), items.item5(), items.item6()),
                5, "Sushi proaspăt și bine prezentat, recomand!");

        if (orderService.getOrdersByCustomer(c.customer3().getId()).isEmpty())
            seedDeliveredOrder(orderService, c.customer3(),
                new Address("Str. Batiștei", "3", "București"),
                List.of(items.item7(), items.item8(), items.item9()),
                4, "Mâncare sănătoasă și gustoasă, smoothie-ul e delicios.");
    }

    private static void addItems(RestaurantService restaurantService, Restaurant restaurant, MenuItem... items) {
        Set<String> existing = new HashSet<>();
        for (MenuItem existingItem : restaurantService.getMenu(restaurant.getId()))
            existing.add(existingItem.getId());
        for (MenuItem item : items) {
            item.setRestaurant(restaurant);
            if (!existing.contains(item.getId()))
                restaurantService.addMenuItemToRestaurant(restaurant.getId(), item);
        }
    }

    private static void seedDeliveredOrder(OrderService svc, Customer customer, Address address, List<MenuItem> items, int rating, String comment) {
        for (MenuItem item : items)
            customer.getCart().addItem(item);
        Order order = svc.placeOrder(customer, address);
        svc.confirmOrder(order.getId());
        svc.markOrderReady(order.getId());
        svc.pickupOrder(order.getId());
        svc.deliverOrder(order.getId());
        svc.submitReview(order.getId(), rating, comment);
    }
}