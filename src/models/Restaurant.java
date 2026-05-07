package models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Restaurant implements Comparable<Restaurant> {
    private String id;
    private String name;
    private Address address;
    private List<MenuItem> menu;
    private double stars;
    private int reviewCount;

    public static final Comparator<Restaurant> BY_RATING = (a, b) -> {
        boolean aNoReviews = a.getReviewCount() == 0;
        boolean bNoReviews = b.getReviewCount() == 0;
        if (aNoReviews && bNoReviews)
            return a.getName().compareToIgnoreCase(b.getName());
        if (aNoReviews) return 1;
        if (bNoReviews) return -1;
        int cmp = Double.compare(b.getStars(), a.getStars());
        if (cmp != 0) return cmp;
        return a.getName().compareToIgnoreCase(b.getName());
    };

    public Restaurant(String id, String name, Address address) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.menu = new ArrayList<>();
        this.stars = 0.0;
        this.reviewCount = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public List<MenuItem> getMenu() {
        return menu;
    }

    public double getStars() {
        return stars;
    }

    public void setStars(double stars) {
        this.stars = stars;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    @Override
    public int compareTo(Restaurant other) {
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public String toString() {
        if (reviewCount == 0)
            return name + " (Fără recenzii)";
        return id + ": " + name + " (" + String.format("%.2f", stars) + "/5 stele din " + reviewCount + " recenzii)";
    }

    public void addMenuItem(MenuItem menuItem) {
        menu.add(menuItem);
        menuItem.setRestaurant(this);
    }
}
