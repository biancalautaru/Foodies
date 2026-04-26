package models;

public record Address(String street, String number, String city) {

    @Override
    public String toString() {
        return street + " " + number + ", " + city;
    }
}
