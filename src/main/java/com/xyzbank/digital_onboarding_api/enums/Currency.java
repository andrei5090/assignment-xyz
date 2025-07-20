package com.xyzbank.digital_onboarding_api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Currency {
    EUR("Euro", "€", 2);

    private final String displayName;
    private final String symbol;
    private final int decimalPlaces;

    @Override
    public String toString() {
        return this.name();
    }
}