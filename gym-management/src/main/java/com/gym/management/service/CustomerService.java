package com.gym.management.service;

import com.gym.management.exception.BusinessException;
import com.gym.management.model.Customer;
import com.gym.management.repository.AbonamentRepository;
import com.gym.management.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final AbonamentRepository abonamentRepository;

    public CustomerService(CustomerRepository customerRepository, AbonamentRepository abonamentRepository) {
        this.customerRepository = customerRepository;
        this.abonamentRepository = abonamentRepository;
    }

    @Transactional(readOnly = true)
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
    }

    public Customer save(Customer customer) {
        if (customer.getId() == null) {
            customerRepository.findByEmail(customer.getEmail()).ifPresent(existing -> {
                throw new IllegalArgumentException("Email already registered: " + customer.getEmail());
            });
        } else if (customerRepository.existsByEmailAndIdNot(customer.getEmail(), customer.getId())) {
            throw new IllegalArgumentException("Email already registered: " + customer.getEmail());
        }
        return customerRepository.save(customer);
    }

    public void delete(Long id) {
        Customer customer = findById(id);
        long subscriptionCount = abonamentRepository.countByCustomerId(id);
        if (subscriptionCount > 0) {
            throw new BusinessException(buildDeleteBlockedMessage(customer.getFullName(), subscriptionCount));
        }
        customerRepository.deleteById(id);
    }

    private static String buildDeleteBlockedMessage(String customerName, long count) {
        String noun = count == 1 ? "subscription" : "subscriptions";
        return "Cannot delete customer \"" + customerName + "\" because they have "
                + count + " linked " + noun + ". "
                + "Delete those subscriptions first from the Subscriptions page.";
    }
}
