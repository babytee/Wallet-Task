package com.walletsystem.utilities;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RequiredArgsConstructor
@Component
public class Utility {


    private static final String REFERENCE_NUMBER_PREFIX = "test";
    private static final String ACCOUNT_REFERENCE_PREFIX = "012";

    private static final Random RANDOM = new Random();

    public static String generateReferenceNumber() {
        return REFERENCE_NUMBER_PREFIX + generateRandomDigits(5) + generateRandomAlphabets(2) + generateRandomDigits(1);
    }

    public static String generateAccountReference() {
        return ACCOUNT_REFERENCE_PREFIX + generateRandomDigits(9);
    }

    private static String generateRandomDigits(int length) {
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < length; i++) {
            digits.append(RANDOM.nextInt(10));
        }
        return digits.toString();
    }

    private static String generateRandomAlphabets(int length) {
        StringBuilder alphabets = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char letter = (char) ('a' + RANDOM.nextInt(26));
            alphabets.append(letter);
        }
        return alphabets.toString();
    }


    public boolean isInputValid(String input, String regex) {
        return Pattern.compile(regex).matcher(input).matches();
    }

    public boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }

    public boolean isValidEmail(String email) {
        // Define the regular expression pattern for a basic email address
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        // Create a Pattern object
        Pattern pattern = Pattern.compile(emailRegex);

        // Create a Matcher object
        Matcher matcher = pattern.matcher(email);

        // Return true if the email matches the pattern
        return matcher.matches();
    }


    public double roundAmount(double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(2, RoundingMode.HALF_UP);

        // Ensure the double value has two decimal places by formatting it as a string and parsing it back
        return Double.parseDouble(String.format("%.2f", bd.doubleValue()));
    }

    public boolean isNigerianPhoneNumber(String phoneNumber) {
        // Regular expression for Nigerian phone number
        String regex = "^(\\+234|0)([789][01]\\d{8})$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }
}
