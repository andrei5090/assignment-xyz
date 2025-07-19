package com.xyzbank.digital_onboarding_api.models;

import com.xyzbank.digital_onboarding_api.enums.AccountType;
import com.xyzbank.digital_onboarding_api.enums.Country;
import com.xyzbank.digital_onboarding_api.enums.Currency;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private Validator validator;
    private Account account;

    static Stream<String> validIbans() {
        return Stream.of(
                "NL91ABNA0417164300",
                "BE68539007547034",
                "NL39RABO0300065264",
                "BE71096123456769"
        );
    }

    static Stream<String> invalidIbans() {
        return Stream.of(
                "",
                "INVALID",
                "DE68539007547034",  // Unsupported country
                "NL91ABNA041716430",  // Too short
                "BE6853900754703A"    // Invalid character
        );
    }

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        Customer customer = new Customer();
        customer.setName("Andrei");
        customer.setAddress("123 Street, City");
        customer.setUsername("andrei123");
        customer.setDateOfBirth(LocalDate.of(1990, 1, 1));
        customer.setCountry(Country.NL);
        customer.setPassword("password123");

        account = new Account();
        account.setIban("NL91ABNA0417164300");
        account.setBalance(new BigDecimal("1000.00"));
        account.setAccountType(AccountType.CHECKING);
        account.setCurrency(Currency.EUR);
        account.setCustomer(customer);
    }

    @Test
    void testValidAccount() {
        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("validIbans")
    void testValidIbans(String iban) {
        account.setIban(iban);
        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("invalidIbans")
    void testInvalidIbans(String iban) {
        account.setIban(iban);
        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullIban() {
        account.setIban(null);
        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        // Null IBAN is allowed during account creation, but in business logic
        // we ensure accounts always have valid IBANs before completion
        assertTrue(violations.isEmpty());
    }

    @Test
    void testNegativeBalance() {
        account.setBalance(new BigDecimal("-100.00"));
        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullBalance() {
        account.setBalance(null);
        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullAccountType() {
        account.setAccountType(null);
        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullCurrency() {
        account.setCurrency(null);
        Set<ConstraintViolation<Account>> violations = validator.validate(account);
        assertFalse(violations.isEmpty());
    }
}