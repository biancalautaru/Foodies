package interfaces;

import models.MenuItem;

import java.util.List;

public interface IMenuService {
    List<MenuItem> getMenuSortedByName(String restaurantId);
    List<MenuItem> getMenuSortedByPrice(String restaurantId);
}