package service;

import exceptions.*;
import models.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderService {
    private Map<String, Order> orders;
    private List<Review> reviews;
    private int nextOrderId;
    private UserService userService;
    private RestaurantService restaurantService;

    public OrderService(UserService userService, RestaurantService restaurantService) {
        this.orders = new LinkedHashMap<>();
        this.reviews = new ArrayList<>();
        this.nextOrderId = 1;
        this.userService = userService;
        this.restaurantService = restaurantService;
    }

    public void placeOrder(Customer customer, Address address) {
        Cart cart = customer.getCart();
        if (cart.isEmpty())
            throw new EmptyCartException();

        Restaurant restaurant = cart.getRestaurant();
        String restaurantCity = restaurant.getAddress().city();
        String deliveryCity = address.city();

        if (!restaurantCity.equalsIgnoreCase(deliveryCity))
            throw new DeliveryAddressMismatchException(restaurantCity, deliveryCity);

        Cart snapshot = cart.clone();
        cart.clearCart();

        String orderId = "#" + nextOrderId++;
        Order order = new Order(orderId, customer, restaurant, address);

        for (MenuItem item : snapshot.getItems())
            order.addItem(item);

        orders.put(orderId, order);
        System.out.println("\nComanda " + order.getId() + " a fost plasată cu succes.");
        order.printOrderSummary();
    }

    public void reorder(Customer customer, String originalOrderId, Address deliveryAddress) {
        Order original = findOrderById(originalOrderId);

        if (!original.getCustomer().getId().equals(customer.getId()))
            throw new OrderAccessDeniedException(
                    "Comanda " + originalOrderId + " nu aparține clientului " + customer.getName() + ".");

        if (original.getStatus() != OrderStatus.DELIVERED)
            throw new InvalidOrderStateException("Poți re-comanda doar o comandă livrată. Comanda " + originalOrderId + " este " + original.getStatus() + ".");

        String restaurantCity = original.getRestaurant().getAddress().city();
        String deliveryCity = deliveryAddress.city();
        if (!restaurantCity.equalsIgnoreCase(deliveryCity))
            throw new DeliveryAddressMismatchException(restaurantCity, deliveryCity);

        String newOrderId = "#" + nextOrderId++;
        Order newOrder = original.toNewOrder(newOrderId, deliveryAddress);
        orders.put(newOrderId, newOrder);

        System.out.println("\nComanda " + newOrder.getId() + " a fost creată pe baza comenzii " + originalOrderId + ".");
        newOrder.printOrderSummary();
    }

    public void acceptOrder(String orderId) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.PENDING)
            throw new InvalidOrderStateException("Doar comenzile în așteptare pot fi acceptate. Comanda " + orderId + " este " + order.getStatus() + ".");

        if (!order.updateStatus(OrderStatus.ACCEPTED))
            throw new InvalidOrderStateException("Nu se poate accepta comanda " + orderId + ".");

        System.out.println("Comanda " + order.getId() + " a fost acceptată de restaurantul " + order.getRestaurant().getName() + ".");
        assignDriversToPendingOrders();
    }

    public void startOrderPreparation(String orderId) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.ACCEPTED && order.getStatus() != OrderStatus.DRIVER_ASSIGNED)
            throw new InvalidOrderStateException("Doar comenzile acceptate sau cu curier asignat pot începe prepararea. Comanda " + orderId + " este " + order.getStatus() + ".");

        if (!order.updateStatus(OrderStatus.PREPARING))
            throw new InvalidOrderStateException("Nu se poate începe prepararea pentru comanda " + orderId + ".");

        System.out.println("Comanda " + order.getId() + " este acum în preparare.");
    }

    public void markOrderReady(String orderId) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.PREPARING)
            throw new InvalidOrderStateException("Doar comenzile în preparare pot fi marcate ca gata. Comanda " + orderId + " este " + order.getStatus() + ".");

        if (!order.updateStatus(OrderStatus.READY_FOR_PICKUP))
            throw new InvalidOrderStateException("Nu se poate marca comanda " + orderId + " ca gata.");

        System.out.println("Comanda " + order.getId() + " este gata pentru ridicare.");
    }

    public void pickupOrder(String orderId) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.READY_FOR_PICKUP)
            throw new InvalidOrderStateException("Doar comenzile gata pot fi ridicate. Comanda " + orderId + " este " + order.getStatus() + ".");

        if (order.getDriver() == null)
            throw new InvalidOrderStateException("Comanda " + orderId + " nu are un curier asignat.");

        if (!order.updateStatus(OrderStatus.OUT_FOR_DELIVERY))
            throw new InvalidOrderStateException("Nu se poate ridica comanda " + orderId + ".");

        System.out.println("Comanda " + order.getId() + " este acum în livrare cu curierul " + order.getDriver().getName() + ".");
    }

    public void deliverOrder(String orderId) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.OUT_FOR_DELIVERY)
            throw new InvalidOrderStateException("Doar comenzile aflate în livrare pot fi livrate. Comanda " + orderId + " este " + order.getStatus() + ".");

        if (!order.updateStatus(OrderStatus.DELIVERED))
            throw new InvalidOrderStateException("Nu se poate livra comanda " + orderId + ".");

        Driver driver = order.getDriver();
        if (driver != null)
            driver.setAvailable(true);

        System.out.println("Comanda " + order.getId() + " a fost livrată cu succes.");
        assignDriversToPendingOrders();
    }

    public void cancelOrder(String orderId) {
        Order order = findOrderById(orderId);

        if (!order.cancelOrder())
            throw new OrderCancellationException(orderId);

        Driver driver = order.getDriver();
        if (driver != null)
            driver.setAvailable(true);

        String message = "Comanda " + order.getId() + " a fost anulată.";
        if (order.getCancellationFee() > 0)
            message += " Taxă de anulare: " + String.format("%.2f", order.getCancellationFee()) + " lei.";
        System.out.println(message);
    }

    public void submitReview(String orderId, int rating, String comment) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.DELIVERED)
            throw new InvalidReviewException("Poți lăsa recenzii doar pentru comenzile livrate. Comanda " + orderId + " este " + order.getStatus() + ".");

        if (order.getReview() != null)
            throw new InvalidReviewException("Comanda " + orderId + " are deja o recenzie.");

        if (rating < 1 || rating > 5)
            throw new InvalidReviewException("Nota trebuie să fie între 1 și 5. Valoare primită: " + rating + ".");

        Review review = new Review(String.valueOf(reviews.size() + 1), order.getCustomer(), order, rating, comment);
        order.setReview(review);
        reviews.add(review);

        Restaurant restaurant = order.getRestaurant();
        int newCount = restaurant.getReviewCount() + 1;
        double newStars = (restaurant.getStars() * restaurant.getReviewCount() + rating) / newCount;
        restaurant.setReviewCount(newCount);
        restaurant.setStars(newStars);

        System.out.println("Recenzie trimisă pentru comanda " + orderId + " (" + restaurant.getName() + "): " + rating + "/5 stele - " + comment);
    }

    public void getCustomerOrderHistory(String customerId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

        System.out.println("\n===== ISTORIC COMENZI =====");
        boolean foundOrders = false;
        for (Order order : orders.values())
            if (order.getCustomer().getId().equals(customerId)) {
                System.out.println("Comanda " + order.getId() + " | " + order.getDate().format(formatter) + " | " + order.getRestaurant().getName() + " | " + order.getStatus() + " | " + String.format("%.2f", order.getTotal()) + " lei");
                foundOrders = true;
            }

        if (!foundOrders)
            System.out.println("Nu s-au găsit comenzi.");
        System.out.println("=========================\n");
    }

    public void displayOrderDetails(String orderId) {
        Order order = findOrderById(orderId);
        order.printOrderSummary();

        if (order.getReview() == null)
            System.out.println("Nu există încă recenzie.");
        else {
            System.out.println("Notă: " + order.getReview().rating() + "/5 stele");
            System.out.println("Comentariu: " + order.getReview().comment());
        }
    }

    public void displayRestaurantReviews(String restaurantId) {
        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);

        System.out.println("\n===== RECENZII PENTRU " + restaurant.getName() + " =====");
        int count = 0;
        double totalRating = 0;

        for (Review review : reviews)
            if (review.order().getRestaurant().getId().equals(restaurantId)) {
                System.out.println(review.customer().getName() + ": " + review.rating() + "/5 stele - " + review.comment());
                totalRating += review.rating();
                count++;
            }

        if (count > 0)
            System.out.println("Notă medie: " + String.format("%.1f", totalRating / count) + "/5");
        else
            System.out.println("Nu există încă recenzii.");

        System.out.println("==================================\n");
    }

    private void assignDriversToPendingOrders() {
        for (Order order : orders.values()) {
            if (order.getStatus() == OrderStatus.ACCEPTED && order.getDriver() == null) {
                Driver driver = userService.findAvailableDriver();
                if (driver == null)
                    break;

                order.setDriver(driver);
                driver.setAvailable(false);
                order.updateStatus(OrderStatus.DRIVER_ASSIGNED);

                System.out.println("Curierul " + driver.getName() + " a fost asignat automat comenzii " + order.getId() + ".");
            }
        }
    }

    private Order findOrderById(String id) {
        Order order = orders.get(id);
        if (order == null)
            throw new OrderNotFoundException(id);
        return order;
    }
}
