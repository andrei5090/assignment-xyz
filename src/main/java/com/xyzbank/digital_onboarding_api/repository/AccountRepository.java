package com.xyzbank.digital_onboarding_api.repository;

import com.xyzbank.digital_onboarding_api.models.Account;
import com.xyzbank.digital_onboarding_api.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    //TODO: Remove if in the end I don't use it anywhere
    boolean existsByIban(String iban);

    Optional<Account> findByIban(String iban);

    List<Account> findByCustomer(Customer customer);
}