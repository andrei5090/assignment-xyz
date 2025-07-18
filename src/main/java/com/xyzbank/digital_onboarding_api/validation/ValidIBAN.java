package com.xyzbank.digital_onboarding_api.validation;

import com.xyzbank.digital_onboarding_api.enums.Country;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = ValidIBAN.IbanValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIBAN {
    String message() default "Invalid IBAN format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class IbanValidator implements ConstraintValidator<ValidIBAN, String> {
        
        @Override
        public boolean isValid(String iban, ConstraintValidatorContext context) {
            if (iban == null || iban.trim().isEmpty()) {
                return false;
            }
            
            String cleanIban = iban.replaceAll("\\s+", "").trim();
            
            if (cleanIban.length() < 4) {
                return false;
            }
            
            String countryCode = cleanIban.substring(0, 2);
            
            try {
                Country country = Country.fromCode(countryCode);
                // Check format first
                if (!country.isValidIban(cleanIban)) {
                    return false;
                }
                
                // Then validate check digits using mod 97
                return validateCheckDigits(cleanIban);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        
        private boolean validateCheckDigits(String iban) {
            String rearranged = iban.substring(4) + iban.substring(0, 4);
            
            // Replace letters with numbers (A=10, B=11, ..., Z=35)
            StringBuilder numericString = new StringBuilder();
            for (char c : rearranged.toCharArray()) {
                if (Character.isLetter(c)) {
                    numericString.append(Character.getNumericValue(c));
                } else {
                    numericString.append(c);
                }
            }

            int remainder = mod97(numericString.toString());
            return remainder == 1;
        }
        
        private int mod97(String numericString) {
            int remainder = 0;
            for (char digit : numericString.toCharArray()) {
                remainder = (remainder * 10 + Character.getNumericValue(digit)) % 97;
            }
            return remainder;
        }
    }
}