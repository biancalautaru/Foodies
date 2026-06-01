package repository;

import models.Customer;
import models.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class ReviewRepository extends AbstractRepository<Review, String> {
    private static final ReviewRepository INSTANCE = new ReviewRepository();

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

    private ReviewRepository() {}

    public static ReviewRepository getInstance() {
        return INSTANCE;
    }

    @Override
    protected String repositoryName() {
        return "ReviewRepository";
    }

    @Override
    public void create(Review review) {
        executeWrite(SQL_INSERT, review.getId(), review.getCustomer().getId(), review.getOrderId(),
                review.getRating(), review.getComment(), Timestamp.valueOf(review.getDate()));
    }

    @Override
    public Review read(String id) {
        return queryOne(SQL_SELECT_BY_ID, this::mapRow, id);
    }

    @Override
    public List<Review> readAll() {
        return queryList(SQL_SELECT_ALL, this::mapRow);
    }

    public Review readByOrder(String orderId) {
        return queryOne(SQL_SELECT_BY_ORDER, this::mapRow, orderId);
    }

    public List<Review> readByRestaurant(String restaurantId) {
        return queryList(SQL_SELECT_BY_RESTAURANT, this::mapRow, restaurantId);
    }

    @Override
    public void update(Review review) {
        executeWrite(SQL_UPDATE, review.getRating(), review.getComment(), review.getId());
    }

    @Override
    public void delete(String id) {
        executeWrite(SQL_DELETE, id);
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