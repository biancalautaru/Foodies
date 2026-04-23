package models;

public class Customer extends User {
    private Address[] addresses;
    private int addressCount;
    private Cart cart;

    public Customer(String id, String name, String email, String phone, int maxAddresses, int maxItems) {
        super(id, name, email, phone);
        this.addresses = new Address[maxAddresses];
        this.addressCount = 0;
        this.cart = new Cart(maxItems);
    }

    public Cart getCart() {
        return cart;
    }

    public void addAddress(Address address) {
        if (addressCount == addresses.length) {
            System.out.println("Error: Maximum addresses reached.");
            return;
        }

        addresses[addressCount++] = address;
    }
}