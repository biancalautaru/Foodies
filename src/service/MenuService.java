package service;

import interfaces.IMenuService;
import interfaces.IRestaurantService;
import models.MenuItem;
import models.Restaurant;
import repository.MenuItemRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MenuService implements IMenuService {
    private final IRestaurantService restaurantService;
    private final MenuItemRepository menuItemRepository;

    public MenuService(IRestaurantService restaurantService, MenuItemRepository menuItemRepository) {
        this.restaurantService = restaurantService;
        this.menuItemRepository = menuItemRepository;
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
        Restaurant restaurant = restaurantService.findRestaurantById(restaurantId);
        List<MenuItem> sorted = new ArrayList<>(menuItemRepository.readByRestaurant(restaurantId));
        sorted.forEach(item -> item.setRestaurant(restaurant));
        sorted.sort(comparator);
        return sorted;
    }
}