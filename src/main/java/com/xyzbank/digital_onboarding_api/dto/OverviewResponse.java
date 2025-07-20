package com.xyzbank.digital_onboarding_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Customer account overview response")
public record OverviewResponse(
        @Schema(description = "Account IBAN number", example = "NL85ANDR0000000001")
        String accountNumber,

        @Schema(description = "Type of account", example = "CHECKING")
        String accountType,

        @Schema(description = "Current account balance", example = "0.00")
        BigDecimal balance,

        @Schema(description = "Account currency", example = "EUR")
        String currency
) {
}