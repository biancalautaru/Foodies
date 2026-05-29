package repository;

import config.DatabaseConfiguration;
import models.Driver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DriverRepository implements GenericRepository<Driver, String> {
    private static final String SQL_INSERT =
            "INSERT INTO drivers (id, name, email, is_available) VALUES (?, ?, ?, ?)";

    private static final String SQL_SELECT_BY_ID =
            "SELECT id, name, email, is_available FROM drivers WHERE id = ?";

    private static final String SQL_SELECT_ALL =
            "SELECT id, name, email, is_available FROM drivers";

    private static final String SQL_SELECT_AVAILABLE =
            "SELECT id, name, email, is_available FROM drivers WHERE is_available = TRUE LIMIT 1";

    private static final String SQL_UPDATE =
            "UPDATE drivers SET name = ?, email = ?, is_available = ? WHERE id = ?";

    private static final String SQL_UPDATE_AVAILABILITY =
            "UPDATE drivers SET is_available = ? WHERE id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM drivers WHERE id = ?";

    private final Connection connection;

    public DriverRepository() {
        this.connection = DatabaseConfiguration.getInstance().getConnection();
    }

    @Override
    public void create(Driver driver) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_INSERT)) {
            stmt.setString(1, driver.getId());
            stmt.setString(2, driver.getName());
            stmt.setString(3, driver.getEmail());
            stmt.setBoolean(4, driver.isAvailable());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DriverRepository: create failed for id=" + driver.getId(), e);
        }
    }

    @Override
    public Driver read(String id) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("DriverRepository: read failed for id=" + id, e);
        }
        return null;
    }

    @Override
    public List<Driver> readAll() {
        List<Driver> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("DriverRepository: readAll failed", e);
        }
        return result;
    }

    public Driver findAvailable() {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_AVAILABLE);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("DriverRepository: findAvailable failed", e);
        }
        return null;
    }

    @Override
    public void update(Driver driver) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, driver.getName());
            stmt.setString(2, driver.getEmail());
            stmt.setBoolean(3, driver.isAvailable());
            stmt.setString(4, driver.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DriverRepository: update failed for id=" + driver.getId(), e);
        }
    }

    public void updateAvailability(String driverId, boolean available) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_UPDATE_AVAILABILITY)) {
            stmt.setBoolean(1, available);
            stmt.setString(2, driverId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(
                    "DriverRepository: updateAvailability failed for id=" + driverId, e);
        }
    }

    @Override
    public void delete(String id) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_DELETE)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DriverRepository: delete failed for id=" + id, e);
        }
    }

    private Driver mapRow(ResultSet rs) throws SQLException {
        Driver driver = new Driver();
        driver.setId(rs.getString("id"));
        driver.setName(rs.getString("name"));
        driver.setEmail(rs.getString("email"));
        driver.setAvailable(rs.getBoolean("is_available"));
        return driver;
    }
}