package main;

import models.*;
import service.*;

import java.time.format.DateTimeFormatter;
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
                case "7" -> viewOrderDetails();
                case "8" -> browseRestaurantReviews();
                case "9" -> findRestaurantsByName();
                case "10" -> {
                    System.out.println("\nLa revedere, " + currentUser.getName() + "!\n");
                    running = false;
                }
                default -> System.out.println("Opțiune invalidă. Alege un număr între 1 și 10.\n");
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
        System.out.println("|  7. Detalii comandă                    |");
        System.out.println("|  8. Recenzii restaurant                |");
        System.out.println("|  9. Restaurante după nume              |");
        System.out.println("|  10. Ieșire                            |");
        System.out.println("|-----------------------------------------");
        System.out.print("Opțiune selectată: ");
    }

    // 1. Restaurante disponibile
    private void showRestaurants() {
        System.out.println("\n===== TOATE RESTAURANTELE (cele mai bine evaluate primele) =====");
        int index = 1;
        for (Restaurant restaurant : restaurantService.getRestaurantsSortedByRating())
            System.out.println(index++ + ". " + restaurant);
        System.out.println("=============================================\n");
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
        Restaurant chosen = list.get(choice - 1);
        System.out.println("\n===== MENIU (cele mai ieftine primele): " + chosen.getName() + " =====");
        for (MenuItem item : menuService.getMenuSortedByPrice(chosen.getId())) {
            System.out.println("  " + item);
            if (item.getDescription() != null && !item.getDescription().isBlank())
                System.out.println("    " + item.getDescription());
        }
        System.out.println("==========================\n");
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
        for (int i = 0; i < menu.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + menu.get(i));
            if (menu.get(i).getDescription() != null && !menu.get(i).getDescription().isBlank())
                System.out.println("       " + menu.get(i).getDescription());
        }
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

            System.out.println("\nComanda " + orderId + " plasată cu succes. Stare: " + newOrder.getStatus().getLabel() + ". [" + newOrder.getStatusChangeTime() + "]");
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        List<Order> orders = orderService.getOrdersByCustomer(currentUser.getId());
        System.out.println("\n===== ISTORIC COMENZI =====");
        if (orders.isEmpty())
            System.out.println("Nu s-au găsit comenzi.");
        else
            for (Order order : orders) {
                String ratingInfo = order.getReview() != null
                        ? " | Rating: " + order.getReview().rating() + "/5"
                        : "";
                System.out.println("Comanda " + order.getId() + " | " + order.getDate().format(formatter) + " | " +
                        order.getRestaurant().getName() + " | " + order.getStatus().getLabel() + " | " +
                        String.format("%.2f", order.getTotal()) + " lei" + ratingInfo);
            }
        System.out.println("=========================\n");
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
                    "). Stare: " + newOrder.getStatus().getLabel() + ". [" + newOrder.getStatusChangeTime() + "]");
            if (simulateOrderProgression(orderId, newOrder))
                System.out.println("Comanda " + orderId + " livrată! Total: " +
                        String.format("%.2f", newOrder.getTotal()) + " lei\n");
        } catch (Exception e) {
            System.out.println("Eroare: " + e.getMessage() + "\n");
        }
    }

    // 7. Detalii comandă
    private void viewOrderDetails() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        List<Order> orders = orderService.getOrdersByCustomer(currentUser.getId());
        if (orders.isEmpty()) {
            System.out.println("\nNu ai comenzi înregistrate.\n");
            return;
        }
        System.out.println("\nAlegeți comanda:");
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            System.out.println("  " + (i + 1) + ". " + o.getId() + " — " + o.getRestaurant().getName() +
                    " | " + o.getDate().format(formatter) + " | " + o.getStatus().getLabel());
        }
        System.out.print("Număr: ");
        int choice = readInt();
        if (choice < 1 || choice > orders.size()) {
            System.out.println("Opțiune invalidă.\n");
            return;
        }
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

    // 8. Recenzii restaurant
    private void browseRestaurantReviews() {
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
        Restaurant chosen = list.get(choice - 1);
        List<Review> reviews = chosen.getReviews();
        System.out.println("\n===== RECENZII: " + chosen.getName() + " =====");
        if (reviews.isEmpty()) {
            System.out.println("Nicio recenzie disponibilă.");
        } else {
            for (Review r : reviews)
                System.out.println("  " + r);
        }
        System.out.println("==========================================\n");
    }

    // 9. Restaurante după nume
    private void findRestaurantsByName() {
        System.out.println("\n===== RESTAURANTE (ordine alfabetică) =====");
        int index = 1;
        for (Restaurant restaurant : restaurantService.getRestaurantsSortedByName())
            System.out.println(index++ + ". " + restaurant);
        System.out.println("==========================================\n");
    }

    // Simulare progres comanda
    private boolean simulateOrderProgression(String orderId, Order order) {
        // Restaurantul confirma sau anuleaza comanda (PENDING -> PREPARING / CANCELLED)
        System.out.print("Apasă Enter când restaurantul confirmă comanda, sau 'c' pentru ca restaurantul să anuleze: ");
        if (scanner.nextLine().trim().equalsIgnoreCase("c")) {
            orderService.restaurantCancelOrder(orderId);
            System.out.println("Comanda " + orderId + " a fost anulată de restaurant.\n");
            return false;
        }
        orderService.confirmOrder(orderId);
        System.out.println("Comanda a fost confirmată de restaurant. Stare: " + order.getStatus().getLabel() + ". [" + order.getStatusChangeTime() + "]");

        // Restaurantul marcheaza comanda gata (PREPARING -> READY_FOR_PICKUP)
        System.out.print("Apasă Enter când restaurantul marchează comanda gata: ");
        scanner.nextLine();
        orderService.markOrderReady(orderId);
        String driverName = order.getDriver() != null ? order.getDriver().getName() : "necunoscut";
        System.out.println("Comanda e gata de ridicare (" + order.getStatus().getLabel() + "). Curier asignat: " + driverName + ". [" + order.getStatusChangeTime() + "]");

        // Curierul ridica comanda (READY_FOR_PICKUP -> OUT_FOR_DELIVERY)
        System.out.print("Apasă Enter când curierul ridică comanda: ");
        scanner.nextLine();
        orderService.pickupOrder(orderId);
        System.out.println("Stare: " + order.getStatus().getLabel() + ". [" + order.getStatusChangeTime() + "]");

        // Comanda este livrata (OUT_FOR_DELIVERY -> DELIVERED)
        System.out.print("Apasă Enter când comanda este livrată: ");
        scanner.nextLine();
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