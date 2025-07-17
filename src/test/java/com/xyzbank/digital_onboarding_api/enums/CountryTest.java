package com.xyzbank.digital_onboarding_api.enums;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CountryTest {

    // Test data
    static Stream<String> validIbans() {
        return Stream.of(
            "NL91ABNA0417164300",
            "BE68539007547034", 
            "NL12RABO0123456789",
            "BE71096123456769"
        );
    }

    static Stream<String> invalidIbans() {
        return Stream.of(
            "NL91ABNA041716430",  // Too short
            "BE6853900754703",    // Too short  
            "NL91abna0417164300", // Lowercase
            "BE6853900754703A"    // Invalid character
        );
    }

    @ParameterizedTest
    @MethodSource("validIbans")
    void testValidIbans(String iban) {
        Country country = Country.fromCode(iban.substring(0, 2));
        assertTrue(country.isValidIban(iban));
    }

    @ParameterizedTest
    @MethodSource("invalidIbans")
    void testInvalidIbans(String iban) {
        Country country = Country.fromCode(iban.substring(0, 2));
        assertFalse(country.isValidIban(iban));
    }

    @Test
    void testFromCode() {
        assertEquals(Country.NL, Country.fromCode("NL"));
        assertEquals(Country.BE, Country.fromCode("BE"));
        assertThrows(IllegalArgumentException.class, () -> Country.fromCode("DE"));
    }

    @Test
    void testDisplayNames() {
        assertEquals("Netherlands", Country.NL.getDisplayName());
        assertEquals("Belgium", Country.BE.getDisplayName());
    }
}