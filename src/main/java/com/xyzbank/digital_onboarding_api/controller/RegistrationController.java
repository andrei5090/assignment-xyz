package com.xyzbank.digital_onboarding_api.controller;

import com.xyzbank.digital_onboarding_api.dto.RegistrationRequest;
import com.xyzbank.digital_onboarding_api.dto.RegistrationResponse;
import com.xyzbank.digital_onboarding_api.service.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.converter.HttpMessageNotReadableException;

@RestController
@RequestMapping("/api")
@Tag(name = "Customer Registration", description = "API for customer onboarding and account creation")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/register")
    @Operation(summary = "Register new customer",
            description = "Create a new customer account with automatic IBAN generation and default random password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Customer successfully registered",
                    content = @Content(schema = @Schema(implementation = RegistrationResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input data or validation erro",
                    content = @Content(schema = @Schema(implementation = RegistrationResponse.class))),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = RegistrationResponse.class)))
    })
    public ResponseEntity<RegistrationResponse> register(@Valid @RequestBody RegistrationRequest request, BindingResult bindingResult) {

        // if the body is invalid send the first error message
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .findFirst()
                    .orElse("Validation failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(RegistrationResponse.failure(errorMessage));
        }

        try {
            RegistrationResponse response = registrationService.registerCustomer(request);

            if (response.username() != null) {

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (ConstraintViolationException e) {
            RegistrationResponse errorResponse = RegistrationResponse.failure("Invalid account data provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            RegistrationResponse errorResponse = RegistrationResponse.failure(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // workaround to display enum (json error parsing) since the bindingResult cannot handle it.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RegistrationResponse> handleJsonParseError(HttpMessageNotReadableException ex) {
        String message = "Invalid data format";
        if (ex.getMessage().contains("Country")) {
            message = "Invalid country. Only NL and BE are allowed";
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(RegistrationResponse.failure(message));
    }
}