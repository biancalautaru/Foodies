package models;

import exceptions.MixedRestaurantCartException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cart implements Cloneable {
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
            throw new MixedRestaurantCartException(this.restaurant.getName(), itemRestaurant.getName());

        items.add(item);
        System.out.println("Produsul '" + item.getName() + "' a fost adăugat în coș.");
    }

    public void clearCart() {
        this.items = new ArrayList<>();
        this.restaurant = null;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public Cart clone() {
        try {
            Cart cloned = (Cart) super.clone();
            cloned.items = new ArrayList<>(this.items);
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Clasa Cart este Cloneable - nu ar trebui să se întâmple", e);
        }
    }
}
