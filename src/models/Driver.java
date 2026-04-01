package models;

public class Driver extends User {
    private VehicleType vehicleType;
    private String licensePlate;
    private boolean isAvailable;

    public Driver(String id, String name, String email, String phone, VehicleType vehicleType, boolean isAvailable) {
        super(id, name, email, phone);
        this.vehicleType = vehicleType;
        this.isAvailable = isAvailable;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
