package interfaces;

import models.Customer;
import models.Driver;

public interface IUserService {
    void addCustomer(Customer customer);
    void addDriver(Driver driver);
    Driver findAvailableDriver();
}