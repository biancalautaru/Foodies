package interfaces;

import models.MenuItem;

import java.util.List;

public interface IMenuService {
    List<MenuItem> getMenu(String restaurantId);
}