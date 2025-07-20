package com.xyzbank.digital_onboarding_api.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class IbanGenerator {

    private static final String COUNTRY_CODE = "NL";
    private static final String BANK_CODE = "ANDR"; // My bank code
    private static final Random random = new Random();

    public String generateNLIban(Long accountId) {
        // format: NLkk bbbb cccc cccc cc
        // pad with 0 the beginning -> no needs to recheck the db for valid no since accountId is unique
        String accountNumber = String.format("%010d", accountId);

        // iban withoug the kk part
        String ibanWithoutCheckDigits = COUNTRY_CODE + "00" + BANK_CODE + accountNumber;

        // calculate kk
        String checkDigits = calculateCheckDigits(ibanWithoutCheckDigits);

        return COUNTRY_CODE + checkDigits + BANK_CODE + accountNumber;
    }

    // follows the wikipedia Algorithms/Validating the IBAN
    // https://en.wikipedia.org/wiki/International_Bank_Account_Number
    private String calculateCheckDigits(String iban) {

        String rearranged = iban.substring(4) + iban.substring(0, 4);

        // replace letters with numbers (A=10, B=11, ..., Z=35)
        StringBuilder numericString = new StringBuilder();
        for (char c : rearranged.toCharArray()) {
            if (Character.isLetter(c)) {
                int value = Character.toUpperCase(c) - 'A' + 10;
                numericString.append(value);
            } else {
                numericString.append(c);
            }
        }

        int remainder = mod97(numericString.toString());

        int checkDigits = 98 - remainder;

        return String.format("%02d", checkDigits);
    }

    private int mod97(String numericString) {
        int remainder = 0;
        for (char digit : numericString.toCharArray()) {
            remainder = (remainder * 10 + Character.getNumericValue(digit)) % 97;
        }
        return remainder;
    }
}