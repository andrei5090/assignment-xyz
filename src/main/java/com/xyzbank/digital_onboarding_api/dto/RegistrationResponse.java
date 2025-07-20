package com.xyzbank.digital_onboarding_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Customer registration response")
public record RegistrationResponse(
        @Schema(description = "Username of the registered customer (null on failure)", example = "andrei123")
        String username,

        @Schema(description = "Generated password for the customer (null on failure)", example = "WB3d5f2OhP1k")
        String password,

        @Schema(description = "Response message indicating success or failure", example = "Registration successful")
        String message
) {
    public static RegistrationResponse success(String username, String password) {
        return new RegistrationResponse(username, password, "Registration successful");
    }

    public static RegistrationResponse failure(String message) {
        return new RegistrationResponse(null, null, "Account creation failed: " + message);
    }
}