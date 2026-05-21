package models;

import interfaces.Displayable;

import java.util.Comparator;

public class MenuItem implements Comparable<MenuItem>, Displayable {
    private String id;
    private String name;
    private String description;
    private double price;
    private Restaurant restaurant;

    public static final Comparator<MenuItem> BY_PRICE = Comparator.comparingDouble(MenuItem::getPrice);

    public MenuItem(String id, String name, String description, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.restaurant = null;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public int compareTo(MenuItem other) {
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public String toDisplayString() {
        return name + " - " + String.format("%.2f", price) + " lei";
    }

    @Override
    public String toString() {
        return toDisplayString();
    }
}