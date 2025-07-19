package com.xyzbank.digital_onboarding_api.service;

import com.xyzbank.digital_onboarding_api.dto.OverviewResponse;
import com.xyzbank.digital_onboarding_api.models.Account;
import com.xyzbank.digital_onboarding_api.models.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OverviewService {

    @Autowired
    private CustomerService customerService;

    public OverviewResponse getAccountOverview(String username) {
        Customer customer = customerService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Account account = customer.getAccounts().getFirst();
        
        return new OverviewResponse(
                account.getIban(),
                account.getAccountType().toString(),
                account.getBalance(),
                account.getCurrency().toString()
        );
    }
}