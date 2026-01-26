package restapi.service;

import restapi.model.Customer;

import java.util.List;

public interface CustomerService {

    /**
     * Creates a new customer
     * @param customer - Customer to be created
     */
    void create(Customer customer);

    /**
     * Returns a list of all existing customers
     * @return List of customers
     */
    List<Customer> readAll();

    /**
     * Returns a customer based on its ID
     * @param id - Customer ID
     * @return - Customer object with the given ID
     */
    Customer read(int id);

    /**
     * Updates the customer with the given ID,
     * according to the passed customer
     * @param customer - Customer to use to update the data
     * @param id - ID of the customer you want to update
     * @return - true if the data has been updated, otherwise false
     */
    boolean update(Customer customer, int id);

    /**
     * Deletes the customer with the given ID
     * @param id - ID of the customer to be deleted
     * @return - true if the customer was deleted, otherwise false
     */
    boolean delete(int id);
}