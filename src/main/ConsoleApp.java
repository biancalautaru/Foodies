package main;

import exceptions.FoodiesException;
import interfaces.IMenuService;
import interfaces.IOrderService;
import interfaces.IRestaurantService;
import interfaces.IUserService;
import models.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private final IUserService userService;
    private final IRestaurantService restaurantService;
    private final IMenuService menuService;
    private final IOrderService orderService;
    private final Scanner scanner;
    private Customer currentUser;

    public ConsoleApp(IUserService userService, IRestaurantService restaurantService, IMenuService menuService, IOrderService orderService) {
        this.userService = userService;
        this.restaurantService = restaurantService;
        this.menuService = menuService;
        this.orderService = orderService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        DataSeeder.seed(userService, restaurantService, menuService, orderService);
        runInteractiveMode();
        scanner.close();
    }

    public void runInteractiveMode() {
        registerUser();
        boolean running = true;
        while (running) {
            printMenu();
            switch (scanner.nextLine().trim()) {
                case "1" -> showRestaurantSubmenu();
                case "2" -> showOrderSubmenu();
                case "0" -> {
                    System.out.println("\nLa revedere, " + currentUser.getName() + "!\n");
                    running = false;
                }
                default -> System.out.println("Opțiune invalidă. Alege 1, 2 sau 0.\n");
            }
        }
    }

    private void showRestaurantSubmenu() {
        boolean inSubmenu = true;
        while (inSubmenu) {
            printRestaurantSubmenu();
            switch (scanner.nextLine().trim()) {
                case "1" -> showRestaurants();
                case "2" -> exploreMenu();
                case "3" -> browseRestaurantReviews();
                case "0" -> inSubmenu = false;
                default  -> System.out.println("Opțiune invalidă. Alege un număr între 0 și 3.\n");
            }
        }
    }

    private void showOrderSubmenu() {
        boolean inSubmenu = true;
        while (inSubmenu) {
            printOrderSubmenu();
            switch (scanner.nextLine().trim()) {
                case "1" -> placeNewOrder();
                case "2" -> showMyOrders();
                case "3" -> leaveReview();
                case "4" -> repeatOrder();
                case "5" -> viewOrderDetails();
                case "0" -> inSubmenu = false;
                default  -> System.out.println("Opțiune invalidă. Alege un număr între 0 și 5.\n");
            }
        }
    }

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

    private void printMenu() {
        System.out.println("------------------------------------------");
        System.out.println("|                 FOODIES                |");
        System.out.println("------------------------------------------");
        System.out.println("|  1. Restaurante                        |");
        System.out.println("|  2. Comenzi                            |");
        System.out.println("|  0. Ieșire                             |");
        System.out.println("------------------------------------------");
        System.out.print("Opțiune selectată: ");
    }

    private void printRestaurantSubmenu() {
        System.out.println("------------------------------------------");
        System.out.println("|             RESTAURANTE                |");
        System.out.println("------------------------------------------");
        System.out.println("|  1. Vezi toate restaurantele           |");
        System.out.println("|  2. Explorează meniu restaurant        |");
        System.out.println("|  3. Recenzii restaurant                |");
        System.out.println("|  0. Înapoi                             |");
        System.out.println("------------------------------------------");
        System.out.print("Opțiune selectată: ");
    }

    private void printOrderSubmenu() {
        System.out.println("------------------------------------------");
        System.out.println("|               COMENZI                  |");
        System.out.println("------------------------------------------");
        System.out.println("|  1. Plasează comandă nouă              |");
        System.out.println("|  2. Comenzile mele                     |");
        System.out.println("|  3. Lasă recenzie                      |");
        System.out.println("|  4. Repetă o comandă anterioară        |");
        System.out.println("|  5. Detalii comandă                    |");
        System.out.println("|  0. Înapoi                             |");
        System.out.println("------------------------------------------");
        System.out.print("Opțiune selectată: ");
    }

    private void showRestaurants() {
        System.out.println("\nAfișează restaurantele în ordine:");
        System.out.println("  1. După rating (implicit)");
        System.out.println("  2. Alfabetică");
        System.out.print("Alegere: ");
        String sortChoice = scanner.nextLine().trim();
        if (sortChoice.equals("2")) {
            System.out.println("\n===== RESTAURANTE (ordine alfabetică) =====");
            int i = 1;
            for (Restaurant r : restaurantService.getRestaurantsSortedByName())
                System.out.println(i++ + ". " + r.toDisplayString());
            System.out.println("==========================================\n");
        } else {
            System.out.println("\n===== RESTAURANTE (cele mai bine evaluate primele) =====");
            int i = 1;
            for (Restaurant r : restaurantService.getRestaurantsSortedByRating())
                System.out.println(i++ + ". " + r.toDisplayString());
            System.out.println("========================================================\n");
        }
    }

    private void exploreMenu() {
        Restaurant chosen = pickRestaurant();
        if (chosen == null) return;
        System.out.println("\n===== MENIU (cele mai ieftine primele): " + chosen.getName() + " =====");
        printMenuItems(menuService.getMenuSortedByPrice(chosen.getId()));
        System.out.println("==========================\n");
    }

    private void placeNewOrder() {
        Restaurant restaurant = pickRestaurant();
        if (restaurant == null) return;

        List<MenuItem> menu = menuService.getMenuSortedByPrice(restaurant.getId());
        System.out.println("\nMeniu " + restaurant.getName() + ":");
        printMenuItems(menu);
        System.out.print("Alege produse (ex: 1,3): ");
        List<MenuItem> selected = parseItemSelection(scanner.nextLine().trim(), menu);
        if (selected.isEmpty()) { System.out.println("Niciun produs valid selectat.\n"); return; }

        System.out.print("Stradă livrare: ");
        String street = scanner.nextLine().trim();
        System.out.print("Număr: ");
        String number = scanner.nextLine().trim();
        Address deliveryAddress = new Address(street, number, restaurant.getAddress().city());

        Cart cart = currentUser.getCart();
        cart.clearCart();
        try {
            for (MenuItem item : selected) cart.addItem(item);
            orderService.placeOrder(currentUser, deliveryAddress);
            List<Order> myOrders = orderService.getOrdersByCustomer(currentUser.getId());
            Order newOrder = myOrders.get(myOrders.size() - 1);
            System.out.println("\nComanda " + newOrder.getId() + " plasată cu succes. Stare: " +
                    newOrder.getStatus().getLabel() + ". [" + newOrder.getStatusChangeTime() + "]");
            runOrderLifecycle(newOrder);
        } catch (FoodiesException e) {
            System.out.println("Eroare: " + e.getMessage() + "\n");
            cart.clearCart();
        }
    }

    private void showMyOrders() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        List<Order> orders = orderService.getOrdersByCustomer(currentUser.getId());
        System.out.println("\n===== ISTORIC COMENZI =====");
        if (orders.isEmpty())
            System.out.println("Nu s-au găsit comenzi.");
        else
            for (Order order : orders) {
                String ratingInfo = order.getReview() != null ? " | Rating: " + order.getReview().rating() + "/5" : "";
                System.out.println("Comanda " + order.getId() + " | " + order.getDate().format(formatter) +
                                   " | " + order.getRestaurant().getName() + " | " + order.getStatus().getLabel() +
                                   " | Total: " + String.format("%.2f", order.getTotal()) + " lei" + ratingInfo);
            }
        System.out.println("===========================\n");
    }

    private void leaveReview() {
        List<Order> reviewable = getReviewableOrders();
        if (reviewable.isEmpty()) { System.out.println("\nNu ai comenzi livrate fără recenzie.\n"); return; }

        System.out.println("\nComenzi disponibile pentru recenzie:");
        for (int i = 0; i < reviewable.size(); i++) {
            Order o = reviewable.get(i);
            System.out.println("  " + (i + 1) + ". " + o.getId() + " — " +
                    o.getRestaurant().getName() + " (" + String.format("%.2f", o.getTotal()) + " lei)");
        }
        System.out.print("Alege comanda: ");
        int choice = readInt();
        if (choice < 1 || choice > reviewable.size()) { System.out.println("Opțiune invalidă.\n"); return; }
        promptReview(reviewable.get(choice - 1));
    }

    private void repeatOrder() {
        List<Order> delivered = getDeliveredOrders();
        if (delivered.isEmpty()) { System.out.println("\nNu ai comenzi livrate din care să repeți.\n"); return; }

        System.out.println("\nComenzi livrate:");
        for (int i = 0; i < delivered.size(); i++) {
            Order o = delivered.get(i);
            System.out.println("  " + (i + 1) + ". " + o.getId() + " — " +
                    o.getRestaurant().getName() + " (" + String.format("%.2f", o.getTotal()) + " lei)");
        }
        System.out.print("Alege comanda de repetat: ");
        int choice = readInt();
        if (choice < 1 || choice > delivered.size()) { System.out.println("Opțiune invalidă.\n"); return; }
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
            System.out.println("\nComanda " + newOrder.getId() + " plasată (repetată din " + selected.getId() +
                    "). Stare: " + newOrder.getStatus().getLabel() + ". [" + newOrder.getStatusChangeTime() + "]");
            runOrderLifecycle(newOrder);
        } catch (FoodiesException e) {
            System.out.println("Eroare: " + e.getMessage() + "\n");
        }
    }

    private void viewOrderDetails() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        List<Order> orders = orderService.getOrdersByCustomer(currentUser.getId());
        if (orders.isEmpty()) { System.out.println("\nNu ai comenzi înregistrate.\n"); return; }

        System.out.println("\nAlegeți comanda:");
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            System.out.println("  " + (i + 1) + ". " + o.getId() + " — " + o.getRestaurant().getName() +
                    " | " + o.getDate().format(formatter) + " | " + o.getStatus().getLabel());
        }
        System.out.print("Număr: ");
        int choice = readInt();
        if (choice < 1 || choice > orders.size()) { System.out.println("Opțiune invalidă.\n"); return; }
        Order selected = orders.get(choice - 1);
        System.out.println("\n===== DETALII COMANDĂ " + selected.getId() + " =====");
        System.out.println("Restaurant: " + selected.getRestaurant().getName());
        System.out.println("Data: " + selected.getDate().format(formatter));
        System.out.println("Stare: " + selected.getStatus().getLabel());
        System.out.println("Produse:");
        for (MenuItem item : selected.getItems())
            System.out.println("  - " + item.getName() + " — " + String.format("%.2f", item.getPrice()) + " lei");
        System.out.println("Subtotal: " + String.format("%.2f", selected.getSubtotal()) + " lei");
        System.out.println("Livrare: " + String.format("%.2f", selected.getDeliveryFee()) + " lei");
        System.out.println("Total: " + String.format("%.2f", selected.getTotal()) + " lei");
        if (selected.getReview() != null)
            System.out.println("Recenzia ta: " + selected.getReview().rating() + "/5 — " + selected.getReview().comment());
        System.out.println("==========================================\n");
    }

    private void browseRestaurantReviews() {
        Restaurant chosen = pickRestaurant();
        if (chosen == null) return;
        System.out.println("\n===== RECENZII: " + chosen.getName() + " =====");
        List<Review> reviews = chosen.getReviews();
        if (reviews.isEmpty())
            System.out.println("Nicio recenzie disponibilă.");
        else
            for (Review r : reviews)
                System.out.println("  " + r);
        System.out.println("==========================================\n");
    }

    private void runOrderLifecycle(Order order) {
        if (simulateOrderProgression(order.getId(), order)) {
            System.out.println("Comanda " + order.getId() + " livrată! Total: " +
                    String.format("%.2f", order.getTotal()) + " lei\n");
            promptForReviewAfterDelivery(order);
        }
    }

    private void promptForReviewAfterDelivery(Order order) {
        System.out.print("Dorești să lași o recenzie acum? (da/nu): ");
        if (scanner.nextLine().trim().equalsIgnoreCase("da")) {
            promptReview(order);
        } else {
            System.out.println("Poți lăsa recenzia mai târziu din meniul Comenzi.\n");
        }
    }

    private boolean simulateOrderProgression(String orderId, Order order) {
        System.out.print("Apasă Enter când restaurantul confirmă comanda, sau 'c' pentru anulare: ");
        if (scanner.nextLine().trim().equalsIgnoreCase("c")) {
            orderService.restaurantCancelOrder(orderId);
            System.out.println("Comanda " + orderId + " a fost anulată de restaurant.\n");
            return false;
        }
        orderService.confirmOrder(orderId);
        System.out.println("Comanda confirmată de restaurant. Stare: " + order.getStatus().getLabel() +
                ". [" + order.getStatusChangeTime() + "]");

        System.out.print("Apasă Enter când restaurantul marchează comanda gata: ");
        scanner.nextLine();
        orderService.markOrderReady(orderId);
        String driverName = order.getDriver() != null ? order.getDriver().getName() : "necunoscut";
        System.out.println("Comanda e gata de ridicare (" + order.getStatus().getLabel() +
                "). Curier asignat: " + driverName + ". [" + order.getStatusChangeTime() + "]");

        System.out.print("Apasă Enter când curierul ridică comanda: ");
        scanner.nextLine();
        orderService.pickupOrder(orderId);
        System.out.println("Stare: " + order.getStatus().getLabel() + ". [" + order.getStatusChangeTime() + "]");

        System.out.print("Apasă Enter când comanda este livrată: ");
        scanner.nextLine();
        orderService.deliverOrder(orderId);
        return true;
    }

    private Restaurant pickRestaurant() {
        List<Restaurant> list = restaurantService.getRestaurantsSortedByRating();
        System.out.println("\nAlegeți restaurantul:");
        for (int i = 0; i < list.size(); i++)
            System.out.println("  " + (i + 1) + ". " + list.get(i).getName());
        System.out.print("Număr: ");
        int choice = readInt();
        if (choice < 1 || choice > list.size()) {
            System.out.println("Opțiune invalidă.\n");
            return null;
        }
        return list.get(choice - 1);
    }

    private void printMenuItems(List<MenuItem> items) {
        for (int i = 0; i < items.size(); i++) {
            MenuItem item = items.get(i);
            System.out.println("  " + (i + 1) + ". " + item.toDisplayString());
            if (item.getDescription() != null && !item.getDescription().isBlank())
                System.out.println("       " + item.getDescription());
        }
    }

    private List<Order> getReviewableOrders() {
        List<Order> result = new ArrayList<>();
        for (Order o : getDeliveredOrders())
            if (o.getReview() == null)
                result.add(o);
        return result;
    }

    private List<Order> getDeliveredOrders() {
        List<Order> result = new ArrayList<>();
        for (Order o : orderService.getOrdersByCustomer(currentUser.getId()))
            if (o.getStatus() == OrderStatus.DELIVERED)
                result.add(o);
        return result;
    }

    private void promptReview(Order order) {
        System.out.print("Rating (1-5): ");
        int rating = readInt();
        System.out.print("Comentariu: ");
        String comment = scanner.nextLine().trim();
        try {
            orderService.submitReview(order.getId(), rating, comment);
            System.out.println("Recenzie trimisă! Mulțumim pentru feedback.\n");
        } catch (FoodiesException e) {
            System.out.println("Eroare la trimiterea recenziei: " + e.getMessage() + "\n");
        }
    }

    private List<MenuItem> parseItemSelection(String input, List<MenuItem> menu) {
        List<MenuItem> selected = new ArrayList<>();
        for (String part : input.split(",")) {
            try {
                int idx = Integer.parseInt(part.trim()) - 1;
                if (idx >= 0 && idx < menu.size())
                    selected.add(menu.get(idx));
            } catch (NumberFormatException ignored) {}
        }
        return selected;
    }

    private int readInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}