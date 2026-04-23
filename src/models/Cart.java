package models;

public class Cart {
    private MenuItem[] items;
    private int itemsCount;
    private Restaurant restaurant;

    public Cart(int maxCartCapacity) {
        this.items = new MenuItem[maxCartCapacity];
        this.itemsCount = 0;
        this.restaurant = null;
    }

    public MenuItem[] getItems() {
        return items;
    }

    public int getItemsCount() {
        return itemsCount;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void addItem(MenuItem item) {
        Restaurant itemRestaurant = item.getRestaurant();

        if (itemsCount == 0)
            this.restaurant = itemRestaurant;
        else
            if (!itemRestaurant.getId().equals(this.restaurant.getId())) {
                System.out.println("Error: Cannot add items from different restaurants to cart.");
                return;
            }

        if (itemsCount == items.length) {
            System.out.println("Error: Cart is full.");
            return;
        }

        items[itemsCount++] = item;
        System.out.println("Item '" + item.getName() + "' added to cart.");
    }

    public void clearCart() {
        this.items = new MenuItem[this.items.length];
        this.itemsCount = 0;
        this.restaurant = null;
    }

    public boolean isEmpty() {
        return itemsCount == 0;
    }
}