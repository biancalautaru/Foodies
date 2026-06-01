package repository;

import models.Customer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CustomerRepository extends AbstractRepository<Customer, String> {
    private static final CustomerRepository INSTANCE = new CustomerRepository();

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

    private CustomerRepository() {}

    public static CustomerRepository getInstance() {
        return INSTANCE;
    }

    @Override
    protected String repositoryName() {
        return "CustomerRepository";
    }

    @Override
    public void create(Customer customer) {
        executeWrite(SQL_INSERT, customer.getId(), customer.getName(), customer.getEmail(), customer.getPassword());
    }

    @Override
    public Customer read(String id) {
        return queryOne(SQL_SELECT_BY_ID, this::mapRow, id);
    }

    @Override
    public List<Customer> readAll() {
        return queryList(SQL_SELECT_ALL, this::mapRow);
    }

    public Customer readByEmail(String email) {
        return queryOne(SQL_SELECT_BY_EMAIL, this::mapRow, email);
    }

    @Override
    public void update(Customer customer) {
        executeWrite(SQL_UPDATE, customer.getName(), customer.getEmail(), customer.getPassword(), customer.getId());
    }

    @Override
    public void delete(String id) {
        executeWrite(SQL_DELETE, id);
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