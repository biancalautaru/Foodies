package service;

import exceptions.EntityNotFoundException;
import exceptions.InvalidOrderException;
import models.*;
import repository.DriverRepository;
import repository.OrderRepository;
import repository.RestaurantRepository;
import repository.ReviewRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderService {
    private final OrderRepository orderRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReviewRepository reviewRepository;
    private final DriverRepository driverRepository;
    private final UserService userService;

    public OrderService(UserService userService) {
        this.orderRepository = OrderRepository.getInstance();
        this.restaurantRepository = RestaurantRepository.getInstance();
        this.reviewRepository = ReviewRepository.getInstance();
        this.driverRepository = DriverRepository.getInstance();
        this.userService = userService;
    }

    public Order placeOrder(Customer customer, Address address) {
        Cart cart = customer.getCart();
        if (cart.isEmpty())
            throw new InvalidOrderException("Coșul este gol.");

        Restaurant restaurant = cart.getRestaurant();
        validateSameCity(restaurant, address);

        List<MenuItem> snapshot = new ArrayList<>(cart.getItems());

        if (address.getId() == null)
            address.setId(UUID.randomUUID().toString());

        String orderId = UUID.randomUUID().toString();
        Order order = new Order(orderId, customer, restaurant, address);
        for (MenuItem item : snapshot)
            order.addItem(item);

        orderRepository.create(order);
        cart.clearCart();
        AuditService.getInstance().log("placeOrder");
        return order;
    }

    public void confirmOrder(String orderId) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.PENDING)
            throw new InvalidOrderException("Doar comenzile în așteptare pot fi confirmate. Comanda " + order.getDisplayId() + " este " + order.getStatus().getLabel() + ".");

        if (!order.updateStatus(OrderStatus.PREPARING))
            throw new InvalidOrderException("Nu se poate confirma comanda " + order.getDisplayId() + ".");

        orderRepository.update(order);
        AuditService.getInstance().log("confirmOrder");
    }

    public void restaurantCancelOrder(String orderId) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.PENDING)
            throw new InvalidOrderException("Restaurantul poate anula doar comenzile în așteptare. Comanda " + order.getDisplayId() + " este " + order.getStatus().getLabel() + ".");

        if (!order.cancelPending())
            throw new InvalidOrderException("Nu se poate anula comanda " + order.getDisplayId() + ".");

        orderRepository.update(order);
        AuditService.getInstance().log("restaurantCancelOrder");
    }

    public Order reorder(Customer customer, String originalOrderId, Address deliveryAddress) {
        Order original = findOrderById(originalOrderId);

        if (!original.getCustomer().getId().equals(customer.getId()))
            throw new InvalidOrderException("Comanda " + original.getDisplayId() + " nu aparține clientului " + customer.getName() + ".");

        if (original.getStatus() != OrderStatus.DELIVERED)
            throw new InvalidOrderException("Poți re-comanda doar o comandă livrată. Comanda " + original.getDisplayId() + " este " + original.getStatus().getLabel() + ".");

        Restaurant restaurant = restaurantRepository.read(original.getRestaurant().getId());
        validateSameCity(restaurant, deliveryAddress);

        if (deliveryAddress.getId() == null)
            deliveryAddress.setId(UUID.randomUUID().toString());

        List<MenuItem> originalItems = orderRepository.readItemsForOrder(originalOrderId);
        if (originalItems.isEmpty())
            throw new InvalidOrderException("Comanda " + original.getDisplayId() + " nu are produse disponibile pentru re-comandare.");

        String newOrderId = UUID.randomUUID().toString();
        Order newOrder = new Order(newOrderId, customer, restaurant, deliveryAddress);
        for (MenuItem item : originalItems)
            newOrder.addItem(item);

        orderRepository.create(newOrder);

        AuditService.getInstance().log("reorder");
        return newOrder;
    }

    public void markOrderReady(String orderId) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.PREPARING)
            throw new InvalidOrderException("Doar comenzile în preparare pot fi marcate ca gata. Comanda " + order.getDisplayId() + " este " + order.getStatus().getLabel() + ".");

        if (!order.updateStatus(OrderStatus.READY_FOR_PICKUP))
            throw new InvalidOrderException("Nu se poate marca comanda " + order.getDisplayId() + " ca gata.");

        orderRepository.update(order);
        AuditService.getInstance().log("markOrderReady");
        assignDriversToReadyOrders();
    }

    public void pickupOrder(String orderId) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.READY_FOR_PICKUP)
            throw new InvalidOrderException("Doar comenzile gata pot fi ridicate. Comanda " + order.getDisplayId() + " este " + order.getStatus().getLabel() + ".");

        if (order.getDriver() == null)
            throw new InvalidOrderException("Comanda " + order.getDisplayId() + " nu are un curier asignat.");

        if (!order.updateStatus(OrderStatus.OUT_FOR_DELIVERY))
            throw new InvalidOrderException("Nu se poate ridica comanda " + order.getDisplayId() + ".");

        orderRepository.update(order);
        AuditService.getInstance().log("pickupOrder");
    }

    public void deliverOrder(String orderId) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.OUT_FOR_DELIVERY)
            throw new InvalidOrderException("Doar comenzile aflate în livrare pot fi livrate. Comanda " + order.getDisplayId() + " este " + order.getStatus().getLabel() + ".");

        if (!order.updateStatus(OrderStatus.DELIVERED))
            throw new InvalidOrderException("Nu se poate livra comanda " + order.getDisplayId() + ".");

        orderRepository.update(order);

        if (order.getDriver() != null)
            driverRepository.updateAvailability(order.getDriver().getId(), true);

        AuditService.getInstance().log("deliverOrder");
        assignDriversToReadyOrders();
    }

    public void submitReview(String orderId, int rating, String comment) {
        Order order = findOrderById(orderId);

        if (order.getStatus() != OrderStatus.DELIVERED)
            throw new InvalidOrderException("Poți lăsa recenzii doar pentru comenzile livrate. Comanda " + order.getDisplayId() + " este " + order.getStatus().getLabel() + ".");

        if (reviewRepository.readByOrder(orderId) != null)
            throw new InvalidOrderException("Comanda " + order.getDisplayId() + " are deja o recenzie.");

        if (rating < 1 || rating > 5)
            throw new InvalidOrderException("Nota trebuie să fie între 1 și 5. Valoare primită: " + rating + ".");

        Review review = new Review(UUID.randomUUID().toString(), order.getCustomer(), orderId, rating, comment);
        reviewRepository.create(review);
        AuditService.getInstance().log("submitReview");
    }

    public void deleteOrder(Customer customer, String orderId) {
        Order order = findOrderById(orderId);

        if (!order.getCustomer().getId().equals(customer.getId()))
            throw new InvalidOrderException("Comanda " + order.getDisplayId() + " nu aparține clientului " + customer.getName() + ".");

        if (order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.CANCELLED)
            throw new InvalidOrderException("Poți șterge doar comenzile livrate sau anulate. Comanda " + order.getDisplayId() + " este " + order.getStatus().getLabel() + ".");

        Review review = reviewRepository.readByOrder(orderId);
        if (review != null)
            reviewRepository.delete(review.getId());

        orderRepository.delete(orderId);
        AuditService.getInstance().log("deleteOrder");
    }

    public List<Order> getOrdersByCustomer(String customerId) {
        List<Order> orders = orderRepository.readByCustomer(customerId);
        for (Order order : orders)
            hydrate(order);
        return orders;
    }

    public Order getOrderById(String orderId) {
        Order order = orderRepository.read(orderId);
        if (order == null)
            throw new EntityNotFoundException("Comanda " + orderId + " nu a fost găsită.");
        hydrate(order);
        return order;
    }

    private void hydrate(Order order) {
        Restaurant restaurant = restaurantRepository.read(order.getRestaurant().getId());
        if (restaurant != null)
            order.setRestaurant(restaurant);

        order.setItems(orderRepository.readItemsForOrder(order.getId()));

        if (order.getDriver() != null) {
            Driver fullDriver = driverRepository.read(order.getDriver().getId());
            if (fullDriver != null)
                order.setDriver(fullDriver);
        }

        Review review = reviewRepository.readByOrder(order.getId());
        if (review != null)
            order.setReview(review);
    }

    private void validateSameCity(Restaurant restaurant, Address deliveryAddress) {
        String restaurantCity = restaurant.getAddress().getCity();
        String deliveryCity = deliveryAddress.getCity();
        if (!restaurantCity.equalsIgnoreCase(deliveryCity))
            throw new InvalidOrderException("Orașul adresei de livrare '" + deliveryCity + "' nu corespunde cu orașul restaurantului '" + restaurantCity + "'.");
    }

    private void assignDriversToReadyOrders() {
        for (Order order : orderRepository.readReadyWithoutDriver()) {
            Driver driver = userService.findAvailableDriver();
            if (driver == null)
                break;
            order.setDriver(driver);
            orderRepository.update(order);
            driverRepository.updateAvailability(driver.getId(), false);
        }
    }

    private Order findOrderById(String id) {
        Order order = orderRepository.read(id);
        if (order == null)
            throw new EntityNotFoundException("Comanda " + id + " nu a fost găsită.");
        return order;
    }
}