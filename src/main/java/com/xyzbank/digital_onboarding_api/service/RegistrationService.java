package com.xyzbank.digital_onboarding_api.service;

import com.xyzbank.digital_onboarding_api.dto.RegistrationRequest;
import com.xyzbank.digital_onboarding_api.dto.RegistrationResponse;
import com.xyzbank.digital_onboarding_api.models.Account;
import com.xyzbank.digital_onboarding_api.models.Customer;
import com.xyzbank.digital_onboarding_api.util.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PasswordGenerator passwordGenerator;

    @Transactional
    public RegistrationResponse registerCustomer(RegistrationRequest request) {
        if (customerService.isUsernameExists(request.username())) {
            return RegistrationResponse.failure("Username already exists");
        }

        String password = passwordGenerator.generatePassword();

        Customer customer = new Customer();
        customer.setName(request.name());
        customer.setAddress(request.address());
        customer.setUsername(request.username());
        customer.setDateOfBirth(request.dateOfBirth());
        customer.setCountry(request.country());
        customer.setPassword(password);

        Customer savedCustomer = customerService.saveCustomer(customer);

        Account account = accountService.createAccountForCustomer(savedCustomer);
        savedCustomer.addAccount(account);

        return RegistrationResponse.success(request.username(), password);
    }
}