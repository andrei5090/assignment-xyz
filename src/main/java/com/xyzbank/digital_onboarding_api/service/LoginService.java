package com.xyzbank.digital_onboarding_api.service;

import com.xyzbank.digital_onboarding_api.dto.LoginRequest;
import com.xyzbank.digital_onboarding_api.dto.LoginResponse;
import com.xyzbank.digital_onboarding_api.models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    @Autowired
    private CustomerService customerService;

    public LoginResponse authenticateCustomer(LoginRequest request) {
        Optional<Customer> customerOpt = customerService.findByUsername(request.username());
        
        if (customerOpt.isEmpty()) {
            return LoginResponse.failure("Invalid username or password");
        }
        
        Customer customer = customerOpt.get();
        if (!customer.getPassword().equals(request.password())) {
            return LoginResponse.failure("Invalid username or password");
        }
        
        return LoginResponse.success(customer.getUsername());
    }
}