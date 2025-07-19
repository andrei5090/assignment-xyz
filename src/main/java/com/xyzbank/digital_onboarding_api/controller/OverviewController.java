package com.xyzbank.digital_onboarding_api.controller;

import com.xyzbank.digital_onboarding_api.dto.OverviewResponse;
import com.xyzbank.digital_onboarding_api.service.OverviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Account Overview", description = "API for customer account overview")
public class OverviewController {

    @Autowired
    private OverviewService overviewService;

    @GetMapping("/overview")
    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @Operation(summary = "Get account overview", description = "Retrieve customer account details including balance and account type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Account overview retrieved successfully",
                    content = @Content(schema = @Schema(implementation = OverviewResponse.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized - Invalid or missing token"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden - Insufficient privileges")
    })
    //    @ApiResponse(responseCode = "404",
    //    description = "Customer not found") - can be used, but with the current design, if a jwt token was generated
    //    the user is there, so any call with give an overview.
    public ResponseEntity<OverviewResponse> getOverview(Authentication authentication) {
        String username = authentication.getName();
        OverviewResponse overview = overviewService.getAccountOverview(username);
        return ResponseEntity.ok(overview);
    }
}