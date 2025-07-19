package com.xyzbank.digital_onboarding_api.service;

import com.xyzbank.digital_onboarding_api.models.Customer;
import com.xyzbank.digital_onboarding_api.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public boolean isUsernameExists(String username) {
        return customerRepository.existsByUsername(username);
    }

    @Transactional
    @CacheEvict(value = {"customers", "usernames"}, key = "#customer.username")
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Optional<Customer> findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }
}