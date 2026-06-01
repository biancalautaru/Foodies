package repository;

import models.MenuItem;
import models.Restaurant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MenuItemRepository extends AbstractRepository<MenuItem, String> {
    private static final MenuItemRepository INSTANCE = new MenuItemRepository();

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

    private MenuItemRepository() {}

    public static MenuItemRepository getInstance() {
        return INSTANCE;
    }

    @Override
    protected String repositoryName() {
        return "MenuItemRepository";
    }

    @Override
    public void create(MenuItem item) {
        executeWrite(SQL_INSERT, item.getId(), item.getName(), item.getDescription(), item.getPrice(),
                item.getRestaurant() != null ? item.getRestaurant().getId() : null);
    }

    @Override
    public MenuItem read(String id) {
        return queryOne(SQL_SELECT_BY_ID, this::mapRow, id);
    }

    @Override
    public List<MenuItem> readAll() {
        return queryList(SQL_SELECT_ALL, this::mapRow);
    }

    public List<MenuItem> readByRestaurant(String restaurantId) {
        return queryList(SQL_SELECT_BY_RESTAURANT, this::mapRow, restaurantId);
    }

    @Override
    public void update(MenuItem item) {
        executeWrite(SQL_UPDATE, item.getName(), item.getDescription(), item.getPrice(),
                item.getRestaurant() != null ? item.getRestaurant().getId() : null, item.getId());
    }

    @Override
    public void delete(String id) {
        executeWrite(SQL_DELETE, id);
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