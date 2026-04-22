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

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public MenuItem[] getMenu() {
        return menu;
    }

    public void setMenu(MenuItem[] menu) {
        this.menu = menu;
    }

    public int getMenuCount() {
        return menuCount;
    }

    public void setMenuCount(int menuCount) {
        this.menuCount = menuCount;
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
