package com.xyzbank.digital_onboarding_api.validation;

import com.xyzbank.digital_onboarding_api.enums.Country;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = ValidIban.IbanValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIban {
    String message() default "Invalid IBAN format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class IbanValidator implements ConstraintValidator<ValidIban, String> {
        
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
                return country.isValidIban(cleanIban);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }
}