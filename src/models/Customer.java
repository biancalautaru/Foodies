package models;

public class Customer extends User {
    private Cart cart;
    private String password;

    public Customer() {
        super();
        this.cart = new Cart();
    }

    public Customer(String id, String name, String email) {
        super(id, name, email);
        this.cart = new Cart();
    }

    public Customer(String id, String name, String email, String password) {
        super(id, name, email);
        this.cart = new Cart();
        this.password = password;
    }

    public Cart getCart() { return cart; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}