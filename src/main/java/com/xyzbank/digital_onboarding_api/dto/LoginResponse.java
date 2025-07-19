package com.xyzbank.digital_onboarding_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Customer login response")
public record LoginResponse(
        @Schema(description = "Success/failure indicator", example = "true")
        boolean success,

        @Schema(description = "Response message", example = "Login successful")
        String message,

        @Schema(description = "Customer username (if successful)", example = "andrei123")
        String username,

        @Schema(description = "JWT authentication token (if successful)", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token
) {
    public static LoginResponse success(String username, String token) {
        return new LoginResponse(true, "Login successful", username, token);
    }

    public static LoginResponse failure(String message) {
        return new LoginResponse(false, message, null, null);
    }
}