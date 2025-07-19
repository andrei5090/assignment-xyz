package com.xyzbank.digital_onboarding_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyzbank.digital_onboarding_api.dto.RegistrationRequest;
import com.xyzbank.digital_onboarding_api.dto.RegistrationResponse;
import com.xyzbank.digital_onboarding_api.enums.Country;
import com.xyzbank.digital_onboarding_api.service.CustomerService;
import com.xyzbank.digital_onboarding_api.models.Customer;
import com.xyzbank.digital_onboarding_api.models.Account;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class RegistrationIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private RegistrationResponse performRegistration(RegistrationRequest request) throws Exception {
        String responseJson = mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readValue(responseJson, RegistrationResponse.class);
    }

    private void verifyCompleteRegistration(RegistrationRequest request, RegistrationResponse response) {
        // verify response data matches request
        assertEquals(request.username(), response.username());
        assertNotNull(response.password());
        assertEquals("Registration successful", response.message());

        // verify customer data in database
        Optional<Customer> customerOpt = customerService.findByUsername(request.username());
        assertTrue(customerOpt.isPresent());
        Customer customer = customerOpt.get();

        assertEquals(request.name(), customer.getName());
        assertEquals(request.address(), customer.getAddress());
        assertEquals(request.username(), customer.getUsername());
        assertEquals(request.dateOfBirth(), customer.getDateOfBirth());
        assertEquals(request.country(), customer.getCountry());
        assertEquals(response.password(), customer.getPassword());

        // verify account creation
        assertNotNull(customer.getAccounts());
        assertEquals(1, customer.getAccounts().size());
        Account account = customer.getAccounts().getFirst();
        assertNotNull(account.getIban());
        assertTrue(account.getIban().startsWith("NL"));
        assertEquals(customer, account.getCustomer());
    }

    private void verifyNoDataCreated(String username) {
        Optional<Customer> customerOpt = customerService.findByUsername(username);
        assertFalse(customerOpt.isPresent(), "No customer should have been created");
    }

    @Test
    void shouldRegisterValidCustomer() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "Andrei Popescu", "Amsterdam", "andrei123",
                LocalDate.of(1990, 1, 1), Country.NL);

        RegistrationResponse response = performRegistration(request);
        verifyCompleteRegistration(request, response);
    }

    @Test
    void shouldRejectDuplicateUsername() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "Maria Ionescu", "Rotterdam", "maria_test",
                LocalDate.of(1985, 5, 15), Country.NL);

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        String responseJson = mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse().getContentAsString();

        RegistrationResponse response = objectMapper.readValue(responseJson, RegistrationResponse.class);
        assertNull(response.username());
        assertTrue(response.message().contains("Username already exists"));
    }

    @Test
    void shouldRejectUnderageCustomer() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "Alex Minor", "Utrecht", "alex_young",
                LocalDate.now().minusYears(17), Country.NL);

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoDataCreated("alex_young");
    }

    @Test
    void shouldAcceptCustomerExactly18Years() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "Stefan Mihai", "The Hague", "stefan18",
                LocalDate.now().minusYears(18), Country.NL);

        RegistrationResponse response = performRegistration(request);
        verifyCompleteRegistration(request, response);
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

    @Test
    void shouldAcceptBelgianCustomer() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "Luc Van Belgium", "Brussels", "luc_belgium",
                LocalDate.of(1980, 12, 25), Country.BE);

        RegistrationResponse response = performRegistration(request);
        verifyCompleteRegistration(request, response);
    }
}