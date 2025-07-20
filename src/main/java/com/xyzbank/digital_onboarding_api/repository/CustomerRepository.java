package com.xyzbank.digital_onboarding_api.repository;

import com.xyzbank.digital_onboarding_api.models.Customer;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Cacheable("usernames")
    boolean existsByUsername(String username);

    @Cacheable("customers")
    Optional<Customer> findByUsername(String username);
}