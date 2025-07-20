package com.xyzbank.digital_onboarding_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyzbank.digital_onboarding_api.dto.LoginRequest;
import com.xyzbank.digital_onboarding_api.dto.LoginResponse;
import com.xyzbank.digital_onboarding_api.dto.RegistrationResponse;
import com.xyzbank.digital_onboarding_api.enums.Country;
import com.xyzbank.digital_onboarding_api.utils.IntegrationTestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    static Stream<Arguments> loginTestData() {
        return IntegrationTestUtils.loginTestData();
    }

    @ParameterizedTest
    @MethodSource("loginTestData")
    void testLogin(String username, boolean expectToken, int expectedStatus) throws Exception {
        RegistrationResponse registration = IntegrationTestUtils.registerCustomer(
                mockMvc, objectMapper, "Andrei Popescu", "Amsterdam", "andrei123", 
                LocalDate.of(1990, 1, 1), Country.NL);

        LoginRequest loginRequest = new LoginRequest(username, 
                username.equals("andrei123") ? registration.password() : "wrongpass");
        
        String loginResponse = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is(expectedStatus))
                .andReturn().getResponse().getContentAsString();
        
        LoginResponse login = objectMapper.readValue(loginResponse, LoginResponse.class);
        assertEquals(expectToken, login.token() != null);
    }
}