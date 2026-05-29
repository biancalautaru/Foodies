package service;

import interfaces.IUserService;
import models.Customer;
import models.Driver;
import repository.CustomerRepository;
import repository.DriverRepository;

public class UserService implements IUserService {
    private final CustomerRepository customerRepository;
    private final DriverRepository driverRepository;

    public UserService(CustomerRepository customerRepository, DriverRepository driverRepository) {
        this.customerRepository = customerRepository;
        this.driverRepository = driverRepository;
    }

    @Override
    public void addCustomer(Customer customer) {
        customerRepository.create(customer);
        AuditService.getInstance().log("addCustomer");
    }

    @Override
    public void addDriver(Driver driver) {
        driverRepository.create(driver);
        AuditService.getInstance().log("addDriver");
    }

    @Override
    public Driver findAvailableDriver() {
        return driverRepository.findAvailable();
    }

    @Override
    public Driver findDriverById(String id) {
        return driverRepository.read(id);
    }

    @Override
    public Customer login(String email, String password) {
        AuditService.getInstance().log("login");
        Customer customer = customerRepository.readByEmail(email);
        if (customer != null && customer.getPassword() != null && customer.getPassword().equals(password)) {
            return customer;
        }
        return null;
    }

    @Override
    public Customer findCustomerByEmail(String email) {
        return customerRepository.readByEmail(email);
    }
}