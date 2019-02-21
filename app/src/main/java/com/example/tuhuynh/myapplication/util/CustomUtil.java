package com.example.tuhuynh.myapplication.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomUtil {


    /**
     * Check string has meaning value or not
     *
     * @param str String
     * @return true if string is meaning
     */
    public static boolean hasMeaning(String str) {
        return !TextUtils.isEmpty(str) && !str.equalsIgnoreCase("null");
    }

    /**
     * Check username is correct format
     *
     * @param username username
     * @return true if username is correct format
     */
    public static boolean isIncorrectUsername(String username) {
        Pattern p = Pattern.compile("[a-z][A-Za-z0-9]{5,16}");
        Matcher m = p.matcher(username);
        return !m.matches();
    }

    /**
     * Check name is correct format
     *
     * @param name name
     * @return true if name is correct format
     */
    public static boolean isIncorrectName(String name) {
        Pattern p = Pattern.compile("[A-Za-z\\s]{2,30}");
        Matcher m = p.matcher(name);
        return !m.matches();
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

    /**
     * Convert String to Date as the format dd-MM-yyyy hh:mm:ss
     *
     * @param str String of date
     * @return Date
     */
    @SuppressLint("SimpleDateFormat")
    public static Date convertStringToDate(String str, String format) {
        SimpleDateFormat formatter;
        Date date = null;
        if (format.equalsIgnoreCase("default")) {
            formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        } else {
            formatter = new SimpleDateFormat(format);
        }
        try {
            date = formatter.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * Convert Date to String as the format dd-MM-yyyy hh:mm:ss
     *
     * @param date date
     * @return String
     */
    @SuppressLint("SimpleDateFormat")
    public static String convertDateToString(Date date, String format) {
        SimpleDateFormat formatter;
        if (format.equalsIgnoreCase("default")) {
            formatter = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        } else {
            formatter = new SimpleDateFormat(format);
        }
        return formatter.format(date);
    }

    /**
     * Capital first letter
     *
     * @param str String
     * @return String with capital of first letter
     */
    public static String capitalFirstLetter(String str) {
        StringBuilder name = new StringBuilder();
        StringTokenizer tokenizer = new StringTokenizer(str, " ");
        while (tokenizer.hasMoreTokens()) {
            String temp = tokenizer.nextToken();
            name.append(temp.substring(0, 1).toUpperCase()).append(temp.substring(1).toLowerCase()).append(" ");
        }
        return name.toString();
    }

    /**
     * Set full name
     *
     * @param firstName first name
     * @param surname   surname
     * @return Full name
     */
    public static String setFullName(String firstName, String surname) {
        String fullName = "";
        if (hasMeaning(firstName)) {
            fullName = firstName;
            if (hasMeaning(surname)) {
                fullName += " " + surname;
            }
        }
        return fullName;
    }

    public static boolean isNullorEmpty(String text) {
        return text == null || text.isEmpty();
    }

}
