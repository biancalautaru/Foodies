package interfaces;

import models.Address;
import models.Customer;
import models.Order;

import java.util.List;

public interface IOrderService {
    Order placeOrder(Customer customer, Address address);
    void confirmOrder(String orderId);
    void restaurantCancelOrder(String orderId);
    Order reorder(Customer customer, String originalOrderId, Address deliveryAddress);
    void markOrderReady(String orderId);
    void pickupOrder(String orderId);
    void deliverOrder(String orderId);
    void submitReview(String orderId, int rating, String comment);
    List<Order> getOrdersByCustomer(String customerId);
    Order getOrderById(String orderId);
}