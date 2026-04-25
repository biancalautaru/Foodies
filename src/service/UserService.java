package service;

import models.*;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private List<Customer> customers;
    private List<Driver> drivers;

    public UserService() {
        this.customers = new ArrayList<>();
        this.drivers = new ArrayList<>();
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        System.out.println("Customer added: " + customer.getName());
    }

    public void addDriver(Driver driver) {
        drivers.add(driver);
        System.out.println("Driver added: " + driver.getName());
    }

    public Driver findAvailableDriver() {
        for (Driver driver : drivers)
            if (driver.isAvailable())
                return driver;

        return null;
    }
}
