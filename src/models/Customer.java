package models;

public class Customer extends User {
    private Cart cart;

    public Customer(String id, String name, String email, String phone) {
        super(id, name, email, phone);
        this.cart = new Cart();
    }

    public Cart getCart() {
        return cart;
    }
}
