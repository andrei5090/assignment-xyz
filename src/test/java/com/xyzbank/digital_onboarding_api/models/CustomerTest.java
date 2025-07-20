package com.xyzbank.digital_onboarding_api.models;

import com.xyzbank.digital_onboarding_api.enums.Country;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Validator validator;
    private Customer customer;

    static Stream<String> invalidNames() {
        return Stream.of(null, "", "A", " ", "   ");
    }

    static Stream<String> validNames() {
        return Stream.of("John Doe", "Jane Smith", "AB", "Jean-Pierre");
    }

    static Stream<String> invalidUsernames() {
        return Stream.of(null, "", "ab", " ", "  ");
    }

    static Stream<String> validUsernames() {
        return Stream.of("johndoe", "jane", "user123", "john_doe");
    }

    static Stream<String> invalidPasswords() {
        return Stream.of(null, "", "1234567", " ", "   ");
    }

    static Stream<String> validPasswords() {
        return Stream.of("password123", "12345678", "mypassword");
    }

    static Stream<LocalDate> validDatesOfBirth() {
        return Stream.of(
                LocalDate.of(1990, 1, 1),
                LocalDate.of(1980, 5, 15),
                LocalDate.now().minusYears(18),
                LocalDate.now().minusYears(25),
                LocalDate.now().minusYears(18).minusDays(1)
        );
    }

    static Stream<LocalDate> invalidDatesOfBirth() {
        return Stream.of(
                LocalDate.of(2010, 1, 1),
                LocalDate.now().minusYears(17),
                LocalDate.now().minusYears(10),
                LocalDate.now().minusYears(17).minusMonths(11),
                LocalDate.now().minusYears(18).plusDays(1)
        );
    }

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        customer = new Customer();
        customer.setName("Andrei");
        customer.setAddress("123 Street, City");
        customer.setUsername("andrei123");
        customer.setDateOfBirth(LocalDate.of(1990, 1, 1));
        customer.setCountry(Country.NL);
        customer.setPassword("password123");
    }

    @Test
    void testValidCustomer() {
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("invalidNames")
    void testInvalidNames(String name) {
        customer.setName(name);
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("validNames")
    void testValidNames(String name) {
        customer.setName(name);
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("invalidUsernames")
    void testInvalidUsernames(String username) {
        customer.setUsername(username);
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("validUsernames")
    void testValidUsernames(String username) {
        customer.setUsername(username);
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("invalidPasswords")
    void testInvalidPasswords(String password) {
        customer.setPassword(password);
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("validPasswords")
    void testValidPasswords(String password) {
        customer.setPassword(password);
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testFutureDateOfBirth() {
        customer.setDateOfBirth(LocalDate.now().plusDays(1));
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testNullCountry() {
        customer.setCountry(null);
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertFalse(violations.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("validDatesOfBirth")
    void testValidDatesOfBirth(LocalDate dateOfBirth) {
        customer.setDateOfBirth(dateOfBirth);
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("invalidDatesOfBirth")
    void testInvalidDatesOfBirth(LocalDate dateOfBirth) {
        customer.setDateOfBirth(dateOfBirth);
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        assertFalse(violations.isEmpty());
    }
}