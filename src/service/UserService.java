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
}