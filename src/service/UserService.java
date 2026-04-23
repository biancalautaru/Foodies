package service;

import models.*;

public class UserService {
    private Customer[] customers;
    private int customerCount;
    private Driver[] drivers;
    private int driverCount;

    public UserService(int maxUsers) {
        this.customers = new Customer[maxUsers];
        this.customerCount = 0;
        this.drivers = new Driver[maxUsers];
        this.driverCount = 0;
    }

    public void addCustomer(Customer customer) {
        if (customerCount == customers.length) {
            System.out.println("Error: Maximum customers reached.");
            return;
        }

        customers[customerCount++] = customer;
        System.out.println("Customer added: " + customer.getName());
    }

    public void addDriver(Driver driver) {
        if (driverCount == drivers.length) {
            System.out.println("Error: Maximum drivers reached.");
            return;
        }

        drivers[driverCount++] = driver;
        System.out.println("Driver added: " + driver.getName());
    }

    public Driver findDriverById(String id) {
        for (int i = 0; i < driverCount; i++)
            if (drivers[i].getId().equals(id))
                return drivers[i];
        return null;
    }

    public Driver findAvailableDriver() {
        for (int i = 0; i < driverCount; i++)
            if (drivers[i].isAvailable())
                return drivers[i];

        return null;
    }
}
