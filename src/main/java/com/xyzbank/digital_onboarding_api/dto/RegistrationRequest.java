package com.xyzbank.digital_onboarding_api.dto;

import com.xyzbank.digital_onboarding_api.enums.Country;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import com.xyzbank.digital_onboarding_api.validation.MinAge;

import java.time.LocalDate;

@Schema(description = "Customer registration request")
public record RegistrationRequest(
        @Schema(description = "Customer full name", example = "Andrei Popescu", minLength = 2, maxLength = 100)
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @Schema(description = "Customer address", example = "123 Main Street, Amsterdam", maxLength = 255)
        @NotBlank(message = "Address is required")
        @Size(max = 255, message = "Address cannot exceed 255 characters")
        String address,

        @Schema(description = "Unique username for the customer", example = "andrei123", minLength = 3, maxLength = 50)
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @Schema(description = "Customer date of birth (must be 18+ years old)", example = "1990-01-01")
        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        @MinAge(18)
        LocalDate dateOfBirth,

        @Schema(description = "Customer country (NL or BE only)", example = "NL")
        @NotNull(message = "Country is required")
        Country country
) {
}