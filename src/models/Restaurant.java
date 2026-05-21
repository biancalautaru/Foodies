package models;

import interfaces.Displayable;
import interfaces.Reviewable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Restaurant implements Comparable<Restaurant>, Reviewable, Displayable {
    private String id;
    private String name;
    private Address address;
    private List<MenuItem> menu;
    private List<Review> reviews;

    public static final Comparator<Restaurant> BY_RATING = (a, b) -> {
        boolean aNoReviews = a.getReviewCount() == 0;
        boolean bNoReviews = b.getReviewCount() == 0;
        if (aNoReviews && bNoReviews)
            return a.getName().compareToIgnoreCase(b.getName());
        if (aNoReviews)
            return 1;
        if (bNoReviews)
            return -1;
        int cmp = Double.compare(b.getAverageRating(), a.getAverageRating());
        if (cmp != 0)
            return cmp;
        return a.getName().compareToIgnoreCase(b.getName());
    };

    public Restaurant(String id, String name, Address address) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.menu = new ArrayList<>();
        this.reviews = new ArrayList<>();
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
        return Collections.unmodifiableList(menu);
    }

    public List<Review> getReviews() {
        return Collections.unmodifiableList(reviews);
    }

    public void addMenuItem(MenuItem menuItem) {
        menu.add(menuItem);
        menuItem.setRestaurant(this);
    }

    @Override
    public void addReview(Review review) {
        reviews.add(review);
    }

    @Override
    public int getReviewCount() {
        return reviews.size();
    }

    @Override
    public double getAverageRating() {
        if (reviews.isEmpty())
            return 0.0;
        int sum = 0;
        for (Review r : reviews)
            sum += r.rating();
        return (double) sum / reviews.size();
    }

    @Override
    public int compareTo(Restaurant other) {
        return this.name.compareToIgnoreCase(other.name);
    }

    @Override
    public String toDisplayString() {
        if (getReviewCount() == 0)
            return name + " (Nicio recenzie)";
        return name + " (" + String.format("%.2f", getAverageRating()) + "/5 stele din " + getReviewCount() + " recenzii)";
    }

    @Override
    public String toString() {
        return toDisplayString();
    }
}