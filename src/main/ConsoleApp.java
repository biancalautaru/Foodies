package main;

import models.*;
import service.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private final UserService userService;
    private final RestaurantService restaurantService;
    private final MenuService menuService;
    private final OrderService orderService;
    private final Scanner scanner;
    private Customer currentUser;

    public ConsoleApp(UserService userService, RestaurantService restaurantService, MenuService menuService, OrderService orderService) {
        this.userService = userService;
        this.restaurantService = restaurantService;
        this.menuService = menuService;
        this.orderService = orderService;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        registerUser();
        boolean running = true;
        while (running) {
            printMenu();
            switch (scanner.nextLine().trim()) {
                case "1" -> showRestaurants();
                case "2" -> exploreMenu();
                case "3" -> placeNewOrder();
                case "4" -> showMyOrders();
                case "5" -> leaveReview();
                case "6" -> repeatOrder();
                case "7" -> cancelMyOrder();
                case "8" -> {
                    System.out.println("\nLa revedere, " + currentUser.getName() + "!\n");
                    running = false;
                }
                default -> System.out.println("Opțiune invalidă. Alege un număr între 0 și 7.\n");
            }
        }
        scanner.close();
    }

    // Inregistrare client
    private void registerUser() {
        System.out.print("Nume: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Telefon: ");
        String phone = scanner.nextLine().trim();

        currentUser = new Customer("U" + System.currentTimeMillis(), name, email, phone);
        userService.addCustomer(currentUser);
        System.out.println("\nCont creat cu succes. Bun venit, " + name + "!\n");
    }

    // Meniu interactiv
    private void printMenu() {
        System.out.println("------------------------------------------");
        System.out.println("|                 FOODIES                |");
        System.out.println("------------------------------------------");
        System.out.println("|  1. Restaurante disponibile            |");
        System.out.println("|  2. Explorează meniu restaurant        |");
        System.out.println("|  3. Plasează comandă nouă              |");
        System.out.println("|  4. Comenzile mele                     |");
        System.out.println("|  5. Lasă recenzie                      |");
        System.out.println("|  6. Repetă o comandă anterioară        |");
        System.out.println("|  7. Anulează o comandă activă          |");
        System.out.println("|  8. Ieșire                             |");
        System.out.println("|-----------------------------------------");
        System.out.print("Opțiune selectată: ");
    }

    // 1. Restaurante disponibile
    private void showRestaurants() {
        restaurantService.displayRestaurantsSortedByRating();
    }

    // 2. Explorează meniu restaurant
    private void exploreMenu() {
        List<Restaurant> list = restaurantService.getRestaurantsSortedByRating();
        System.out.println("\nAlegeți restaurantul:");
        for (int i = 0; i < list.size(); i++)
            System.out.println("  " + (i + 1) + ". " + list.get(i).getName());
        System.out.print("Număr: ");
        int choice = readInt();
        if (choice < 1 || choice > list.size()) {
            System.out.println("Opțiune invalidă.\n");
            return;
        }
        menuService.displayMenuSortedByPrice(list.get(choice - 1).getId());
    }

    // 3. Plasează comandă nouă
    private void placeNewOrder() {
        List<Restaurant> restaurants = restaurantService.getRestaurantsSortedByRating();
        System.out.println("\nAlegeți restaurantul:");
        for (int i = 0; i < restaurants.size(); i++)
            System.out.println("  " + (i + 1) + ". " + restaurants.get(i).getName());
        System.out.print("Număr: ");
        int rChoice = readInt();
        if (rChoice < 1 || rChoice > restaurants.size()) {
            System.out.println("Opțiune invalidă.\n");
            return;
        }
        Restaurant restaurant = restaurants.get(rChoice - 1);

        List<MenuItem> menu = menuService.getMenuSortedByPrice(restaurant.getId());
        System.out.println("\nMeniu " + restaurant.getName() + ":");
        for (int i = 0; i < menu.size(); i++)
            System.out.println("  " + (i + 1) + ". " + menu.get(i));
        System.out.print("Alege produse (ex: 1,3): ");
        String[] parts = scanner.nextLine().trim().split(",");
        List<MenuItem> selected = new ArrayList<>();
        for (String part : parts) {
            try {
                int idx = Integer.parseInt(part.trim()) - 1;
                if (idx >= 0 && idx < menu.size())
                    selected.add(menu.get(idx));
            } catch (NumberFormatException ignored) {}
        }
        if (selected.isEmpty()) {
            System.out.println("Niciun produs valid selectat.\n");
            return;
        }

        System.out.print("Stradă livrare: ");
        String street = scanner.nextLine().trim();
        System.out.print("Număr: ");
        String number = scanner.nextLine().trim();
        Address deliveryAddress = new Address(street, number, restaurant.getAddress().city());

        Cart cart = currentUser.getCart();
        cart.clearCart();
        try {
            for (MenuItem item : selected)
                cart.addItem(item);
            orderService.placeOrder(currentUser, deliveryAddress);

            List<Order> myOrders = orderService.getOrdersByCustomer(currentUser.getId());
            Order newOrder = myOrders.get(myOrders.size() - 1);
            String orderId = newOrder.getId();

            System.out.println("\nComanda " + orderId + " plasată cu succes. Stare: PREPARING.");
            if (simulateOrderProgression(orderId, newOrder))
                System.out.println("Comanda " + orderId + " a fost livrată! Total: " +
                        String.format("%.2f", newOrder.getTotal()) + " lei\n");
        } catch (Exception e) {
            System.out.println("Eroare: " + e.getMessage() + "\n");
            cart.clearCart();
        }
    }

    // 4. Comenzile mele
    private void showMyOrders() {
        orderService.getCustomerOrderHistory(currentUser.getId());
    }

    // 5. Lasă recenzie
    private void leaveReview() {
        List<Order> reviewable = new ArrayList<>();
        for (Order o : orderService.getOrdersByCustomer(currentUser.getId()))
            if (o.getStatus() == OrderStatus.DELIVERED && o.getReview() == null)
                reviewable.add(o);

        if (reviewable.isEmpty()) {
            System.out.println("\nNu ai comenzi livrate fără recenzie.\n");
            return;
        }
        System.out.println("\nComenzi disponibile pentru recenzie:");
        for (int i = 0; i < reviewable.size(); i++) {
            Order o = reviewable.get(i);
            System.out.println("  " + (i + 1) + ". " + o.getId() + " — " +
                    o.getRestaurant().getName() + " (" + String.format("%.2f", o.getTotal()) + " lei)");
        }
        System.out.print("Alege comanda: ");
        int choice = readInt();
        if (choice < 1 || choice > reviewable.size()) {
            System.out.println("Opțiune invalidă.\n");
            return;
        }
        Order selected = reviewable.get(choice - 1);
        System.out.print("Rating (1-5): ");
        int rating = readInt();
        System.out.print("Comentariu: ");
        String comment = scanner.nextLine().trim();
        try {
            orderService.submitReview(selected.getId(), rating, comment);
            System.out.println("Recenzie trimisă! Mulțumim pentru feedback.\n");
        } catch (Exception e) {
            System.out.println("Eroare: " + e.getMessage() + "\n");
        }
    }

    // 6. Repetă o comandă anterioară
    private void repeatOrder() {
        List<Order> delivered = new ArrayList<>();
        for (Order o : orderService.getOrdersByCustomer(currentUser.getId()))
            if (o.getStatus() == OrderStatus.DELIVERED)
                delivered.add(o);

        if (delivered.isEmpty()) {
            System.out.println("\nNu ai comenzi livrate din care să repeți.\n");
            return;
        }
        System.out.println("\nComenzi livrate:");
        for (int i = 0; i < delivered.size(); i++) {
            Order o = delivered.get(i);
            System.out.println("  " + (i + 1) + ". " + o.getId() + " — " +
                    o.getRestaurant().getName() + " (" + String.format("%.2f", o.getTotal()) + " lei)");
        }
        System.out.print("Alege comanda de repetat: ");
        int choice = readInt();
        if (choice < 1 || choice > delivered.size()) {
            System.out.println("Opțiune invalidă.\n");
            return;
        }
        Order selected = delivered.get(choice - 1);
        System.out.print("Stradă nouă de livrare: ");
        String street = scanner.nextLine().trim();
        System.out.print("Număr: ");
        String number = scanner.nextLine().trim();
        Address newAddress = new Address(street, number, selected.getRestaurant().getAddress().city());
        try {
            orderService.reorder(currentUser, selected.getId(), newAddress);

            List<Order> myOrders = orderService.getOrdersByCustomer(currentUser.getId());
            Order newOrder = myOrders.get(myOrders.size() - 1);
            String orderId = newOrder.getId();

            System.out.println("\nComanda " + orderId + " plasată (repetată din " + selected.getId() +
                    "). Stare: PREPARING.");
            if (simulateOrderProgression(orderId, newOrder))
                System.out.println("Comanda " + orderId + " livrată! Total: " +
                        String.format("%.2f", newOrder.getTotal()) + " lei\n");
        } catch (Exception e) {
            System.out.println("Eroare: " + e.getMessage() + "\n");
        }
    }

    // 7. Anulează o comandă activă
    private void cancelMyOrder() {
        List<Order> cancellable = new ArrayList<>();
        for (Order o : orderService.getOrdersByCustomer(currentUser.getId()))
            if (o.getStatus() == OrderStatus.PENDING
                    || o.getStatus() == OrderStatus.PREPARING
                    || o.getStatus() == OrderStatus.READY_FOR_PICKUP
                    || o.getStatus() == OrderStatus.OUT_FOR_DELIVERY)
                cancellable.add(o);

        if (cancellable.isEmpty()) {
            System.out.println("\nNu ai comenzi active care pot fi anulate.\n");
            return;
        }

        System.out.println("\nComenzi active:");
        for (int i = 0; i < cancellable.size(); i++) {
            Order o = cancellable.get(i);
            String fee = switch (o.getStatus()) {
                case PENDING, PREPARING -> "fără taxă";
                case READY_FOR_PICKUP -> "taxă 30% (" + String.format("%.2f", 0.30 * o.getSubtotal()) + " lei)";
                case OUT_FOR_DELIVERY -> "taxă 100% din subtotal + taxă livrare (" + String.format("%.2f", o.getSubtotal() + o.getDeliveryFee()) + " lei)";
                default -> "";
            };
            System.out.println("  " + (i + 1) + ". " + o.getId() + " — " + o.getRestaurant().getName() + " | " + o.getStatus() + " | " + fee);
        }
        System.out.print("Alege comanda de anulat (0 pentru renunțare): ");
        int choice = readInt();
        if (choice < 1 || choice > cancellable.size()) {
            System.out.println("Anulare abandonată.\n");
            return;
        }
        try {
            orderService.cancelOrder(cancellable.get(choice - 1).getId());
            System.out.println();
        } catch (Exception e) {
            System.out.println("Eroare: " + e.getMessage() + "\n");
        }
    }

    // Simulare progres comanda
    private boolean simulateOrderProgression(String orderId, Order order) {
        // Restaurantul confirma comanda (PENDING -> PREPARING)
        System.out.print("Apasă Enter când restaurantul confirmă comanda, sau 'c' pentru a anula (fără taxă): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("c")) {
            orderService.cancelOrder(orderId);
            System.out.println();
            return false;
        }
        orderService.confirmOrder(orderId);
        System.out.println("Comanda a fost confirmată de restaurant. Stare: PREPARING.");

        // Restaurantul marcheaza comanda gata (PREPARING -> READY_FOR_PICKUP)
        System.out.print("Apasă Enter când restaurantul marchează comanda gata, sau 'c' pentru a anula (fără taxă): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("c")) {
            orderService.cancelOrder(orderId);
            System.out.println();
            return false;
        }
        orderService.markOrderReady(orderId);
        String driverName = order.getDriver() != null ? order.getDriver().getName() : "necunoscut";
        System.out.println("Comanda e gata de ridicare. Curier asignat: " + driverName + ".");

        // Curierul ridica comanda (READY_FOR_PICKUP -> OUT_FOR_DELIVERY)
        System.out.print("Apasă Enter când curierul ridică comanda, sau 'c' pentru a anula (taxă 20%): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("c")) {
            orderService.cancelOrder(orderId);
            System.out.println();
            return false;
        }
        orderService.pickupOrder(orderId);
        System.out.println("Comanda e în livrare.");

        // Comanda este livrata (OUT_FOR_DELIVERY -> DELIVERED)
        System.out.print("Apasă Enter când comanda este livrată, sau 'c' pentru a anula (taxă 100%): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("c")) {
            orderService.cancelOrder(orderId);
            System.out.println();
            return false;
        }
        orderService.deliverOrder(orderId);
        return true;
    }

    // Utilitar pentru citirea unui numar intreg
    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}