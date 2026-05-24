package repository;

import config.DatabaseConfiguration;
import models.Address;
import models.Restaurant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RestaurantRepository implements GenericRepository<Restaurant, String> {
    private static final String SQL_INSERT_ADDRESS =
            "INSERT INTO addresses (id, street, number, city) VALUES (?, ?, ?, ?)" +
            " ON CONFLICT (id) DO NOTHING";

    private static final String SQL_INSERT =
            "INSERT INTO restaurants (id, name, address_id) VALUES (?, ?, ?)";

    private static final String SQL_SELECT_BY_ID =
            "SELECT r.id, r.name, a.id AS address_id, a.street, a.number, a.city" +
            " FROM restaurants r JOIN addresses a ON r.address_id = a.id" +
            " WHERE r.id = ?";

    private static final String SQL_SELECT_ALL =
            "SELECT r.id, r.name, a.id AS address_id, a.street, a.number, a.city" +
            " FROM restaurants r JOIN addresses a ON r.address_id = a.id";

    private static final String SQL_UPDATE_ADDRESS =
            "UPDATE addresses SET street = ?, number = ?, city = ? WHERE id = ?";

    private static final String SQL_UPDATE =
            "UPDATE restaurants SET name = ?, address_id = ? WHERE id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM restaurants WHERE id = ?";

    private final Connection connection;

    public RestaurantRepository() {
        this.connection = DatabaseConfiguration.getInstance().getConnection();
    }

    @Override
    public void create(Restaurant restaurant) {
        try {
            Address addr = restaurant.getAddress();
            try (PreparedStatement stmt = connection.prepareStatement(SQL_INSERT_ADDRESS)) {
                stmt.setString(1, addr.getId());
                stmt.setString(2, addr.getStreet());
                stmt.setString(3, addr.getNumber());
                stmt.setString(4, addr.getCity());
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = connection.prepareStatement(SQL_INSERT)) {
                stmt.setString(1, restaurant.getId());
                stmt.setString(2, restaurant.getName());
                stmt.setString(3, addr.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("RestaurantRepository: create failed for id=" + restaurant.getId(), e);
        }
    }

    @Override
    public Restaurant read(String id) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("RestaurantRepository: read failed for id=" + id, e);
        }
        return null;
    }

    @Override
    public List<Restaurant> readAll() {
        List<Restaurant> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("RestaurantRepository: readAll failed", e);
        }
        return result;
    }

    @Override
    public void update(Restaurant restaurant) {
        try {
            Address addr = restaurant.getAddress();
            try (PreparedStatement stmt = connection.prepareStatement(SQL_UPDATE_ADDRESS)) {
                stmt.setString(1, addr.getStreet());
                stmt.setString(2, addr.getNumber());
                stmt.setString(3, addr.getCity());
                stmt.setString(4, addr.getId());
                stmt.executeUpdate();
            }
            try (PreparedStatement stmt = connection.prepareStatement(SQL_UPDATE)) {
                stmt.setString(1, restaurant.getName());
                stmt.setString(2, addr.getId());
                stmt.setString(3, restaurant.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("RestaurantRepository: update failed for id=" + restaurant.getId(), e);
        }
    }

    @Override
    public void delete(String id) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_DELETE)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("RestaurantRepository: delete failed for id=" + id, e);
        }
    }

    private Restaurant mapRow(ResultSet rs) throws SQLException {
        Address address = new Address(
                rs.getString("address_id"),
                rs.getString("street"),
                rs.getString("number"),
                rs.getString("city")
        );
        Restaurant restaurant = new Restaurant();
        restaurant.setId(rs.getString("id"));
        restaurant.setName(rs.getString("name"));
        restaurant.setAddress(address);
        return restaurant;
    }
}