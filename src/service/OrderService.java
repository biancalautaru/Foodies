package service;

import exceptions.EntityNotFoundException;
import exceptions.InvalidOrderException;
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
            throw new InvalidOrderException("Coșul este gol.");

        Restaurant restaurant = cart.getRestaurant();
        String restaurantCity = restaurant.getAddress().city();
        String deliveryCity = address.city();

        if (!restaurantCity.equalsIgnoreCase(deliveryCity))
            throw new InvalidOrderException("Orașul adresei de livrare '" + deliveryCity + "' nu corespunde cu orașul restaurantului '" + restaurantCity + "'.");

        List<MenuItem> snapshot = new ArrayList<>(cart.getItems());
        cart.clearCart();

        String orderId = "#" + nextOrderId++;
        Order order = new Order(orderId, customer, restaurant, address);

        for (MenuItem item : snapshot)
            order.addItem(item);

        orders.put(orderId, order);
        AuditService.getInstance().log("placeOrder");
    }

    public void confirmOrder(String orderId) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.PENDING)
            throw new InvalidOrderException("Doar comenzile în așteptare pot fi confirmate. Comanda " + orderId + " este " + order.getStatus() + ".");

        if (!order.updateStatus(OrderStatus.PREPARING))
            throw new InvalidOrderException("Nu se poate confirma comanda " + orderId + ".");

        AuditService.getInstance().log("confirmOrder");
    }

    public void restaurantCancelOrder(String orderId) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.PENDING)
            throw new InvalidOrderException("Restaurantul poate anula doar comenzile în așteptare. Comanda " + orderId + " este " + order.getStatus() + ".");

        order.cancelOrder();
        AuditService.getInstance().log("restaurantCancelOrder");
        System.out.println("Comanda " + orderId + " a fost anulată de restaurant.");
    }

    public void reorder(Customer customer, String originalOrderId, Address deliveryAddress) {
        Order original = findOrderById(originalOrderId);

        if (!original.getCustomer().getId().equals(customer.getId()))
            throw new InvalidOrderException("Comanda " + originalOrderId + " nu aparține clientului " + customer.getName() + ".");

        if (original.getStatus() != OrderStatus.DELIVERED)
            throw new InvalidOrderException("Poți re-comanda doar o comandă livrată. Comanda " + originalOrderId + " este " + original.getStatus() + ".");

        String restaurantCity = original.getRestaurant().getAddress().city();
        String deliveryCity = deliveryAddress.city();
        if (!restaurantCity.equalsIgnoreCase(deliveryCity))
            throw new InvalidOrderException("Orașul adresei de livrare '" + deliveryCity + "' nu corespunde cu orașul restaurantului '" + restaurantCity + "'.");

        String newOrderId = "#" + nextOrderId++;
        Order newOrder = original.toNewOrder(newOrderId, deliveryAddress);
        orders.put(newOrderId, newOrder);

        AuditService.getInstance().log("reorder");
    }

    public void markOrderReady(String orderId) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.PREPARING)
            throw new InvalidOrderException("Doar comenzile în preparare pot fi marcate ca gata. Comanda " + orderId + " este " + order.getStatus() + ".");

        if (!order.updateStatus(OrderStatus.READY_FOR_PICKUP))
            throw new InvalidOrderException("Nu se poate marca comanda " + orderId + " ca gata.");

        AuditService.getInstance().log("markOrderReady");
        assignDriversToReadyOrders();
    }

    public void pickupOrder(String orderId) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.READY_FOR_PICKUP)
            throw new InvalidOrderException("Doar comenzile gata pot fi ridicate. Comanda " + orderId + " este " + order.getStatus() + ".");

        if (order.getDriver() == null)
            throw new InvalidOrderException("Comanda " + orderId + " nu are un curier asignat.");

        if (!order.updateStatus(OrderStatus.OUT_FOR_DELIVERY))
            throw new InvalidOrderException("Nu se poate ridica comanda " + orderId + ".");
    }

    public void deliverOrder(String orderId) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.OUT_FOR_DELIVERY)
            throw new InvalidOrderException("Doar comenzile aflate în livrare pot fi livrate. Comanda " + orderId + " este " + order.getStatus() + ".");

        if (!order.updateStatus(OrderStatus.DELIVERED))
            throw new InvalidOrderException("Nu se poate livra comanda " + orderId + ".");

        Driver driver = order.getDriver();
        if (driver != null)
            driver.setAvailable(true);

        AuditService.getInstance().log("deliverOrder");
        assignDriversToReadyOrders();
    }

    public void cancelOrder(String orderId) {
        Order order = findOrderById(orderId);
        OrderStatus statusBefore = order.getStatus();

        if (!order.cancelOrder()) {
            if (statusBefore == OrderStatus.DELIVERED)
                throw new InvalidOrderException("Comanda " + orderId + " a fost deja livrată și nu poate fi anulată.");
            throw new InvalidOrderException("Comanda " + orderId + " este deja anulată.");
        }

        Driver driver = order.getDriver();
        if (driver != null)
            driver.setAvailable(true);

        AuditService.getInstance().log("cancelOrder");
        String feeInfo = order.getCancellationFee() > 0 ? " Taxă de anulare: " + String.format("%.2f", order.getCancellationFee()) + " lei." : "";
        System.out.println("Comanda " + order.getId() + " a fost anulată." + feeInfo);
    }

    public void submitReview(String orderId, int rating, String comment) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.DELIVERED)
            throw new InvalidOrderException("Poți lăsa recenzii doar pentru comenzile livrate. Comanda " + orderId + " este " + order.getStatus() + ".");

        if (order.getReview() != null)
            throw new InvalidOrderException("Comanda " + orderId + " are deja o recenzie.");

        if (rating < 1 || rating > 5)
            throw new InvalidOrderException("Nota trebuie să fie între 1 și 5. Valoare primită: " + rating + ".");

        Review review = new Review(String.valueOf(reviews.size() + 1), order.getCustomer(), order, rating, comment);
        order.setReview(review);
        reviews.add(review);

        AuditService.getInstance().log("submitReview");
        Restaurant restaurant = order.getRestaurant();
        int newCount = restaurant.getReviewCount() + 1;
        double newStars = (restaurant.getStars() * restaurant.getReviewCount() + rating) / newCount;
        restaurant.setReviewCount(newCount);
        restaurant.setStars(newStars);
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
        System.out.print(order.toSummaryString());

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

    private void assignDriversToReadyOrders() {
        for (Order order : orders.values()) {
            if (order.getStatus() == OrderStatus.READY_FOR_PICKUP && order.getDriver() == null) {
                Driver driver = userService.findAvailableDriver();
                if (driver == null)
                    break;
                order.setDriver(driver);
                driver.setAvailable(false);
            }
        }
    }

    public List<Order> getOrdersByCustomer(String customerId) {
        List<Order> result = new ArrayList<>();
        for (Order order : orders.values())
            if (order.getCustomer().getId().equals(customerId))
                result.add(order);
        return result;
    }

    private Order findOrderById(String id) {
        Order order = orders.get(id);
        if (order == null)
            throw new EntityNotFoundException("Comanda " + id + " nu a fost găsită.");
        return order;
    }
}
