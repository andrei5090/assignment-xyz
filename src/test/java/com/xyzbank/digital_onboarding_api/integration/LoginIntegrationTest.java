package com.xyzbank.digital_onboarding_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyzbank.digital_onboarding_api.dto.LoginRequest;
import com.xyzbank.digital_onboarding_api.dto.LoginResponse;
import com.xyzbank.digital_onboarding_api.dto.RegistrationRequest;
import com.xyzbank.digital_onboarding_api.dto.RegistrationResponse;
import com.xyzbank.digital_onboarding_api.enums.Country;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Transactional
class LoginIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    static Stream<Arguments> loginTestData() {
        return Stream.of(
                Arguments.of("andrei123", true, 200),
                Arguments.of("wrong", false, 401),
                Arguments.of("nonexistent", false, 401)
        );
    }

    @ParameterizedTest
    @MethodSource("loginTestData")
    void testLogin(String username, boolean expectToken, int expectedStatus) throws Exception {
        RegistrationRequest regRequest = new RegistrationRequest(
                "Andrei Popescu", "Amsterdam", "andrei123",
                LocalDate.of(1990, 1, 1), Country.NL);
        
        String regResponse = mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        
        RegistrationResponse registration = objectMapper.readValue(regResponse, RegistrationResponse.class);

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