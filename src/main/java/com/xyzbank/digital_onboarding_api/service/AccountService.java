package com.xyzbank.digital_onboarding_api.service;

import com.xyzbank.digital_onboarding_api.enums.AccountType;
import com.xyzbank.digital_onboarding_api.models.Account;
import com.xyzbank.digital_onboarding_api.models.Customer;
import com.xyzbank.digital_onboarding_api.repository.AccountRepository;
import com.xyzbank.digital_onboarding_api.util.IbanGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private IbanGenerator ibanGenerator;

    @Transactional
    public Account createAccountForCustomer(Customer customer) {
        Account account = new Account();
        account.setBalance(BigDecimal.ZERO);
        account.setAccountType(AccountType.CHECKING);
        account.setCustomer(customer);

        // save to get ID
        account = accountRepository.save(account);

        // generate IBAN using the unique ID
        String iban = ibanGenerator.generateNLIban(account.getId());
        if (iban == null || iban.trim().isEmpty()) {
            throw new IllegalStateException("Failed to generate valid IBAN");
        }
        account.setIban(iban);

        // since the validation is true when the iban is null, we check if the iban rn is not empty and not null
        // triggering full validation -> the exception will rollback all the changes
        return accountRepository.save(account);
    }
}