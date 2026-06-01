package service;

import models.Customer;
import models.Driver;
import repository.CustomerRepository;
import repository.DriverRepository;

public class UserService {
    private final CustomerRepository customerRepository;
    private final DriverRepository driverRepository;

    public UserService() {
        this.customerRepository = CustomerRepository.getInstance();
        this.driverRepository = DriverRepository.getInstance();
    }

    public void addCustomer(Customer customer) {
        customerRepository.create(customer);
        AuditService.getInstance().log("addCustomer");
    }

    public void addDriver(Driver driver) {
        driverRepository.create(driver);
        AuditService.getInstance().log("addDriver");
    }

    public Driver findAvailableDriver() {
        return driverRepository.findAvailable();
    }

    public Driver findDriverById(String id) {
        return driverRepository.read(id);
    }

    public Customer login(String email, String password) {
        AuditService.getInstance().log("login");
        Customer customer = customerRepository.readByEmail(email);
        if (customer != null && customer.getPassword() != null && customer.getPassword().equals(password)) {
            return customer;
        }
        return null;
    }

    public Customer findCustomerByEmail(String email) {
        return customerRepository.readByEmail(email);
    }
}