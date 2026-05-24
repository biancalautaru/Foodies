package models;

public class Address {
    private String id;
    private String street;
    private String number;
    private String city;

    public Address() {}

    public Address(String id, String street, String number, String city) {
        this.id = id;
        this.street = street;
        this.number = number;
        this.city = city;
    }

    public Address(String street, String number, String city) {
        this.street = street;
        this.number = number;
        this.city = city;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    @Override
    public String toString() {
        return street + " " + number + ", " + city;
    }
}