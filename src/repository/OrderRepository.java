package repository;

import config.DatabaseConfiguration;
import models.Customer;
import models.Driver;
import models.MenuItem;
import models.Order;
import models.OrderStatus;
import models.Restaurant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository implements GenericRepository<Order, String> {

    private static final String SQL_INSERT_ADDRESS =
            "INSERT INTO addresses (id, street, number, city) VALUES (?, ?, ?, ?)" +
            " ON CONFLICT (id) DO NOTHING";

    private static final String SQL_INSERT =
            "INSERT INTO orders (id, date, customer_id, restaurant_id, delivery_address_id," +
            " driver_id, status, status_change_time)" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING number";

    private static final String SQL_INSERT_ORDER_ITEM =
            "INSERT INTO order_items (order_id, menu_item_id) VALUES (?, ?)";

    private static final String SQL_SELECT_BY_ID =
            "SELECT id, number, date, status, status_change_time," +
            " customer_id, restaurant_id, delivery_address_id, driver_id" +
            " FROM orders WHERE id = ?";

    private static final String SQL_SELECT_ALL =
            "SELECT id, number, date, status, status_change_time," +
            " customer_id, restaurant_id, delivery_address_id, driver_id" +
            " FROM orders";

    private static final String SQL_SELECT_BY_CUSTOMER =
            "SELECT id, number, date, status, status_change_time," +
            " customer_id, restaurant_id, delivery_address_id, driver_id" +
            " FROM orders WHERE customer_id = ? ORDER BY date DESC";

    private static final String SQL_SELECT_READY_WITHOUT_DRIVER =
            "SELECT id, number, date, status, status_change_time," +
            " customer_id, restaurant_id, delivery_address_id, driver_id" +
            " FROM orders WHERE status = 'READY_FOR_PICKUP' AND driver_id IS NULL";

    private static final String SQL_SELECT_ITEMS_BY_ORDER =
            "SELECT mi.id, mi.name, mi.description, mi.price, mi.restaurant_id" +
            " FROM order_items oi JOIN menu_items mi ON oi.menu_item_id = mi.id" +
            " WHERE oi.order_id = ?";

    private static final String SQL_UPDATE =
            "UPDATE orders SET status = ?, status_change_time = ?, driver_id = ? WHERE id = ?";

    private static final String SQL_DELETE_ORDER_ITEMS =
            "DELETE FROM order_items WHERE order_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM orders WHERE id = ?";

    private final Connection connection;

    public OrderRepository() {
        this.connection = DatabaseConfiguration.getInstance().getConnection();
    }

    @Override
    public void create(Order order) {
        try {
            try (PreparedStatement stmt = connection.prepareStatement(SQL_INSERT_ADDRESS)) {
                stmt.setString(1, order.getDeliveryAddress().getId());
                stmt.setString(2, order.getDeliveryAddress().getStreet());
                stmt.setString(3, order.getDeliveryAddress().getNumber());
                stmt.setString(4, order.getDeliveryAddress().getCity());
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = connection.prepareStatement(SQL_INSERT)) {
                stmt.setString(1, order.getId());
                stmt.setTimestamp(2, Timestamp.valueOf(order.getDate()));
                stmt.setString(3, order.getCustomer().getId());
                stmt.setString(4, order.getRestaurant().getId());
                stmt.setString(5, order.getDeliveryAddress().getId());
                stmt.setString(6, order.getDriver() != null ? order.getDriver().getId() : null);
                stmt.setString(7, order.getStatus().name());
                stmt.setTimestamp(8, Timestamp.valueOf(order.getStatusChangeDateTime()));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next())
                        order.setNumber(rs.getInt("number"));
                }
            }
            try (PreparedStatement stmt = connection.prepareStatement(SQL_INSERT_ORDER_ITEM)) {
                for (MenuItem item : order.getItems()) {
                    stmt.setString(1, order.getId());
                    stmt.setString(2, item.getId());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        } catch (SQLException e) {
            throw new RuntimeException("OrderRepository: create failed for id=" + order.getId(), e);
        }
    }

    @Override
    public Order read(String id) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("OrderRepository: read failed for id=" + id, e);
        }
        return null;
    }

    @Override
    public List<Order> readAll() {
        List<Order> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("OrderRepository: readAll failed", e);
        }
        return result;
    }

    public List<Order> readByCustomer(String customerId) {
        List<Order> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_BY_CUSTOMER)) {
            stmt.setString(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "OrderRepository: readByCustomer failed for customerId=" + customerId, e);
        }
        return result;
    }

    public List<Order> readReadyWithoutDriver() {
        List<Order> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_READY_WITHOUT_DRIVER);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("OrderRepository: readReadyWithoutDriver failed", e);
        }
        return result;
    }

    public List<MenuItem> readItemsForOrder(String orderId) {
        List<MenuItem> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_ITEMS_BY_ORDER)) {
            stmt.setString(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MenuItem item = new MenuItem();
                    item.setId(rs.getString("id"));
                    item.setName(rs.getString("name"));
                    item.setDescription(rs.getString("description"));
                    item.setPrice(rs.getDouble("price"));

                    String restaurantId = rs.getString("restaurant_id");
                    if (restaurantId != null) {
                        Restaurant restaurantStub = new Restaurant();
                        restaurantStub.setId(restaurantId);
                        item.setRestaurant(restaurantStub);
                    }

                    result.add(item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "OrderRepository: readItemsForOrder failed for orderId=" + orderId, e);
        }
        return result;
    }

    @Override
    public void update(Order order) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, order.getStatus().name());
            stmt.setTimestamp(2, Timestamp.valueOf(order.getStatusChangeDateTime()));
            stmt.setString(3, order.getDriver() != null ? order.getDriver().getId() : null);
            stmt.setString(4, order.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("OrderRepository: update failed for id=" + order.getId(), e);
        }
    }

    @Override
    public void delete(String id) {
        try {
            try (PreparedStatement stmt = connection.prepareStatement(SQL_DELETE_ORDER_ITEMS)) {
                stmt.setString(1, id);
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = connection.prepareStatement(SQL_DELETE)) {
                stmt.setString(1, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("OrderRepository: delete failed for id=" + id, e);
        }
    }

    private Order mapRow(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getString("id"));
        order.setNumber(rs.getInt("number"));
        order.setDate(rs.getTimestamp("date").toLocalDateTime());
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        order.setStatusChangeTime(rs.getTimestamp("status_change_time").toLocalDateTime());

        Customer customerStub = new Customer();
        customerStub.setId(rs.getString("customer_id"));
        order.setCustomer(customerStub);

        Restaurant restaurantStub = new Restaurant();
        restaurantStub.setId(rs.getString("restaurant_id"));
        order.setRestaurant(restaurantStub);

        String driverId = rs.getString("driver_id");
        if (driverId != null) {
            Driver driverStub = new Driver();
            driverStub.setId(driverId);
            order.setDriver(driverStub);
        }

        return order;
    }
}