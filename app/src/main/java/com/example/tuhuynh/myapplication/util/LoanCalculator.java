package com.example.tuhuynh.myapplication.util;

public class LoanCalculator {

    /**
     *
     */
    public static double calculateInterest(double amount, double interest) {
        return (amount * interest) / 1200;
    }

    /**
     *
     */
    public static Long calculatePrincipal(long month, long amount) {
        return amount / month;
    }


}
