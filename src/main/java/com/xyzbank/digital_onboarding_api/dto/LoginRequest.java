package com.xyzbank.digital_onboarding_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Customer login request")
public record LoginRequest(
        @Schema(description = "Customer username", example = "andrei123")
        @NotBlank(message = "Username is required")
        String username,

        @Schema(description = "Customer password", example = "generated123")
        @NotBlank(message = "Password is required")
        String password
) {
}