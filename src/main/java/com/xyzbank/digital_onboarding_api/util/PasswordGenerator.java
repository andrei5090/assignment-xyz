package com.xyzbank.digital_onboarding_api.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class PasswordGenerator {

    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!?!-_";
    private static final int DEFAULT_LENGTH = 12;
    private static final SecureRandom random = new SecureRandom();

    public String generatePassword() {
        return generatePassword(DEFAULT_LENGTH);
    }

    public String generatePassword(int length) {
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(randomIndex));
        }

        return password.toString();
    }
}