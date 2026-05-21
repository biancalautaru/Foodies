package service;

import interfaces.IUserService;
import models.Customer;
import models.Driver;

import java.util.ArrayList;
import java.util.List;

public class UserService implements IUserService {
    private List<Customer> customers;
    private List<Driver> drivers;

    public UserService() {
        this.customers = new ArrayList<>();
        this.drivers = new ArrayList<>();
    }

    @Override
    public void addCustomer(Customer customer) {
        customers.add(customer);
        AuditService.getInstance().log("addCustomer");
    }

    @Override
    public void addDriver(Driver driver) {
        drivers.add(driver);
        AuditService.getInstance().log("addDriver");
    }

    @Override
    public Driver findAvailableDriver() {
        for (Driver driver : drivers)
            if (driver.isAvailable())
                return driver;
        return null;
    }
}