package com.xyzbank.digital_onboarding_api.repository;

import com.xyzbank.digital_onboarding_api.models.Account;
import com.xyzbank.digital_onboarding_api.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByCustomer(Customer customer);
}