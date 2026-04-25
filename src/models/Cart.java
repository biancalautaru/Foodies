package models;

import exceptions.MixedRestaurantCartException;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private List<MenuItem> items;
    private Restaurant restaurant;

    public Cart() {
        this.items = new ArrayList<>();
        this.restaurant = null;
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void addItem(MenuItem item) {
        Restaurant itemRestaurant = item.getRestaurant();

        if (items.isEmpty())
            this.restaurant = itemRestaurant;
        else if (!itemRestaurant.getId().equals(this.restaurant.getId())) {
            throw new MixedRestaurantCartException(this.restaurant.getName(), itemRestaurant.getName());
        }

        items.add(item);
        System.out.println("Item '" + item.getName() + "' added to cart.");
    }

    public void clearCart() {
        this.items = new ArrayList<>();
        this.restaurant = null;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
