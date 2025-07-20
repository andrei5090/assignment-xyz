package com.xyzbank.digital_onboarding_api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyzbank.digital_onboarding_api.dto.LoginRequest;
import com.xyzbank.digital_onboarding_api.dto.LoginResponse;
import com.xyzbank.digital_onboarding_api.dto.RegistrationRequest;
import com.xyzbank.digital_onboarding_api.dto.RegistrationResponse;
import com.xyzbank.digital_onboarding_api.enums.Country;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IntegrationTestUtils {

    public static RegistrationResponse registerCustomer(MockMvc mockMvc, ObjectMapper objectMapper,
                                                        String name, String address, String username, LocalDate dateOfBirth, Country country) throws Exception {
        RegistrationRequest regRequest = new RegistrationRequest(name, address, username, dateOfBirth, country);

        String regResponse = mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        RegistrationResponse registration = objectMapper.readValue(regResponse, RegistrationResponse.class);
        assertEquals(username, registration.username());
        assertNotNull(registration.password());
        return registration;
    }

    public static LoginResponse loginCustomer(MockMvc mockMvc, ObjectMapper objectMapper,
                                              String username, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(username, password);

        String loginResponse = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        LoginResponse login = objectMapper.readValue(loginResponse, LoginResponse.class);
        assertTrue(login.success());
        assertNotNull(login.token());
        return login;
    }

    public static void expectRegistrationFailure(MockMvc mockMvc, ObjectMapper objectMapper,
                                                 String name, String address, String username, LocalDate dateOfBirth, Country country) throws Exception {
        RegistrationRequest regRequest = new RegistrationRequest(name, address, username, dateOfBirth, country);

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isBadRequest());
    }

    public static LoginResponse registerAndLogin(MockMvc mockMvc, ObjectMapper objectMapper,
                                                 String name, String address, String username, LocalDate dateOfBirth, Country country) throws Exception {
        RegistrationResponse registration = registerCustomer(mockMvc, objectMapper, name, address, username, dateOfBirth, country);
        return loginCustomer(mockMvc, objectMapper, registration.username(), registration.password());
    }

    public static Stream<Arguments> validCustomers() {
        return Stream.of(
                Arguments.of("Andrei Popescu", "Amsterdam", "andrei123", LocalDate.of(1990, 1, 1), Country.NL),
                Arguments.of("Maria Ionescu", "Rotterdam", "maria_test", LocalDate.of(1985, 5, 15), Country.NL),
                Arguments.of("Luc Van Belgium", "Brussels", "luc_belgium", LocalDate.of(1980, 12, 25), Country.BE),
                Arguments.of("Stefan Mihai", "The Hague", "stefan18", LocalDate.now().minusYears(18), Country.NL)
        );
    }

    public static Stream<Arguments> invalidAgeCustomers() {
        return Stream.of(
                Arguments.of("Alex Minor", "Utrecht", "alex_young", LocalDate.now().minusYears(17), Country.NL),
                Arguments.of("Young Person", "Eindhoven", "youngperson", LocalDate.of(2010, 1, 1), Country.BE)
        );
    }

    public static Stream<Arguments> invalidCountryCustomers() {
        return Stream.of(
                Arguments.of("Hans Mueller", "Berlin", "hansmueller", LocalDate.of(1990, 1, 1), "DE"),
                Arguments.of("Pierre Dubois", "Paris", "pierre_paris", LocalDate.of(1988, 3, 20), "FR"),
                Arguments.of("John Smith", "London", "johnsmith", LocalDate.of(1985, 6, 10), "UK")
        );
    }

    public static Stream<Arguments> loginTestData() {
        return Stream.of(
                Arguments.of("andrei123", true, 200),
                Arguments.of("wrong", false, 401),
                Arguments.of("nonexistent", false, 401)
        );
    }

    public static Stream<Arguments> overviewCustomers() {
        return Stream.of(
                Arguments.of("Andrei Popescu", "Amsterdam", "andrei_overview", Country.NL),
                Arguments.of("Luc Van Belgium", "Brussels", "luc_overview", Country.BE)
        );
    }
}