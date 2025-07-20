package com.xyzbank.digital_onboarding_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyzbank.digital_onboarding_api.dto.RegistrationRequest;
import com.xyzbank.digital_onboarding_api.dto.RegistrationResponse;
import com.xyzbank.digital_onboarding_api.enums.Country;
import com.xyzbank.digital_onboarding_api.service.CustomerService;
import com.xyzbank.digital_onboarding_api.models.Customer;
import com.xyzbank.digital_onboarding_api.models.Account;
import com.xyzbank.digital_onboarding_api.utils.IntegrationTestUtils;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RegistrationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    private void verifyNoDataCreated(String username) {
        Optional<Customer> customerOpt = customerService.findByUsername(username);
        assertFalse(customerOpt.isPresent(), "No customer should have been created");
    }

    static Stream<Arguments> validCustomers() {
        return IntegrationTestUtils.validCustomers();
    }

    static Stream<Arguments> invalidAgeCustomers() {
        return IntegrationTestUtils.invalidAgeCustomers();
    }

    @ParameterizedTest
    @MethodSource("validCustomers")
    void shouldRegisterValidCustomers(String name, String address, String username, LocalDate dateOfBirth, Country country) throws Exception {
        RegistrationResponse response = IntegrationTestUtils.registerCustomer(
                mockMvc, objectMapper, name, address, username, dateOfBirth, country);

        assertEquals(username, response.username());
        assertNotNull(response.password());
        assertEquals("Registration successful", response.message());

        Optional<Customer> customerOpt = customerService.findByUsername(username);
        assertTrue(customerOpt.isPresent());
        Customer customer = customerOpt.get();

        assertEquals(name, customer.getName());
        assertEquals(username, customer.getUsername());
        assertEquals(country, customer.getCountry());

        assertNotNull(customer.getAccounts());
        assertEquals(1, customer.getAccounts().size());
        assertTrue(customer.getAccounts().getFirst().getIban().startsWith("NL"));
    }

    @ParameterizedTest
    @MethodSource("invalidAgeCustomers")
    void shouldRejectUnderageCustomers(String name, String address, String username, LocalDate dateOfBirth, Country country) throws Exception {
        IntegrationTestUtils.expectRegistrationFailure(mockMvc, objectMapper, name, address, username, dateOfBirth, country);
        verifyNoDataCreated(username);
    }

    @Test
    void shouldRejectDuplicateUsername() throws Exception {
        IntegrationTestUtils.registerCustomer(mockMvc, objectMapper, 
                "Unique User", "Utrecht", "unique_duplicate_test", LocalDate.of(1985, 5, 15), Country.NL);

        IntegrationTestUtils.expectRegistrationFailure(mockMvc, objectMapper,
                "Different Person", "Different Address", "unique_duplicate_test", LocalDate.of(1990, 1, 1), Country.NL);
    }

    @Test
    void shouldRejectInvalidCountry() throws Exception {
        // need manual json since we cannot map to a non existent enum
        String invalidJson = """
                {
                    "name": "Pierre Dubois",
                    "address": "Paris",
                    "username": "pierre_paris",
                    "dateOfBirth": "1990-01-01",
                    "country": "FR"
                }
                """;

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        verifyNoDataCreated("pierre_paris");
    }

}