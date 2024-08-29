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

    public String generateUniqueId() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String timestamp = dateFormat.format(new Date());
        Random random = new Random();
        int randomNum = random.nextInt(1000);
        String uniqueId = timestamp + String.format("%03d", randomNum);
        return uniqueId;
    }

    public String generateUniqueRefNumber() {
        // You can customize the format of the invoice reference number based on your requirements
        // For example, using the current timestamp and a random UUID
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8); // Use a portion of the UUID

        return "wa-" + timestamp + "-" + uuid;
    }

    public  String generateReferenceNumber() {
        // Generate a random UUID and convert it to a string
        String uuid = UUID.randomUUID().toString().replace("-", ""); // Remove hyphens

        // Ensure the length is between 12 and 30 characters
        if (uuid.length() < 12) {
            // Append random numbers to ensure minimum length
            uuid = String.format("%-12s", uuid).replace(' ', '0');
        } else if (uuid.length() > 30) {
            // Truncate to ensure maximum length
            uuid = uuid.substring(0, 30);
        }

        return uuid;
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
