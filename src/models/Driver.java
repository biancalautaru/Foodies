package models;

public class Driver extends User {
    private boolean isAvailable;

    public Driver() {
        super();
        this.isAvailable = true;
    }

    public Driver(String id, String name, String email) {
        super(id, name, email);
        this.isAvailable = true;
    }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { this.isAvailable = available; }
}