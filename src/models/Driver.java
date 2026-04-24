package models;

public class Driver extends User {
    private boolean isAvailable;

    public Driver(String id, String name, String email, String phone) {
        super(id, name, email, phone);
        isAvailable = true;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}