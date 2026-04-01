package models;

public class Restaurant {
    private String id;
    private String name;
    private MenuItem[] menu;
    private int menuCount;

    public Restaurant(String id, String name, int maxMenuCapacity) {
        this.id = id;
        this.name = name;
        this.menu = new MenuItem[maxMenuCapacity];
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

    public void addMenuItem(MenuItem menuItem) {
        if (this.menuCount < menu.length) {
            menu[menuCount] = menuItem;
            menuCount++;
        }
        else {
            System.out.println("Menu Full");
        }
    }
}
