package com.xyzbank.digital_onboarding_api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountType {
    CHECKING("Checking Account", "Current account for daily transactions"),
    SAVINGS("Savings Account", "Account for saving money");
    
    private final String displayName;
    private final String description;
    
    @Override
    public String toString() {
        return this.name();
    }
}