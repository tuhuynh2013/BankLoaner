package com.example.tuhuynh.myapplication.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomUtil {

    /**
     * Check username is correct format
     *
     * @param username username
     * @return true if username is correct format
     */
    public static boolean isCorrectUsername(String username) {
        Pattern p = Pattern.compile("[a-z][A-Za-z0-9]{8,16}");
        Matcher m = p.matcher(username);
        return m.matches();
    }

    /**
     * Check name is correct format
     *
     * @param name name
     * @return true if name is correct format
     */
    public static boolean isCorrectName(String name) {
        Pattern p = Pattern.compile("[A-Za-z\\s]{2,30}");
        Matcher m = p.matcher(name);
        return m.matches();
    }

    /**
     * Check identity number is correct format
     *
     * @param identityNumber identity number
     * @return true if identity number is correct format
     */
    public static boolean isCorrectIdentity(String identityNumber) {
        Pattern p = Pattern.compile("[0-9]{9}");
        Matcher m = p.matcher(identityNumber);
        return m.matches();
    }

    /**
     * Check phone number is correct format
     *
     * @param phone phone number
     * @return true if phone number is correct format
     */
    public static boolean isCorrectPhone(String phone) {
        Pattern p = Pattern.compile("[0-9]{10,11}");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    /**
     * Convert long to string with the format #,###,###,###
     *
     * @param l long
     * @return String
     */
    public static String convertLongToFormattedString(long l) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        formatter.applyPattern("#,###,###,###");
        return formatter.format(l);
    }

    /**
     * Convert formatted string to long number
     *
     * @param s formatted string
     * @return long
     */
    public static Long convertFormattedStringToLong(String s) {
        if (!s.isEmpty()) {
            return Long.parseLong(s.replace(",", ""));
        } else {
            return null;
        }
    }


}
