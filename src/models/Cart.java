package models;

import exceptions.InvalidOrderException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cart {
    private List<MenuItem> items;
    private Restaurant restaurant;

    public Cart() {
        this.items = new ArrayList<>();
        this.restaurant = null;
    }

    public List<MenuItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void addItem(MenuItem item) {
        Restaurant itemRestaurant = item.getRestaurant();

        if (items.isEmpty())
            this.restaurant = itemRestaurant;
        else if (!itemRestaurant.getId().equals(this.restaurant.getId()))
            throw new InvalidOrderException("Nu se pot adăuga produse de la '" + itemRestaurant.getName() + "' - coșul conține deja produse de la '" + this.restaurant.getName() + "'.");

        items.add(item);
    }

    public void clearCart() {
        this.items = new ArrayList<>();
        this.restaurant = null;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}