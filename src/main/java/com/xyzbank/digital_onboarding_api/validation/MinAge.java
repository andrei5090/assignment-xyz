package com.xyzbank.digital_onboarding_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import java.time.LocalDate;
import java.time.Period;

@Constraint(validatedBy = MinAge.MinAgeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MinAge {
    String message() default "Customer must be at least {value} years old";
    int value() default 18;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    class MinAgeValidator implements ConstraintValidator<MinAge, LocalDate> {
        
        private int minAge;
        
        @Override
        public void initialize(MinAge constraintAnnotation) {
            this.minAge = constraintAnnotation.value();
        }
        
        @Override
        public boolean isValid(LocalDate dateOfBirth, ConstraintValidatorContext context) {
            if (dateOfBirth == null) {
                return false;
            }
            
            LocalDate now = LocalDate.now();
            Period age = Period.between(dateOfBirth, now);
            
            return age.getYears() >= minAge;
        }
    }
}