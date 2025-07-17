package com.xyzbank.digital_onboarding_api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Country {
    // Netherlands: NLkk bbbb cccc cccc cc (18 chars: NL + 2 check digits + 4 letters + 10 digits)
    NL("Netherlands", "^NL\\d{2}[A-Z]{4}\\d{10}$"),
    // Belgium: BEkk bbbc cccc ccxx (16 chars: BE + 2 check digits + 12 digits)
    BE("Belgium", "^BE\\d{2}\\d{12}$");
    
    private final String displayName;
    private final String ibanPattern;
    
    public static Country fromCode(String code) {
        if (code == null) {
            return null;
        }
        
        for (Country country : values()) {
            if (country.name().equalsIgnoreCase(code)) {
                return country;
            }
        }
        
        throw new IllegalArgumentException("Unknown country code: " + code);
    }
    
    public boolean isValidIban(String iban) {
        if (iban == null || iban.trim().isEmpty()) {
            return false;
        }
        
        return iban.matches(this.ibanPattern);
    }
    
    @Override
    public String toString() {
        return this.name();
    }
}