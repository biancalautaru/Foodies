package models;

public class Customer extends User {
    private Cart cart;

    public Customer(String id, String name, String email, String phone, int maxItems) {
        super(id, name, email, phone);
        this.cart = new Cart(maxItems);
    }

    public Cart getCart() {
        return cart;
    }
}