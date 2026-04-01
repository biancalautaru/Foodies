package models;

public class Customer extends User {
    private Address deliveryAddress;

    public Customer(String id, String name, String email, String phone, Address deliveryAddress) {
        super(id, name, email, phone);
        this.deliveryAddress = deliveryAddress;
    }

    public Address getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(Address deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
