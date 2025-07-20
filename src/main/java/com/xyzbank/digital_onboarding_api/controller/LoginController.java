package com.xyzbank.digital_onboarding_api.controller;

import com.xyzbank.digital_onboarding_api.dto.LoginRequest;
import com.xyzbank.digital_onboarding_api.dto.LoginResponse;
import com.xyzbank.digital_onboarding_api.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Customer Authentication", description = "API for customer login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    @Operation(summary = "Customer login", description = "Authenticate customer with username and password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Login successful",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class)))
    })
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = loginService.authenticateCustomer(request);

            if (response.success()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace(); // TODO: delete
            LoginResponse errorResponse = LoginResponse.failure("Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}