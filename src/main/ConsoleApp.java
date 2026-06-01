package main;

import config.DatabaseConfiguration;
import exceptions.FoodiesException;
import models.*;
import service.OrderService;
import service.RestaurantService;
import service.UserService;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private static final int BOX_WIDTH = 50;

    private final UserService userService;
    private final RestaurantService restaurantService;
    private final OrderService orderService;
    private final Scanner scanner;
    private Customer currentUser;

    public ConsoleApp(UserService userService, RestaurantService restaurantService, OrderService orderService) {
        this.userService = userService;
        this.restaurantService = restaurantService;
        this.orderService = orderService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        try {
            DataSeeder.seed(userService, restaurantService, orderService);
            runInteractiveMode();
        } finally {
            scanner.close();
            DatabaseConfiguration.closeIfOpen();
        }
    }

    public void runInteractiveMode() {
        boolean exitApp = false;
        while (!exitApp) {
            if (!authenticate())
                return;
            exitApp = runUserSession();
            currentUser = null;
        }
    }

    private boolean runUserSession() {
        while (true) {
            printMenu();
            switch (scanner.nextLine().trim()) {
                case "1" -> showRestaurantSubmenu();
                case "2" -> showOrderSubmenu();
                case "3" -> {
                    System.out.println("\nTe-ai deconectat. Pe curând, " + currentUser.getName() + "!\n");
                    return false;
                }
                case "0" -> {
                    System.out.println("\nLa revedere, " + currentUser.getName() + "!\n");
                    return true;
                }
                default -> System.out.println("Opțiune invalidă. Alege 1, 2, 3 sau 0.\n");
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
                case "6" -> deleteOrder();
                case "0" -> inSubmenu = false;
                default  -> System.out.println("Opțiune invalidă. Alege un număr între 0 și 6.\n");
            }
        }
    }

    private boolean authenticate() {
        while (currentUser == null) {
            printAuthMenu();
            switch (scanner.nextLine().trim()) {
                case "1" -> login();
                case "2" -> register();
                case "0" -> {
                    System.out.println("\nLa revedere!\n");
                    return false;
                }
                default -> System.out.println("Opțiune invalidă. Alege 1, 2 sau 0.\n");
            }
        }
        return true;
    }

    private void printAuthMenu() {
        printBoxMenu("FOODIES", List.of(
                "1. Conectează-te",
                "2. Înregistrează-te",
                "0. Ieșire"
        ));
        System.out.print("Opțiune selectată: ");
    }

    private void login() {
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Parolă: ");
        String password = scanner.nextLine().trim();
        Customer customer = userService.login(email, password);
        if (customer == null) {
            System.out.println("\nEmail sau parolă incorecte. Încearcă din nou.\n");
            return;
        }
        currentUser = customer;
        System.out.println("\nBun venit înapoi, " + currentUser.getName() + "!\n");
    }

    private void register() {
        System.out.print("Nume: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        if (userService.findCustomerByEmail(email) != null) {
            System.out.println("\nExistă deja un cont cu acest email. Conectează-te.\n");
            return;
        }
        System.out.print("Parolă: ");
        String password = scanner.nextLine().trim();
        currentUser = new Customer("U" + System.currentTimeMillis(), name, email, password);
        userService.addCustomer(currentUser);
        System.out.println("\nCont creat cu succes. Bun venit, " + name + "!\n");
    }

    private void printMenu() {
        printBoxMenu("FOODIES", List.of(
                "1. Restaurante",
                "2. Comenzi",
                "3. Deconectare",
                "0. Ieșire"
        ));
        System.out.print("Opțiune selectată: ");
    }

    private void printRestaurantSubmenu() {
        printBoxMenu("RESTAURANTE", List.of(
                "1. Vezi toate restaurantele",
                "2. Explorează meniu restaurant",
                "3. Vezi recenzii restaurant",
                "0. Înapoi"
        ));
        System.out.print("Opțiune selectată: ");
    }

    private void printOrderSubmenu() {
        printBoxMenu("COMENZI", List.of(
                "1. Plasează comandă nouă",
                "2. Comenzile mele",
                "3. Lasă recenzie",
                "4. Repetă o comandă anterioară",
                "5. Detalii comandă",
                "6. Șterge o comandă",
                "0. Înapoi"
        ));
        System.out.print("Opțiune selectată: ");
    }

    private void showRestaurants() {
        System.out.println("\nAfișează restaurantele în ordine:");
        System.out.println("  1. După rating");
        System.out.println("  2. Alfabetică");
        System.out.print("Alegere: ");
        String sortChoice = scanner.nextLine().trim();
        if (sortChoice.equals("2")) {
            int width = printTitle("RESTAURANTE (ordine alfabetică)");
            int i = 1;
            for (Restaurant r : restaurantService.getRestaurantsSortedByName())
                System.out.println(i++ + ". " + r.toDisplayString());
            printSectionEnd(width);
        } else {
            int width = printTitle("RESTAURANTE (cele mai bine evaluate primele)");
            int i = 1;
            for (Restaurant r : restaurantService.getRestaurantsSortedByRating())
                System.out.println(i++ + ". " + r.toDisplayString());
            printSectionEnd(width);
        }
    }

    private void exploreMenu() {
        Restaurant chosen = pickRestaurant();
        if (chosen == null) return;
        int width = printTitle("MENIU: " + chosen.getName());
        printMenuItems(restaurantService.getMenu(chosen.getId()));
        printSectionEnd(width);
    }

    private void placeNewOrder() {
        Restaurant restaurant = pickRestaurant();
        if (restaurant == null) return;

        List<MenuItem> menu = restaurantService.getMenu(restaurant.getId());
        System.out.println("\nMeniu " + restaurant.getName() + ":");
        printMenuItems(menu);
        System.out.print("Alege produse (ex: 1,3): ");
        List<MenuItem> selected = parseItemSelection(scanner.nextLine().trim(), menu);
        if (selected.isEmpty()) { System.out.println("Niciun produs valid selectat.\n"); return; }

        System.out.print("Stradă livrare: ");
        String street = scanner.nextLine().trim();
        System.out.print("Număr: ");
        String number = scanner.nextLine().trim();
        Address deliveryAddress = new Address(street, number, restaurant.getAddress().getCity());

        Cart cart = currentUser.getCart();
        cart.clearCart();
        try {
            for (MenuItem item : selected) cart.addItem(item);
            Order newOrder = orderService.placeOrder(currentUser, deliveryAddress);
            System.out.println("\nComanda " + newOrder.getDisplayId() + " plasată cu succes. Stare: " +
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
        int width = printTitle("ISTORIC COMENZI");
        if (orders.isEmpty())
            System.out.println("Nu s-au găsit comenzi.");
        else
            for (Order order : orders) {
                String ratingInfo = order.getReview() != null ? " | Rating: " + order.getReview().getRating() + "/5" : "";
                System.out.println("Comanda " + order.getDisplayId() + " | " + order.getDate().format(formatter) +
                                   " | " + order.getRestaurant().getName() + " | " + order.getStatus().getLabel() +
                                   " | Total: " + String.format("%.2f", order.getTotal()) + " lei" + ratingInfo);
            }
        printSectionEnd(width);
    }

    private void leaveReview() {
        List<Order> reviewable = getReviewableOrders();
        if (reviewable.isEmpty()) { System.out.println("\nNu ai comenzi livrate fără recenzie.\n"); return; }

        System.out.println("\nComenzi disponibile pentru recenzie:");
        for (int i = 0; i < reviewable.size(); i++) {
            Order o = reviewable.get(i);
            System.out.println("  " + (i + 1) + ". " + o.getDisplayId() + " — " +
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
            System.out.println("  " + (i + 1) + ". " + o.getDisplayId() + " — " +
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
        Address newAddress = new Address(street, number, selected.getRestaurant().getAddress().getCity());
        try {
            Order newOrder = orderService.reorder(currentUser, selected.getId(), newAddress);
            System.out.println("\nComanda " + newOrder.getDisplayId() + " plasată (repetată din " + selected.getDisplayId() +
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
            System.out.println("  " + (i + 1) + ". " + o.getDisplayId() + " — " + o.getRestaurant().getName() +
                    " | " + o.getDate().format(formatter) + " | " + o.getStatus().getLabel());
        }
        System.out.print("Număr: ");
        int choice = readInt();
        if (choice < 1 || choice > orders.size()) { System.out.println("Opțiune invalidă.\n"); return; }
        Order selected = orders.get(choice - 1);
        int width = printTitle("DETALII COMANDĂ " + selected.getDisplayId());
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
            System.out.println("Recenzia ta: " + selected.getReview().getRating() + "/5 — " + selected.getReview().getComment());
        printSectionEnd(width);
    }

    private void deleteOrder() {
        List<Order> deletable = new ArrayList<>();
        for (Order o : orderService.getOrdersByCustomer(currentUser.getId()))
            if (o.getStatus() == OrderStatus.DELIVERED || o.getStatus() == OrderStatus.CANCELLED)
                deletable.add(o);
        if (deletable.isEmpty()) { System.out.println("\nNu ai comenzi livrate sau anulate de șters.\n"); return; }

        System.out.println("\nComenzi care pot fi șterse:");
        for (int i = 0; i < deletable.size(); i++) {
            Order o = deletable.get(i);
            System.out.println("  " + (i + 1) + ". " + o.getDisplayId() + " — " +
                    o.getRestaurant().getName() + " (" + o.getStatus().getLabel() + ")");
        }
        System.out.print("Alege comanda de șters: ");
        int choice = readInt();
        if (choice < 1 || choice > deletable.size()) { System.out.println("Opțiune invalidă.\n"); return; }
        Order selected = deletable.get(choice - 1);

        System.out.print("Sigur dorești să ștergi comanda " + selected.getDisplayId() + "? (da/nu): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("da")) { System.out.println("Ștergere anulată.\n"); return; }

        try {
            orderService.deleteOrder(currentUser, selected.getId());
            System.out.println("Comanda " + selected.getDisplayId() + " a fost ștearsă.\n");
        } catch (FoodiesException e) {
            System.out.println("Eroare: " + e.getMessage() + "\n");
        }
    }

    private void browseRestaurantReviews() {
        Restaurant chosen = pickRestaurant();
        if (chosen == null) return;
        int width = printTitle("RECENZII: " + chosen.getName());
        List<Review> reviews = chosen.getReviews();
        if (reviews.isEmpty())
            System.out.println("Nicio recenzie disponibilă.");
        else
            for (Review r : reviews)
                System.out.println("  " + r);
        printSectionEnd(width);
    }

    private void runOrderLifecycle(Order order) {
        if (simulateOrderProgression(order)) {
            System.out.println("Comanda " + order.getDisplayId() + " livrată! Total: " +
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

    private boolean simulateOrderProgression(Order order) {
        String orderId = order.getId();
        String displayId = order.getDisplayId();

        System.out.print("Apasă Enter când restaurantul confirmă comanda, sau 'c' pentru anulare: ");
        if (scanner.nextLine().trim().equalsIgnoreCase("c")) {
            orderService.restaurantCancelOrder(orderId);
            System.out.println("Comanda " + displayId + " a fost anulată de restaurant.\n");
            return false;
        }
        orderService.confirmOrder(orderId);
        Order current = orderService.getOrderById(orderId);
        System.out.println("Comanda confirmată de restaurant. Stare: " + current.getStatus().getLabel() +
                ". [" + current.getStatusChangeTime() + "]");

        System.out.print("Apasă Enter când restaurantul marchează comanda gata: ");
        scanner.nextLine();
        orderService.markOrderReady(orderId);
        current = orderService.getOrderById(orderId);
        String driverName = current.getDriver() != null ? current.getDriver().getName() : "necunoscut";
        System.out.println("Comanda e gata de ridicare (" + current.getStatus().getLabel() +
                "). Curier asignat: " + driverName + ". [" + current.getStatusChangeTime() + "]");

        System.out.print("Apasă Enter când curierul ridică comanda: ");
        scanner.nextLine();
        orderService.pickupOrder(orderId);
        current = orderService.getOrderById(orderId);
        System.out.println("Stare: " + current.getStatus().getLabel() + ". [" + current.getStatusChangeTime() + "]");

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

    private void printBoxMenu(String title, List<String> options) {
        int width = BOX_WIDTH;
        for (String option : options)
            width = Math.max(width, option.length() + 4);
        width = Math.max(width, title.length() + 4);

        printLine('-', width);
        System.out.println("|" + center(title, width - 2) + "|");
        printLine('-', width);
        for (String option : options)
            System.out.println("| " + padRight(option, width - 4) + " |");
        printLine('-', width);
    }

    private int printTitle(String title) {
        String text = " " + title + " ";
        int width = Math.max(BOX_WIDTH, title.length() + 8);
        int left = (width - text.length()) / 2;
        int right = width - text.length() - left;

        System.out.println();
        System.out.println("=".repeat(left) + text + "=".repeat(right));
        return width;
    }

    private void printSectionEnd(int width) {
        printLine('=', width);
        System.out.println();
    }

    private void printLine(char ch) {
        printLine(ch, BOX_WIDTH);
    }

    private void printLine(char ch, int width) {
        System.out.println(String.valueOf(ch).repeat(width));
    }

    private String center(String text, int width) {
        int left = Math.max(0, (width - text.length()) / 2);
        int right = Math.max(0, width - text.length() - left);
        return " ".repeat(left) + text + " ".repeat(right);
    }

    private String padRight(String text, int width) {
        if (text.length() >= width)
            return text;
        return text + " ".repeat(width - text.length());
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