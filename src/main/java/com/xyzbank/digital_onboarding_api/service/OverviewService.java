package com.xyzbank.digital_onboarding_api.service;

import com.xyzbank.digital_onboarding_api.dto.OverviewResponse;
import com.xyzbank.digital_onboarding_api.models.Account;
import com.xyzbank.digital_onboarding_api.models.Customer;
import com.xyzbank.digital_onboarding_api.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OverviewService {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public OverviewResponse getAccountOverview(String username) {
        Customer customer = customerService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<Account> accounts = accountRepository.findByCustomer(customer);
        Account account = accounts.getFirst();

        return new OverviewResponse(
                account.getIban(),
                account.getAccountType().toString(),
                account.getBalance(),
                account.getCurrency().toString()
        );
    }
}