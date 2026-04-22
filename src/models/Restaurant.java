package models;

public class Restaurant {
    private String id;
    private String name;
    private Address address;
    private MenuItem[] menu;
    private int menuCount;

    public Restaurant(String id, String name, Address address, int maxItemCapacity) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.menu = new MenuItem[maxItemCapacity];
        this.menuCount = 0;
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

    @Override
    public String toString() {
        return id + ": " + name;
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
