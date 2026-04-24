package models;

public class Restaurant {
    private String id;
    private String name;
    private Address address;
    private MenuItem[] menu;
    private int menuCount;
    private double stars;
    private int reviewCount;

    public Restaurant(String id, String name, Address address, int maxItemCapacity) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.menu = new MenuItem[maxItemCapacity];
        this.menuCount = 0;
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

    public MenuItem[] getMenu() {
        return menu;
    }

    public int getMenuCount() {
        return menuCount;
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
    public String toString() {
        if (reviewCount == 0)
            return id + ": " + name + " (No reviews)";
        return id + ": " + name + " (" + String.format("%.2f", stars) + "/5 stars from " + reviewCount + " reviews)";
    }

    public void addMenuItem(MenuItem menuItem) {
        if (menuCount == menu.length) {
            System.out.println("Error: Maximum menu items reached.");
            return;
        }

        menu[menuCount++] = menuItem;
        menuItem.setRestaurant(this);
    }
}