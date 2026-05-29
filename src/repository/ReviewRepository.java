package repository;

import config.DatabaseConfiguration;
import models.Customer;
import models.Review;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ReviewRepository implements GenericRepository<Review, String> {
    private static final String SQL_INSERT =
            "INSERT INTO reviews (id, customer_id, order_id, rating, comment, date)" +
            " VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_BY_ID =
            "SELECT rv.id, rv.customer_id, rv.order_id, rv.rating, rv.comment, rv.date," +
            " c.name AS customer_name, c.email AS customer_email" +
            " FROM reviews rv JOIN customers c ON rv.customer_id = c.id" +
            " WHERE rv.id = ?";

    private static final String SQL_SELECT_ALL =
            "SELECT rv.id, rv.customer_id, rv.order_id, rv.rating, rv.comment, rv.date," +
            " c.name AS customer_name, c.email AS customer_email" +
            " FROM reviews rv JOIN customers c ON rv.customer_id = c.id";

    private static final String SQL_SELECT_BY_ORDER =
            "SELECT rv.id, rv.customer_id, rv.order_id, rv.rating, rv.comment, rv.date," +
            " c.name AS customer_name, c.email AS customer_email" +
            " FROM reviews rv JOIN customers c ON rv.customer_id = c.id" +
            " WHERE rv.order_id = ?";

    private static final String SQL_SELECT_BY_RESTAURANT =
            "SELECT rv.id, rv.customer_id, rv.order_id, rv.rating, rv.comment, rv.date," +
            " c.name AS customer_name, c.email AS customer_email" +
            " FROM reviews rv JOIN orders o ON rv.order_id = o.id" +
            " JOIN customers c ON rv.customer_id = c.id" +
            " WHERE o.restaurant_id = ?";

    private static final String SQL_UPDATE =
            "UPDATE reviews SET rating = ?, comment = ? WHERE id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM reviews WHERE id = ?";

    private final Connection connection;

    public ReviewRepository() {
        this.connection = DatabaseConfiguration.getInstance().getConnection();
    }

    @Override
    public void create(Review review) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_INSERT)) {
            stmt.setString(1, review.getId());
            stmt.setString(2, review.getCustomer().getId());
            stmt.setString(3, review.getOrderId());
            stmt.setInt(4, review.getRating());
            stmt.setString(5, review.getComment());
            stmt.setTimestamp(6, Timestamp.valueOf(review.getDate()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ReviewRepository: create failed for id=" + review.getId(), e);
        }
    }

    @Override
    public Review read(String id) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("ReviewRepository: read failed for id=" + id, e);
        }
        return null;
    }

    @Override
    public List<Review> readAll() {
        List<Review> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("ReviewRepository: readAll failed", e);
        }
        return result;
    }

    public Review readByOrder(String orderId) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_BY_ORDER)) {
            stmt.setString(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "ReviewRepository: readByOrder failed for orderId=" + orderId, e);
        }
        return null;
    }

    public List<Review> readByRestaurant(String restaurantId) {
        List<Review> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_BY_RESTAURANT)) {
            stmt.setString(1, restaurantId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "ReviewRepository: readByRestaurant failed for restaurantId=" + restaurantId, e);
        }
        return result;
    }

    @Override
    public void update(Review review) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_UPDATE)) {
            stmt.setInt(1, review.getRating());
            stmt.setString(2, review.getComment());
            stmt.setString(3, review.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ReviewRepository: update failed for id=" + review.getId(), e);
        }
    }

    @Override
    public void delete(String id) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_DELETE)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("ReviewRepository: delete failed for id=" + id, e);
        }
    }

    private Review mapRow(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getString("customer_id"));
        customer.setName(rs.getString("customer_name"));
        customer.setEmail(rs.getString("customer_email"));

        Review review = new Review();
        review.setId(rs.getString("id"));
        review.setCustomer(customer);
        review.setOrderId(rs.getString("order_id"));
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));
        review.setDate(rs.getTimestamp("date").toLocalDateTime());
        return review;
    }
}