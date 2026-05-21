package service;

import interfaces.IMenuService;
import interfaces.IRestaurantService;
import models.MenuItem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MenuService implements IMenuService {
    private final IRestaurantService restaurantService;

    public MenuService(IRestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @Override
    public List<MenuItem> getMenuSortedByName(String restaurantId) {
        return sortedMenu(restaurantId, Comparator.naturalOrder());
    }

    @Override
    public List<MenuItem> getMenuSortedByPrice(String restaurantId) {
        return sortedMenu(restaurantId, MenuItem.BY_PRICE);
    }

    private List<MenuItem> sortedMenu(String restaurantId, Comparator<MenuItem> comparator) {
        List<MenuItem> sorted = new ArrayList<>(restaurantService.findRestaurantById(restaurantId).getMenu());
        sorted.sort(comparator);
        return sorted;
    }
}