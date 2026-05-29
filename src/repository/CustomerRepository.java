package repository;

import config.DatabaseConfiguration;
import models.Customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepository implements GenericRepository<Customer, String> {
    private static final String SQL_INSERT =
            "INSERT INTO customers (id, name, email, password) VALUES (?, ?, ?, ?)";

    private static final String SQL_SELECT_BY_ID =
            "SELECT id, name, email, password FROM customers WHERE id = ?";

    private static final String SQL_SELECT_BY_EMAIL =
            "SELECT id, name, email, password FROM customers WHERE email = ?";

    private static final String SQL_SELECT_ALL =
            "SELECT id, name, email, password FROM customers";

    private static final String SQL_UPDATE =
            "UPDATE customers SET name = ?, email = ?, password = ? WHERE id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM customers WHERE id = ?";

    private final Connection connection;

    public CustomerRepository() {
        this.connection = DatabaseConfiguration.getInstance().getConnection();
    }

    @Override
    public void create(Customer customer) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_INSERT)) {
            stmt.setString(1, customer.getId());
            stmt.setString(2, customer.getName());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPassword());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("CustomerRepository: create failed for id=" + customer.getId(), e);
        }
    }

    @Override
    public Customer read(String id) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_BY_ID)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("CustomerRepository: read failed for id=" + id, e);
        }
        return null;
    }

    @Override
    public List<Customer> readAll() {
        List<Customer> result = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("CustomerRepository: readAll failed", e);
        }
        return result;
    }

    public Customer readByEmail(String email) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_SELECT_BY_EMAIL)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("CustomerRepository: readByEmail failed for email=" + email, e);
        }
        return null;
    }

    @Override
    public void update(Customer customer) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_UPDATE)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getEmail());
            stmt.setString(3, customer.getPassword());
            stmt.setString(4, customer.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("CustomerRepository: update failed for id=" + customer.getId(), e);
        }
    }

    @Override
    public void delete(String id) {
        try (PreparedStatement stmt = connection.prepareStatement(SQL_DELETE)) {
            stmt.setString(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("CustomerRepository: delete failed for id=" + id, e);
        }
    }

    private Customer mapRow(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getString("id"));
        customer.setName(rs.getString("name"));
        customer.setEmail(rs.getString("email"));
        customer.setPassword(rs.getString("password"));
        return customer;
    }
}