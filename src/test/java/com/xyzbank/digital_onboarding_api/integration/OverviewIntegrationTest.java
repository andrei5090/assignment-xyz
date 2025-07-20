package com.xyzbank.digital_onboarding_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xyzbank.digital_onboarding_api.dto.LoginResponse;
import com.xyzbank.digital_onboarding_api.dto.OverviewResponse;
import com.xyzbank.digital_onboarding_api.enums.Country;
import com.xyzbank.digital_onboarding_api.utils.IntegrationTestUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OverviewIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    static Stream<Arguments> overviewCustomers() {
        return IntegrationTestUtils.overviewCustomers();
    }

    @ParameterizedTest
    @MethodSource("overviewCustomers")
    void shouldGetOverviewAfterRegistrationAndLogin(String name, String address, String username, Country country) throws Exception {
        LoginResponse login = IntegrationTestUtils.registerAndLogin(
                mockMvc, objectMapper, name, address, username, LocalDate.of(1990, 1, 1), country);

        String overviewResponse = mockMvc.perform(get("/api/overview")
                        .header("Authorization", "Bearer " + login.token()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        OverviewResponse overview = objectMapper.readValue(overviewResponse, OverviewResponse.class);

        assertNotNull(overview.accountNumber());
        assertTrue(overview.accountNumber().startsWith("NL")); // All customers get NL IBAN
        assertEquals("CHECKING", overview.accountType());
        assertEquals(0.0, overview.balance().doubleValue());
        assertEquals("EUR", overview.currency());
    }
}