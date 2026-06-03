package repository;

import exceptions.RepositoryException;
import models.Address;
import models.Restaurant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class RestaurantRepository extends AbstractRepository<Restaurant, String> {
    private static final RestaurantRepository INSTANCE = new RestaurantRepository();

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

    private RestaurantRepository() {}

    public static RestaurantRepository getInstance() {
        return INSTANCE;
    }

    @Override
    protected String repositoryName() {
        return "RestaurantRepository";
    }

    @Override
    public void create(Restaurant restaurant) {
        try {
            Address addr = restaurant.getAddress();
            executeWrite(SQL_INSERT_ADDRESS, addr.getId(), addr.getStreet(), addr.getNumber(), addr.getCity());
            executeWrite(SQL_INSERT, restaurant.getId(), restaurant.getName(), addr.getId());
        } catch (RepositoryException e) {
            throw new RepositoryException("RestaurantRepository: create failed for id=" + restaurant.getId(), e);
        }
    }

    @Override
    public Restaurant read(String id) {
        return queryOne(SQL_SELECT_BY_ID, this::mapRow, id);
    }

    @Override
    public List<Restaurant> readAll() {
        return queryList(SQL_SELECT_ALL, this::mapRow);
    }

    @Override
    public void update(Restaurant restaurant) {
        try {
            Address addr = restaurant.getAddress();
            executeWrite(SQL_UPDATE_ADDRESS, addr.getStreet(), addr.getNumber(), addr.getCity(), addr.getId());
            executeWrite(SQL_UPDATE, restaurant.getName(), addr.getId(), restaurant.getId());
        } catch (RepositoryException e) {
            throw new RepositoryException("RestaurantRepository: update failed for id=" + restaurant.getId(), e);
        }
    }

    @Override
    public void delete(String id) {
        executeWrite(SQL_DELETE, id);
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