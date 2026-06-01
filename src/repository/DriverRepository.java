package repository;

import models.Driver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DriverRepository extends AbstractRepository<Driver, String> {
    private static final DriverRepository INSTANCE = new DriverRepository();

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

    private DriverRepository() {}

    public static DriverRepository getInstance() {
        return INSTANCE;
    }

    @Override
    protected String repositoryName() {
        return "DriverRepository";
    }

    @Override
    public void create(Driver driver) {
        executeWrite(SQL_INSERT, driver.getId(), driver.getName(), driver.getEmail(), driver.isAvailable());
    }

    @Override
    public Driver read(String id) {
        return queryOne(SQL_SELECT_BY_ID, this::mapRow, id);
    }

    @Override
    public List<Driver> readAll() {
        return queryList(SQL_SELECT_ALL, this::mapRow);
    }

    public Driver findAvailable() {
        return queryOne(SQL_SELECT_AVAILABLE, this::mapRow);
    }

    @Override
    public void update(Driver driver) {
        executeWrite(SQL_UPDATE, driver.getName(), driver.getEmail(), driver.isAvailable(), driver.getId());
    }

    public void updateAvailability(String driverId, boolean available) {
        executeWrite(SQL_UPDATE_AVAILABILITY, available, driverId);
    }

    @Override
    public void delete(String id) {
        executeWrite(SQL_DELETE, id);
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