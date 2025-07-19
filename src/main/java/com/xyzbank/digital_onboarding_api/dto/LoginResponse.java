package com.xyzbank.digital_onboarding_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Customer login response")
public record LoginResponse(
        @Schema(description = "Success/failure indicator", example = "true")
        boolean success,

        @Schema(description = "Response message", example = "Login successful")
        String message,

        @Schema(description = "Customer username (if successful)", example = "andrei123")
        String username
) {
    public static LoginResponse success(String username) {
        return new LoginResponse(true, "Login successful", username);
    }

    public static LoginResponse failure(String message) {
        return new LoginResponse(false, message, null);
    }
}