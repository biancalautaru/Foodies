package repository;

import config.DatabaseConfiguration;
import models.MenuItem;
import models.Restaurant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuItemRepository implements GenericRepository<MenuItem, String> {
    private static final String SQL_INSERT =
            "INSERT INTO menu_items (id, name, description, price, restaurant_id) VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_BY_ID =
            "SELECT id, name, description, price, restaurant_id FROM menu_items WHERE id = ?";

    private static final String SQL_SELECT_ALL =
            "SELECT id, name, description, price, restaurant_id FROM menu_items";

    private static final String SQL_SELECT_BY_RESTAURANT =
            "SELECT id, name, description, price, restaurant_id FROM menu_items WHERE restaurant_id = ?";

    private static final String SQL_UPDATE =
            "UPDATE menu_items SET name = ?, description = ?, price = ?, restaurant_id = ? WHERE id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM menu_items WHERE id = ?";

    private final Connection connection;

    public MenuItemRepository() {
        this.connection = DatabaseConfiguration.getInstance().getConnection();
    }

    @Override
    public void create(MenuItem item) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_INSERT)) {
            stmt.setString(1, item.getId());
            stmt.setString(2, item.getName());
            stmt.setString(3, item.getDescription());
            stmt.setDouble(4, item.getPrice());
            stmt.setString(5, item.getRestaurant() != null ? item.getRestaurant().getId() : null);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("MenuItemRepository: create failed for id=" + item.getId(), e);
        }
    }

    @Override
    public MenuItem read(String id) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("MenuItemRepository: read failed for id=" + id, e);
        }
        return null;
    }

    @Override
    public List<MenuItem> readAll() {
        List<MenuItem> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("MenuItemRepository: readAll failed", e);
        }
        return result;
    }

    public List<MenuItem> readByRestaurant(String restaurantId) {
        List<MenuItem> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_BY_RESTAURANT)) {
            stmt.setString(1, restaurantId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    "MenuItemRepository: readByRestaurant failed for restaurantId=" + restaurantId, e);
        }
        return result;
    }

    @Override
    public void update(MenuItem item) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, item.getName());
            stmt.setString(2, item.getDescription());
            stmt.setDouble(3, item.getPrice());
            stmt.setString(4, item.getRestaurant() != null ? item.getRestaurant().getId() : null);
            stmt.setString(5, item.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("MenuItemRepository: update failed for id=" + item.getId(), e);
        }
    }

    @Override
    public void delete(String id) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_DELETE)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("MenuItemRepository: delete failed for id=" + id, e);
        }
    }

    private MenuItem mapRow(ResultSet rs) throws SQLException {
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

        return item;
    }
}