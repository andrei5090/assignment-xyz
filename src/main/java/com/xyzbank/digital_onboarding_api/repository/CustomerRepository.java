package com.xyzbank.digital_onboarding_api.repository;

import com.xyzbank.digital_onboarding_api.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    boolean existsByUsername(String username);

    Optional<Customer> findByUsername(String username);
}