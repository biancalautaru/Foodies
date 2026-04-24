package models;

public class MenuItem {
    private String id;
    private String name;
    private String description;
    private double price;
    private Restaurant restaurant;

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

    public double getPrice() {
        return price;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public String toString() {
        return name + " - " + String.format("%.2f", price) + " lei";
    }

}